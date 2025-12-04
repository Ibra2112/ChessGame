package network;

import board.Board;
import board.Position;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

/**
 * Client class for connecting to a chess server and playing network games.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class ChessClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isWhitePlayer;
    private boolean connected;
    private NetworkMessageListener listener;
    
    /**
     * Interface for receiving network messages.
     */
    public interface NetworkMessageListener {
        void onMoveReceived(Position from, Position to);
        void onBoardUpdate(boolean isWhiteTurn);
        void onBoardReceived(Board board);
        void onGameOver(String message);
        void onError(String error);
        void onCheck();
        void onCheckmate();
        void onDisconnected();
    }
    
    /**
     * Constructor for ChessClient.
     * 
     * @param host the server hostname or IP address
     * @param port the server port
     * @param listener the message listener
     * @throws IOException if connection fails
     */
    public ChessClient(String host, int port, NetworkMessageListener listener) throws IOException {
        this.listener = listener;
        this.connected = false;
        
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
            // Start listening thread
            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            connected = false;
            throw e;
        }
    }
    
    /**
     * Listens for messages from the server.
     */
    private void listenForMessages() {
        try {
            while (connected && !socket.isClosed()) {
                NetworkMessage message = (NetworkMessage) in.readObject();
                
                switch (message.getType()) {
                    case CONNECTED:
                        isWhitePlayer = message.isWhitePlayer();
                        listener.onBoardUpdate(true); // Initial board state
                        break;
                    case MOVE:
                        listener.onMoveReceived(message.getFrom(), message.getTo());
                        break;
                    case BOARD_UPDATE:
                        listener.onBoardUpdate(message.isWhiteTurn());
                        if (message.getBoard() != null) {
                            listener.onBoardReceived(message.getBoard());
                        }
                        break;
                    case GAME_OVER:
                        listener.onGameOver(message.getMessage());
                        connected = false;
                        break;
                    case ERROR:
                        listener.onError(message.getMessage());
                        break;
                    case CHECK:
                        listener.onCheck();
                        break;
                    case CHECKMATE:
                        listener.onCheckmate();
                        break;
                    case DISCONNECT:
                        connected = false;
                        listener.onDisconnected();
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                listener.onDisconnected();
                connected = false;
            }
        }
    }
    
    /**
     * Sends a move to the server.
     * 
     * @param from starting position
     * @param to destination position
     */
    public void sendMove(Position from, Position to) {
        if (!connected) return;
        
        try {
            NetworkMessage moveMsg = NetworkMessage.createMove(from, to);
            out.writeObject(moveMsg);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error sending move: " + e.getMessage());
            listener.onError("Failed to send move: " + e.getMessage());
        }
    }
    
    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        connected = false;
        try {
            if (out != null) {
                NetworkMessage disconnectMsg = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT);
                out.writeObject(disconnectMsg);
                out.flush();
            }
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks if the client is connected.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connected && !socket.isClosed();
    }
    
    /**
     * Gets whether this client is playing as white.
     * 
     * @return true if white player, false if black
     */
    public boolean isWhitePlayer() {
        return isWhitePlayer;
    }
}


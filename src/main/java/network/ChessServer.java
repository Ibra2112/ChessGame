package network;

import board.Board;
import board.Position;
import pieces.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Server class for hosting network chess games.
 * Handles two clients playing against each other.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class ChessServer {
    private static final int DEFAULT_PORT = 8888;
    private ServerSocket serverSocket;
    private Socket whiteSocket;
    private Socket blackSocket;
    private ObjectOutputStream whiteOut;
    private ObjectOutputStream blackOut;
    private ObjectInputStream whiteIn;
    private ObjectInputStream blackIn;
    private Board board;
    private boolean isWhiteTurn;
    private boolean gameOver;
    private Thread whiteThread;
    private Thread blackThread;
    
    /**
     * Constructor for ChessServer.
     * 
     * @param port the port to listen on
     * @throws IOException if server socket creation fails
     */
    public ChessServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.board = new Board();
        this.isWhiteTurn = true;
        this.gameOver = false;
        System.out.println("Chess Server started on port " + port);
        System.out.println("Waiting for players to connect...");
    }
    
    /**
     * Starts the server and waits for two clients to connect.
     */
    public void start() {
        try {
            // Wait for white player
            System.out.println("Waiting for White player...");
            whiteSocket = serverSocket.accept();
            whiteOut = new ObjectOutputStream(whiteSocket.getOutputStream());
            whiteIn = new ObjectInputStream(whiteSocket.getInputStream());
            System.out.println("White player connected from: " + whiteSocket.getRemoteSocketAddress());
            
            // Send confirmation to white player
            NetworkMessage connectMsg = new NetworkMessage(NetworkMessage.MessageType.CONNECTED);
            connectMsg.setWhitePlayer(true);
            whiteOut.writeObject(connectMsg);
            whiteOut.flush();
            
            // Wait for black player
            System.out.println("Waiting for Black player...");
            blackSocket = serverSocket.accept();
            blackOut = new ObjectOutputStream(blackSocket.getOutputStream());
            blackIn = new ObjectInputStream(blackSocket.getInputStream());
            System.out.println("Black player connected from: " + blackSocket.getRemoteSocketAddress());
            
            // Send confirmation to black player
            connectMsg = new NetworkMessage(NetworkMessage.MessageType.CONNECTED);
            connectMsg.setWhitePlayer(false);
            blackOut.writeObject(connectMsg);
            blackOut.flush();
            
            // Send initial board state to both players
            sendBoardUpdate();
            
            // Start threads to handle each client
            whiteThread = new Thread(() -> handleClient(true));
            blackThread = new Thread(() -> handleClient(false));
            
            whiteThread.start();
            blackThread.start();
            
            System.out.println("Both players connected. Game started!");
            
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles communication with a client.
     * 
     * @param isWhite true if handling white player, false for black
     */
    private void handleClient(boolean isWhite) {
        ObjectInputStream in = isWhite ? whiteIn : blackIn;
        ObjectOutputStream out = isWhite ? whiteOut : blackOut;
        
        try {
            while (!gameOver && !Thread.currentThread().isInterrupted()) {
                NetworkMessage message = (NetworkMessage) in.readObject();
                
                if (message.getType() == NetworkMessage.MessageType.MOVE) {
                    handleMove(message, isWhite);
                } else if (message.getType() == NetworkMessage.MessageType.DISCONNECT) {
                    System.out.println((isWhite ? "White" : "Black") + " player disconnected");
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println((isWhite ? "White" : "Black") + " player disconnected: " + e.getMessage());
        } finally {
            closeConnections();
        }
    }
    
    /**
     * Handles a move from a client.
     * 
     * @param message the move message
     * @param isWhite true if move is from white player
     */
    private void handleMove(NetworkMessage message, boolean isWhite) {
        // Check if it's the correct player's turn
        if (isWhite != isWhiteTurn) {
            try {
                NetworkMessage errorMsg = NetworkMessage.createError("Not your turn!");
                (isWhite ? whiteOut : blackOut).writeObject(errorMsg);
                (isWhite ? whiteOut : blackOut).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        Position from = message.getFrom();
        Position to = message.getTo();
        
        Piece piece = board.getPiece(from);
        if (piece == null || piece.isWhite() != isWhite) {
            try {
                NetworkMessage errorMsg = NetworkMessage.createError("Invalid piece!");
                (isWhite ? whiteOut : blackOut).writeObject(errorMsg);
                (isWhite ? whiteOut : blackOut).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Validate move
        if (!piece.isValidMove(to, board.getSquares())) {
            try {
                NetworkMessage errorMsg = NetworkMessage.createError("Invalid move!");
                (isWhite ? whiteOut : blackOut).writeObject(errorMsg);
                (isWhite ? whiteOut : blackOut).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Check if move would put own king in check
        Board testBoard = board.copy();
        testBoard.movePiece(from, to);
        if (testBoard.isCheck(isWhite)) {
            try {
                NetworkMessage errorMsg = NetworkMessage.createError("Move would put your king in check!");
                (isWhite ? whiteOut : blackOut).writeObject(errorMsg);
                (isWhite ? whiteOut : blackOut).flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Make the move
        board.movePiece(from, to);
        
        // Check for pawn promotion (server handles automatically - promotes to Queen)
        if (piece instanceof Pawn) {
            int promotionRow = isWhite ? 0 : 7;
            if (to.getRow() == promotionRow) {
                Piece newPiece = new Queen(isWhite, to);
                board.getSquares()[to.getRow()][to.getColumn()] = newPiece;
            }
        }
        
        // Switch turns
        isWhiteTurn = !isWhiteTurn;
        
        // Check for game over conditions
        boolean checkDetected = board.isCheck(!isWhite);
        boolean checkmateDetected = board.isCheckmate(!isWhite);
        
        if (checkmateDetected) {
            gameOver = true;
            String winner = isWhite ? "White" : "Black";
            try {
                NetworkMessage gameOverMsg = NetworkMessage.createGameOver("CHECKMATE! " + winner + " wins!");
                whiteOut.writeObject(gameOverMsg);
                whiteOut.flush();
                blackOut.writeObject(gameOverMsg);
                blackOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Send board update to both players
            sendBoardUpdate();
            
            // Send check notification if applicable
            if (checkDetected) {
                try {
                    NetworkMessage checkMsg = new NetworkMessage(NetworkMessage.MessageType.CHECK);
                    whiteOut.writeObject(checkMsg);
                    whiteOut.flush();
                    blackOut.writeObject(checkMsg);
                    blackOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Sends board update to both clients.
     */
    private void sendBoardUpdate() {
        try {
            Board boardCopy = board.copy();
            NetworkMessage updateMsg = NetworkMessage.createBoardUpdate(boardCopy, isWhiteTurn);
            updateMsg.setWhitePlayer(true);
            whiteOut.writeObject(updateMsg);
            whiteOut.flush();
            
            updateMsg = NetworkMessage.createBoardUpdate(boardCopy, isWhiteTurn);
            updateMsg.setWhitePlayer(false);
            blackOut.writeObject(updateMsg);
            blackOut.flush();
        } catch (IOException e) {
            System.err.println("Error sending board update: " + e.getMessage());
        }
    }
    
    /**
     * Closes all connections.
     */
    private void closeConnections() {
        try {
            if (whiteSocket != null) whiteSocket.close();
            if (blackSocket != null) blackSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the current board state.
     * 
     * @return a copy of the board
     */
    public Board getBoard() {
        return board.copy();
    }
    
    /**
     * Main method to run the server.
     * 
     * @param args command line arguments (optional port number)
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default port " + DEFAULT_PORT);
            }
        }
        
        try {
            ChessServer server = new ChessServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


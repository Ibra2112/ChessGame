package network;

import board.Board;
import board.Position;
import java.io.Serializable;

/**
 * Represents a message sent over the network between client and server.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class NetworkMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        CONNECT,           // Client connecting
        CONNECTED,         // Server confirms connection
        MOVE,              // Move made
        BOARD_UPDATE,      // Board state update
        GAME_OVER,         // Game ended
        DISCONNECT,        // Player disconnected
        ERROR,             // Error message
        TURN_SWITCH,       // Turn switched
        CHECK,             // Check detected
        CHECKMATE          // Checkmate detected
    }
    
    private MessageType type;
    private Position from;
    private Position to;
    private boolean isWhiteTurn;
    private String message;
    private boolean isWhitePlayer; // Which player this message is for
    private Board board; // Board state for updates
    
    /**
     * Constructor for NetworkMessage.
     * 
     * @param type the message type
     */
    public NetworkMessage(MessageType type) {
        this.type = type;
    }
    
    /**
     * Creates a move message.
     * 
     * @param from starting position
     * @param to destination position
     * @return NetworkMessage for move
     */
    public static NetworkMessage createMove(Position from, Position to) {
        NetworkMessage msg = new NetworkMessage(MessageType.MOVE);
        msg.from = from;
        msg.to = to;
        return msg;
    }
    
    /**
     * Creates a board update message.
     * 
     * @param board the current board state
     * @param isWhiteTurn true if it's white's turn
     * @return NetworkMessage for board update
     */
    public static NetworkMessage createBoardUpdate(Board board, boolean isWhiteTurn) {
        NetworkMessage msg = new NetworkMessage(MessageType.BOARD_UPDATE);
        msg.board = board;
        msg.isWhiteTurn = isWhiteTurn;
        return msg;
    }
    
    public Board getBoard() {
        return board;
    }
    
    /**
     * Creates a game over message.
     * 
     * @param message the game over message
     * @return NetworkMessage for game over
     */
    public static NetworkMessage createGameOver(String message) {
        NetworkMessage msg = new NetworkMessage(MessageType.GAME_OVER);
        msg.message = message;
        return msg;
    }
    
    /**
     * Creates an error message.
     * 
     * @param message the error message
     * @return NetworkMessage for error
     */
    public static NetworkMessage createError(String message) {
        NetworkMessage msg = new NetworkMessage(MessageType.ERROR);
        msg.message = message;
        return msg;
    }
    
    // Getters and setters
    public MessageType getType() {
        return type;
    }
    
    public Position getFrom() {
        return from;
    }
    
    public Position getTo() {
        return to;
    }
    
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isWhitePlayer() {
        return isWhitePlayer;
    }
    
    public void setWhitePlayer(boolean isWhitePlayer) {
        this.isWhitePlayer = isWhitePlayer;
    }
}


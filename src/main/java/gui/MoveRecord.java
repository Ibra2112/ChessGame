package gui;

import board.Board;
import board.Position;
import pieces.Piece;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a move record for tracking game history.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class MoveRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Position from;
    private Position to;
    private String pieceType;
    private boolean pieceIsWhite;
    private String capturedPieceType;
    private boolean capturedPieceIsWhite;
    
    /**
     * Constructor for MoveRecord.
     * 
     * @param from the starting position
     * @param to the destination position
     * @param piece the piece that moved
     * @param capturedPiece the captured piece, or null if none
     */
    public MoveRecord(Position from, Position to, Piece piece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.pieceType = piece.getClass().getSimpleName();
        this.pieceIsWhite = piece.isWhite();
        
        if (capturedPiece != null) {
            this.capturedPieceType = capturedPiece.getClass().getSimpleName();
            this.capturedPieceIsWhite = capturedPiece.isWhite();
        } else {
            this.capturedPieceType = null;
            this.capturedPieceIsWhite = false;
        }
    }
    
    /**
     * Gets the starting position.
     * 
     * @return the starting position
     */
    public Position getFrom() {
        return from;
    }
    
    /**
     * Gets the destination position.
     * 
     * @return the destination position
     */
    public Position getTo() {
        return to;
    }
    
    /**
     * Gets the piece that moved.
     * Note: This creates a new piece instance based on stored information.
     * 
     * @return the piece
     */
    public Piece getPiece() {
        return createPiece(pieceType, pieceIsWhite, from);
    }
    
    /**
     * Gets the captured piece, if any.
     * 
     * @return the captured piece, or null if none
     */
    public Piece getCapturedPiece() {
        if (capturedPieceType == null) {
            return null;
        }
        return createPiece(capturedPieceType, capturedPieceIsWhite, to);
    }
    
    /**
     * Creates a piece instance from type information.
     * 
     * @param pieceType the piece type name
     * @param isWhite true if white, false if black
     * @param position the position
     * @return the piece instance
     */
    private Piece createPiece(String pieceType, boolean isWhite, Position position) {
        try {
            Class<?> pieceClass = Class.forName("pieces." + pieceType);
            return (Piece) pieceClass.getConstructor(boolean.class, Position.class)
                .newInstance(isWhite, position);
        } catch (Exception e) {
            throw new RuntimeException("Error creating piece: " + pieceType, e);
        }
    }
}


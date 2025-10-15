package pieces;

import board.Position;
import java.util.List;

/**
 * Abstract class representing a chess piece.
 * This class defines common attributes and methods for all chess pieces.
 * 
 * @author Chess Game
 * @version 1.0
 */
public abstract class Piece {
    protected boolean isWhite;
    protected Position position;
    protected boolean hasMoved;

    /**
     * Constructor for Piece class.
     * 
     * @param isWhite true if the piece is white, false if black
     * @param position the initial position of the piece
     */
    public Piece(boolean isWhite, Position position) {
        this.isWhite = isWhite;
        this.position = position;
        this.hasMoved = false;
    }

    /**
     * Gets the color of the piece.
     * 
     * @return true if the piece is white, false if black
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Gets the current position of the piece.
     * 
     * @return the current position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the piece.
     * 
     * @param position the new position
     */
    public void setPosition(Position position) {
        this.position = position;
        this.hasMoved = true;
    }

    /**
     * Checks if the piece has moved from its initial position.
     * 
     * @return true if the piece has moved, false otherwise
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Abstract method to get all possible moves for this piece.
     * Each piece type implements its own movement rules.
     * 
     * @param board the current board state
     * @return a list of valid positions the piece can move to
     */
    public abstract List<Position> possibleMoves(Piece[][] board);

    /**
     * Checks if a move to the given position is valid for this piece.
     * 
     * @param targetPosition the position to move to
     * @param board the current board state
     * @return true if the move is valid, false otherwise
     */
    public abstract boolean isValidMove(Position targetPosition, Piece[][] board);

    /**
     * Gets the string representation of the piece.
     * 
     * @return string representation (e.g., "wP" for white pawn, "bK" for black king)
     */
    public abstract String toString();

    /**
     * Creates a copy of this piece.
     * 
     * @return a new instance of the same piece type
     */
    public abstract Piece copy();
}

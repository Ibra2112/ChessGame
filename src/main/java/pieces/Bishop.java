package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bishop piece in chess.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Bishop extends Piece {

    /**
     * Constructor for Bishop class.
     * 
     * @param isWhite true if the bishop is white, false if black
     * @param position the initial position of the bishop
     */
    public Bishop(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    /**
     * Gets all possible moves for the bishop.
     * 
     * @param board the current board state
     * @return a list of valid positions the bishop can move to
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        
        // Diagonal directions
        int[][] directions = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        
        for (int[] direction : directions) {
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];
            
            while (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece targetPiece = board[newRow][newCol];
                
                if (targetPiece == null) {
                    // Empty square - can move here
                    moves.add(new Position(newRow, newCol));
                } else {
                    // Occupied square
                    if (targetPiece.isWhite() != isWhite) {
                        // Enemy piece - can capture
                        moves.add(new Position(newRow, newCol));
                    }
                    break; // Can't move past any piece
                }
                
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        
        return moves;
    }

    /**
     * Checks if a move to the given position is valid for the bishop.
     * 
     * @param targetPosition the position to move to
     * @param board the current board state
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean isValidMove(Position targetPosition, Piece[][] board) {
        List<Position> possibleMoves = possibleMoves(board);
        return possibleMoves.contains(targetPosition);
    }

    /**
     * Gets the string representation of the bishop.
     * 
     * @return "wB" for white bishop, "bB" for black bishop
     */
    @Override
    public String toString() {
        return isWhite ? "wB" : "bB";
    }

    /**
     * Creates a copy of this bishop.
     * 
     * @return a new Bishop instance with the same properties
     */
    @Override
    public Piece copy() {
        Bishop copy = new Bishop(isWhite, new Position(position.getRow(), position.getColumn()));
        copy.hasMoved = this.hasMoved;
        return copy;
    }
}

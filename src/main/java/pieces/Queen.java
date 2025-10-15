package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen piece in chess.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Queen extends Piece {

    /**
     * Constructor for Queen class.
     * 
     * @param isWhite true if the queen is white, false if black
     * @param position the initial position of the queen
     */
    public Queen(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    /**
     * Gets all possible moves for the queen.
     * The queen combines the moves of a rook and bishop.
     * 
     * @param board the current board state
     * @return a list of valid positions the queen can move to
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        
        // All directions: horizontal, vertical, and diagonal
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Rook moves
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Bishop moves
        };
        
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
     * Checks if a move to the given position is valid for the queen.
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
     * Gets the string representation of the queen.
     * 
     * @return "wQ" for white queen, "bQ" for black queen
     */
    @Override
    public String toString() {
        return isWhite ? "wQ" : "bQ";
    }

    /**
     * Creates a copy of this queen.
     * 
     * @return a new Queen instance with the same properties
     */
    @Override
    public Piece copy() {
        Queen copy = new Queen(isWhite, new Position(position.getRow(), position.getColumn()));
        copy.hasMoved = this.hasMoved;
        return copy;
    }
}

package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King piece in chess.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class King extends Piece {

    /**
     * Constructor for King class.
     * 
     * @param isWhite true if the king is white, false if black
     * @param position the initial position of the king
     */
    public King(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    /**
     * Gets all possible moves for the king.
     * 
     * @param board the current board state
     * @return a list of valid positions the king can move to
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        
        // King can move one square in any direction
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] direction : directions) {
            int newRow = position.getRow() + direction[0];
            int newCol = position.getColumn() + direction[1];
            
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece targetPiece = board[newRow][newCol];
                
                // Can move to empty square or capture enemy piece
                if (targetPiece == null || targetPiece.isWhite() != isWhite) {
                    moves.add(new Position(newRow, newCol));
                }
            }
        }
        
        return moves;
    }

    /**
     * Checks if a move to the given position is valid for the king.
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
     * Gets the string representation of the king.
     * 
     * @return "wK" for white king, "bK" for black king
     */
    @Override
    public String toString() {
        return isWhite ? "wK" : "bK";
    }

    /**
     * Creates a copy of this king.
     * 
     * @return a new King instance with the same properties
     */
    @Override
    public Piece copy() {
        King copy = new King(isWhite, new Position(position.getRow(), position.getColumn()));
        copy.hasMoved = this.hasMoved;
        return copy;
    }
}

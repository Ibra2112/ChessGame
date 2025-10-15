package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight piece in chess.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Knight extends Piece {

    /**
     * Constructor for Knight class.
     * 
     * @param isWhite true if the knight is white, false if black
     * @param position the initial position of the knight
     */
    public Knight(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    /**
     * Gets all possible moves for the knight.
     * 
     * @param board the current board state
     * @return a list of valid positions the knight can move to
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        
        // Knight moves in L-shape: 2 squares in one direction, then 1 square perpendicular
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        
        for (int[] move : knightMoves) {
            int newRow = position.getRow() + move[0];
            int newCol = position.getColumn() + move[1];
            
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
     * Checks if a move to the given position is valid for the knight.
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
     * Gets the string representation of the knight.
     * 
     * @return "wN" for white knight, "bN" for black knight
     */
    @Override
    public String toString() {
        return isWhite ? "wN" : "bN";
    }

    /**
     * Creates a copy of this knight.
     * 
     * @return a new Knight instance with the same properties
     */
    @Override
    public Piece copy() {
        Knight copy = new Knight(isWhite, new Position(position.getRow(), position.getColumn()));
        copy.hasMoved = this.hasMoved;
        return copy;
    }
}

package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn piece in chess.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Pawn extends Piece {

    /**
     * Constructor for Pawn class.
     * 
     * @param isWhite true if the pawn is white, false if black
     * @param position the initial position of the pawn
     */
    public Pawn(boolean isWhite, Position position) {
        super(isWhite, position);
    }

    /**
     * Gets all possible moves for the pawn.
     * 
     * @param board the current board state
     * @return a list of valid positions the pawn can move to
     */
    @Override
    public List<Position> possibleMoves(Piece[][] board) {
        List<Position> moves = new ArrayList<>();
        int direction = isWhite ? -1 : 1; // White pawns move up (decreasing row), black pawns move down (increasing row)
        int startRow = isWhite ? 6 : 1; // Starting row for pawns
        
        // Forward move (one square)
        int newRow = position.getRow() + direction;
        if (newRow >= 0 && newRow <= 7 && board[newRow][position.getColumn()] == null) {
            moves.add(new Position(newRow, position.getColumn()));
            
            // Forward move (two squares) - only from starting position
            if (position.getRow() == startRow) {
                newRow = position.getRow() + (2 * direction);
                if (newRow >= 0 && newRow <= 7 && board[newRow][position.getColumn()] == null) {
                    moves.add(new Position(newRow, position.getColumn()));
                }
            }
        }
        
        // Diagonal capture moves
        for (int colOffset = -1; colOffset <= 1; colOffset += 2) {
            int newCol = position.getColumn() + colOffset;
            newRow = position.getRow() + direction;
            
            if (newRow >= 0 && newRow <= 7 && newCol >= 0 && newCol <= 7) {
                Piece targetPiece = board[newRow][newCol];
                if (targetPiece != null && targetPiece.isWhite() != isWhite) {
                    moves.add(new Position(newRow, newCol));
                }
            }
        }
        
        return moves;
    }

    /**
     * Checks if a move to the given position is valid for the pawn.
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
     * Gets the string representation of the pawn.
     * 
     * @return "wP" for white pawn, "bP" for black pawn
     */
    @Override
    public String toString() {
        return isWhite ? "wP" : "bP";
    }

    /**
     * Creates a copy of this pawn.
     * 
     * @return a new Pawn instance with the same properties
     */
    @Override
    public Piece copy() {
        Pawn copy = new Pawn(isWhite, new Position(position.getRow(), position.getColumn()));
        copy.hasMoved = this.hasMoved;
        return copy;
    }
}

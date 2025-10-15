package utils;

import board.Position;
import pieces.Piece;

/**
 * Utility class containing helper functions for the chess game.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Utils {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Utils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Checks if a position is within the bounds of the chessboard.
     * 
     * @param row the row coordinate
     * @param col the column coordinate
     * @return true if the position is valid, false otherwise
     */
    public static boolean isValidPosition(int row, int col) {
        return row >= 0 && row <= 7 && col >= 0 && col <= 7;
    }

    /**
     * Checks if a position is within the bounds of the chessboard.
     * 
     * @param position the position to check
     * @return true if the position is valid, false otherwise
     */
    public static boolean isValidPosition(Position position) {
        return isValidPosition(position.getRow(), position.getColumn());
    }

    /**
     * Converts algebraic notation to row and column coordinates.
     * 
     * @param algebraicNotation the position in algebraic notation (e.g., "E4")
     * @return an array [row, col] with 0-based coordinates
     */
    public static int[] algebraicToCoordinates(String algebraicNotation) {
        if (algebraicNotation == null || algebraicNotation.length() != 2) {
            throw new IllegalArgumentException("Algebraic notation must be 2 characters");
        }
        
        char file = algebraicNotation.charAt(0);
        char rank = algebraicNotation.charAt(1);
        
        if (file < 'A' || file > 'H' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid algebraic notation");
        }
        
        int col = file - 'A';
        int row = 8 - (rank - '0');
        
        return new int[]{row, col};
    }

    /**
     * Converts row and column coordinates to algebraic notation.
     * 
     * @param row the row coordinate (0-7)
     * @param col the column coordinate (0-7)
     * @return the position in algebraic notation (e.g., "E4")
     */
    public static String coordinatesToAlgebraic(int row, int col) {
        if (!isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        
        char file = (char) ('A' + col);
        char rank = (char) ('0' + (8 - row));
        
        return String.valueOf(file) + String.valueOf(rank);
    }

    /**
     * Gets the piece symbol for display purposes.
     * 
     * @param piece the piece to get the symbol for
     * @return the piece symbol
     */
    public static String getPieceSymbol(Piece piece) {
        if (piece == null) {
            return "  ";
        }
        return piece.toString();
    }

    /**
     * Parses a move string in the format "FROM TO" (e.g., "E2 E4").
     * 
     * @param moveString the move string to parse
     * @return an array containing [from, to] positions
     * @throws IllegalArgumentException if the move string is invalid
     */
    public static Position[] parseMove(String moveString) {
        if (moveString == null || moveString.trim().isEmpty()) {
            throw new IllegalArgumentException("Move string cannot be null or empty");
        }
        
        String[] parts = moveString.trim().toUpperCase().split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Move must be in format 'FROM TO' (e.g., 'E2 E4')");
        }
        
        try {
            Position from = new Position(parts[0]);
            Position to = new Position(parts[1]);
            return new Position[]{from, to};
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid position in move: " + e.getMessage());
        }
    }

    /**
     * Checks if two positions are on the same diagonal.
     * 
     * @param pos1 the first position
     * @param pos2 the second position
     * @return true if the positions are on the same diagonal, false otherwise
     */
    public static boolean isSameDiagonal(Position pos1, Position pos2) {
        int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
        int colDiff = Math.abs(pos1.getColumn() - pos2.getColumn());
        return rowDiff == colDiff;
    }

    /**
     * Checks if two positions are on the same row.
     * 
     * @param pos1 the first position
     * @param pos2 the second position
     * @return true if the positions are on the same row, false otherwise
     */
    public static boolean isSameRow(Position pos1, Position pos2) {
        return pos1.getRow() == pos2.getRow();
    }

    /**
     * Checks if two positions are on the same column.
     * 
     * @param pos1 the first position
     * @param pos2 the second position
     * @return true if the positions are on the same column, false otherwise
     */
    public static boolean isSameColumn(Position pos1, Position pos2) {
        return pos1.getColumn() == pos2.getColumn();
    }

    /**
     * Gets the direction vector between two positions.
     * 
     * @param from the starting position
     * @param to the ending position
     * @return an array [rowDirection, colDirection] with values -1, 0, or 1
     */
    public static int[] getDirection(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getColumn() - from.getColumn();
        
        int rowDirection = rowDiff == 0 ? 0 : (rowDiff > 0 ? 1 : -1);
        int colDirection = colDiff == 0 ? 0 : (colDiff > 0 ? 1 : -1);
        
        return new int[]{rowDirection, colDirection};
    }
}

package board;

/**
 * Represents a position on the chessboard using row and column coordinates.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Position {
    private int row;
    private int column;

    /**
     * Constructor for Position class.
     * 
     * @param row the row coordinate (0-7, where 0 is rank 8 and 7 is rank 1)
     * @param column the column coordinate (0-7, where 0 is file A and 7 is file H)
     */
    public Position(int row, int column) {
        if (row < 0 || row > 7 || column < 0 || column > 7) {
            throw new IllegalArgumentException("Row and column must be between 0 and 7");
        }
        this.row = row;
        this.column = column;
    }

    /**
     * Constructor for Position class using algebraic notation (e.g., "E4").
     * 
     * @param algebraicNotation the position in algebraic notation
     */
    public Position(String algebraicNotation) {
        if (algebraicNotation == null || algebraicNotation.length() != 2) {
            throw new IllegalArgumentException("Algebraic notation must be 2 characters (e.g., 'E4')");
        }
        
        char file = algebraicNotation.charAt(0);
        char rank = algebraicNotation.charAt(1);
        
        // Convert file (A-H) to column (0-7)
        if (file < 'A' || file > 'H') {
            throw new IllegalArgumentException("File must be between A and H");
        }
        this.column = file - 'A';
        
        // Convert rank (1-8) to row (0-7)
        if (rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Rank must be between 1 and 8");
        }
        this.row = 8 - (rank - '0'); // Convert to 0-based indexing
    }

    /**
     * Gets the row coordinate.
     * 
     * @return the row coordinate (0-7)
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate.
     * 
     * @return the column coordinate (0-7)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Converts the position to algebraic notation.
     * 
     * @return the position in algebraic notation (e.g., "E4")
     */
    public String toAlgebraicNotation() {
        char file = (char) ('A' + column);
        char rank = (char) ('0' + (8 - row));
        return String.valueOf(file) + String.valueOf(rank);
    }

    /**
     * Checks if this position is equal to another position.
     * 
     * @param obj the object to compare
     * @return true if the positions are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && column == position.column;
    }

    /**
     * Generates a hash code for this position.
     * 
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return row * 8 + column;
    }

    /**
     * Returns the string representation of this position.
     * 
     * @return the position in algebraic notation
     */
    @Override
    public String toString() {
        return toAlgebraicNotation();
    }
}

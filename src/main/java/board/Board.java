package board;

import pieces.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the chessboard and manages piece positions and game state.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Board {
    private Piece[][] squares;
    private List<Piece> capturedPieces;

    /**
     * Constructor for Board class.
     * Initializes an empty 8x8 board.
     */
    public Board() {
        this.squares = new Piece[8][8];
        this.capturedPieces = new ArrayList<>();
        initializeBoard();
    }

    /**
     * Initializes the board with pieces in their starting positions.
     */
    private void initializeBoard() {
        // Initialize black pieces (rows 0-1)
        // Row 0 (black back rank)
        squares[0][0] = new Rook(false, new Position(0, 0));
        squares[0][1] = new Knight(false, new Position(0, 1));
        squares[0][2] = new Bishop(false, new Position(0, 2));
        squares[0][3] = new Queen(false, new Position(0, 3));
        squares[0][4] = new King(false, new Position(0, 4));
        squares[0][5] = new Bishop(false, new Position(0, 5));
        squares[0][6] = new Knight(false, new Position(0, 6));
        squares[0][7] = new Rook(false, new Position(0, 7));
        
        // Row 1 (black pawns)
        for (int col = 0; col < 8; col++) {
            squares[1][col] = new Pawn(false, new Position(1, col));
        }
        
        // Initialize white pieces (rows 6-7)
        // Row 6 (white pawns)
        for (int col = 0; col < 8; col++) {
            squares[6][col] = new Pawn(true, new Position(6, col));
        }
        
        // Row 7 (white back rank)
        squares[7][0] = new Rook(true, new Position(7, 0));
        squares[7][1] = new Knight(true, new Position(7, 1));
        squares[7][2] = new Bishop(true, new Position(7, 2));
        squares[7][3] = new Queen(true, new Position(7, 3));
        squares[7][4] = new King(true, new Position(7, 4));
        squares[7][5] = new Bishop(true, new Position(7, 5));
        squares[7][6] = new Knight(true, new Position(7, 6));
        squares[7][7] = new Rook(true, new Position(7, 7));
    }

    /**
     * Gets the piece at the specified position.
     * 
     * @param position the position to check
     * @return the piece at the position, or null if empty
     */
    public Piece getPiece(Position position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Moves a piece from one position to another.
     * 
     * @param from the starting position
     * @param to the destination position
     * @return true if the move was successful, false otherwise
     */
    public boolean movePiece(Position from, Position to) {
        Piece piece = getPiece(from);
        if (piece == null) {
            return false;
        }
        
        Piece capturedPiece = getPiece(to);
        if (capturedPiece != null) {
            capturedPieces.add(capturedPiece);
        }
        
        squares[from.getRow()][from.getColumn()] = null;
        squares[to.getRow()][to.getColumn()] = piece;
        piece.setPosition(to);
        
        return true;
    }

    /**
     * Checks if a given color is in check.
     * 
     * @param isWhite true if checking white, false if checking black
     * @return true if the color is in check, false otherwise
     */
    public boolean isCheck(boolean isWhite) {
        Position kingPosition = findKing(isWhite);
        if (kingPosition == null) {
            return false; // King not found (shouldn't happen in normal play)
        }
        
        // Check if any enemy piece can attack the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && piece.isWhite() != isWhite) {
                    if (piece.isValidMove(kingPosition, squares)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Checks if a given color is in checkmate.
     * 
     * @param isWhite true if checking white, false if checking black
     * @return true if the color is in checkmate, false otherwise
     */
    public boolean isCheckmate(boolean isWhite) {
        if (!isCheck(isWhite)) {
            return false; // Not in check, so not checkmate
        }
        
        // Check if any move can get out of check
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Position> possibleMoves = piece.possibleMoves(squares);
                    for (Position move : possibleMoves) {
                        // Try the move and see if it gets out of check
                        Piece originalPiece = getPiece(move);
                        Position originalPosition = piece.getPosition();
                        
                        // Make the move
                        squares[originalPosition.getRow()][originalPosition.getColumn()] = null;
                        squares[move.getRow()][move.getColumn()] = piece;
                        piece.setPosition(move);
                        
                        boolean stillInCheck = isCheck(isWhite);
                        
                        // Undo the move
                        squares[originalPosition.getRow()][originalPosition.getColumn()] = piece;
                        squares[move.getRow()][move.getColumn()] = originalPiece;
                        piece.setPosition(originalPosition);
                        
                        if (!stillInCheck) {
                            return false; // Found a move that gets out of check
                        }
                    }
                }
            }
        }
        
        return true; // No moves can get out of check
    }

    /**
     * Finds the position of the king of the specified color.
     * 
     * @param isWhite true if looking for white king, false for black king
     * @return the position of the king, or null if not found
     */
    private Position findKing(boolean isWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece instanceof King && piece.isWhite() == isWhite) {
                    return piece.getPosition();
                }
            }
        }
        return null;
    }

    /**
     * Displays the current state of the board in the console.
     * Shows # symbols on dark squares when empty, and pieces when occupied.
     */
    public void display() {
        System.out.println("\n   A  B  C  D  E  F  G  H");
        System.out.println("  +--+--+--+--+--+--+--+--+");
        
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " |");
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    // Check if this is a dark square (when row + col is odd)
                    if ((row + col) % 2 == 1) {
                        System.out.print("##|");
                    } else {
                        System.out.print("  |");
                    }
                } else {
                    System.out.print(piece.toString() + "|");
                }
            }
            System.out.println(" " + (8 - row));
        }
        
        System.out.println("  +--+--+--+--+--+--+--+--+");
        System.out.println("   A  B  C  D  E  F  G  H\n");
    }

    /**
     * Gets the list of captured pieces.
     * 
     * @return the list of captured pieces
     */
    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * Gets the squares array for validation purposes.
     * 
     * @return the 8x8 array of pieces
     */
    public Piece[][] getSquares() {
        return squares;
    }

    /**
     * Creates a deep copy of the board.
     * 
     * @return a new Board instance with copied pieces
     */
    public Board copy() {
        Board copy = new Board();
        copy.squares = new Piece[8][8];
        copy.capturedPieces = new ArrayList<>(this.capturedPieces);
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (squares[row][col] != null) {
                    copy.squares[row][col] = squares[row][col].copy();
                }
            }
        }
        
        return copy;
    }
}

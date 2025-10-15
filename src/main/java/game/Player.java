package game;

import board.Board;
import board.Position;
import pieces.Piece;
import java.util.Scanner;

/**
 * Represents a player in the chess game.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Player {
    private boolean isWhite;
    private String name;
    private Scanner scanner;

    /**
     * Constructor for Player class.
     * 
     * @param isWhite true if the player plays white pieces, false if black
     * @param name the name of the player
     */
    public Player(boolean isWhite, String name) {
        this.isWhite = isWhite;
        this.name = name;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Gets the color of the player.
     * 
     * @return true if the player plays white pieces, false if black
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Gets the name of the player.
     * 
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Prompts the player to make a move and returns the move.
     * 
     * @param board the current board state
     * @return an array containing [from, to] positions, or null if invalid move
     */
    public Position[] makeMove(Board board) {
        System.out.print(name + " (" + (isWhite ? "White" : "Black") + "), enter your move (e.g., E2 E4): ");
        String input = scanner.nextLine().trim().toUpperCase();
        
        if (input.equals("QUIT") || input.equals("EXIT")) {
            return null; // Signal to quit the game
        }
        
        try {
            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Invalid format. Please enter move as 'FROM TO' (e.g., E2 E4)");
                return makeMove(board); // Recursive call to try again
            }
            
            Position from = new Position(parts[0]);
            Position to = new Position(parts[1]);
            
            // Validate that the player is moving their own piece
            Piece piece = board.getPiece(from);
            if (piece == null) {
                System.out.println("No piece at " + from + ". Please try again.");
                return makeMove(board);
            }
            
            if (piece.isWhite() != isWhite) {
                System.out.println("That's not your piece! Please try again.");
                return makeMove(board);
            }
            
            // Validate the move
            if (!piece.isValidMove(to, board.getSquares())) {
                System.out.println("Invalid move for " + piece.toString() + ". Please try again.");
                return makeMove(board);
            }
            
            return new Position[]{from, to};
            
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position: " + e.getMessage() + ". Please try again.");
            return makeMove(board);
        }
    }

    /**
     * Prompts the player to choose a piece for pawn promotion.
     * 
     * @return the piece type as a string (Q, R, B, N)
     */
    public String choosePromotionPiece() {
        System.out.print("Pawn promotion! Choose piece (Q/R/B/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        while (!choice.equals("Q") && !choice.equals("R") && !choice.equals("B") && !choice.equals("N")) {
            System.out.print("Invalid choice. Please enter Q, R, B, or N: ");
            choice = scanner.nextLine().trim().toUpperCase();
        }
        
        return choice;
    }
}

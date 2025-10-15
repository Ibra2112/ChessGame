package game;

import board.Board;
import board.Position;
import pieces.*;
import java.util.Scanner;

/**
 * Main game class that orchestrates the chess game.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class Game {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private boolean isWhiteTurn;
    private boolean gameOver;
    private Scanner scanner;

    /**
     * Constructor for Game class.
     */
    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
        this.isWhiteTurn = true;
        this.gameOver = false;
        initializePlayers();
    }

    /**
     * Initializes the players by getting their names.
     */
    private void initializePlayers() {
        System.out.println("Welcome to Console Chess Game!");
        System.out.println("===============================");
        
        System.out.print("Enter name for White player: ");
        String whiteName = scanner.nextLine().trim();
        if (whiteName.isEmpty()) {
            whiteName = "White Player";
        }
        
        System.out.print("Enter name for Black player: ");
        String blackName = scanner.nextLine().trim();
        if (blackName.isEmpty()) {
            blackName = "Black Player";
        }
        
        this.whitePlayer = new Player(true, whiteName);
        this.blackPlayer = new Player(false, blackName);
        
        System.out.println("\nGame initialized!");
        System.out.println("White: " + whiteName);
        System.out.println("Black: " + blackName);
        System.out.println("\nEnter 'QUIT' at any time to exit the game.");
        System.out.println("Move format: FROM TO (e.g., E2 E4)");
    }

    /**
     * Starts the game and runs the main game loop.
     */
    public void start() {
        while (!gameOver) {
            displayGameState();
            
            Player currentPlayer = isWhiteTurn ? whitePlayer : blackPlayer;
            
            if (board.isCheck(currentPlayer.isWhite())) {
                System.out.println("CHECK! " + currentPlayer.getName() + " is in check.");
                
                if (board.isCheckmate(currentPlayer.isWhite())) {
                    System.out.println("CHECKMATE! " + currentPlayer.getName() + " loses!");
                    gameOver = true;
                    break;
                }
            }
            
            Position[] move = currentPlayer.makeMove(board);
            
            if (move == null) {
                // Player wants to quit
                System.out.println("Game ended by player choice.");
                gameOver = true;
                break;
            }
            
            Position from = move[0];
            Position to = move[1];
            Piece piece = board.getPiece(from);
            
            // Make the move
            if (board.movePiece(from, to)) {
                // Check for pawn promotion
                if (piece instanceof Pawn) {
                    checkPawnPromotion(to, currentPlayer);
                }
                
                // Switch turns
                isWhiteTurn = !isWhiteTurn;
                
                System.out.println("Move made: " + piece.toString() + " from " + from + " to " + to);
                
                // Check for checkmate after the move
                if (board.isCheckmate(!currentPlayer.isWhite())) {
                    displayGameState();
                    System.out.println("CHECKMATE! " + currentPlayer.getName() + " wins!");
                    gameOver = true;
                } else if (board.isCheck(!currentPlayer.isWhite())) {
                    System.out.println("CHECK! " + (isWhiteTurn ? whitePlayer : blackPlayer).getName() + " is in check.");
                }
            } else {
                System.out.println("Invalid move. Please try again.");
            }
        }
        
        scanner.close();
        System.out.println("Thank you for playing!");
    }

    /**
     * Checks for pawn promotion and handles it.
     * 
     * @param position the position of the promoted pawn
     * @param player the player whose pawn is being promoted
     */
    private void checkPawnPromotion(Position position, Player player) {
        Piece piece = board.getPiece(position);
        if (piece instanceof Pawn) {
            int promotionRow = player.isWhite() ? 0 : 7;
            if (position.getRow() == promotionRow) {
                String pieceChoice = player.choosePromotionPiece();
                Piece newPiece = createPromotionPiece(pieceChoice, player.isWhite(), position);
                
                // Replace the pawn with the new piece
                board.getSquares()[position.getRow()][position.getColumn()] = newPiece;
                
                System.out.println("Pawn promoted to " + newPiece.toString());
            }
        }
    }

    /**
     * Creates a new piece for pawn promotion.
     * 
     * @param pieceType the type of piece to create (Q, R, B, N)
     * @param isWhite true if the piece is white, false if black
     * @param position the position of the new piece
     * @return the new piece
     */
    private Piece createPromotionPiece(String pieceType, boolean isWhite, Position position) {
        switch (pieceType) {
            case "Q":
                return new Queen(isWhite, position);
            case "R":
                return new Rook(isWhite, position);
            case "B":
                return new Bishop(isWhite, position);
            case "N":
                return new Knight(isWhite, position);
            default:
                return new Queen(isWhite, position); // Default to queen
        }
    }

    /**
     * Displays the current game state including the board and game information.
     */
    private void displayGameState() {
        System.out.println("\n" + "=".repeat(50));
        board.display();
        
        Player currentPlayer = isWhiteTurn ? whitePlayer : blackPlayer;
        System.out.println("Current turn: " + currentPlayer.getName() + " (" + 
                          (currentPlayer.isWhite() ? "White" : "Black") + ")");
        
        // Display captured pieces if any
        if (!board.getCapturedPieces().isEmpty()) {
            System.out.println("Captured pieces: " + getCapturedPiecesString());
        }
        
        System.out.println("=".repeat(50));
    }

    /**
     * Gets a string representation of captured pieces.
     * 
     * @return string representation of captured pieces
     */
    private String getCapturedPiecesString() {
        StringBuilder sb = new StringBuilder();
        for (Piece piece : board.getCapturedPieces()) {
            sb.append(piece.toString()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Ends the game and declares the winner.
     * 
     * @param winner the winning player, or null for a draw
     */
    public void end(Player winner) {
        gameOver = true;
        if (winner != null) {
            System.out.println("Game Over! " + winner.getName() + " wins!");
        } else {
            System.out.println("Game Over! It's a draw!");
        }
    }
}

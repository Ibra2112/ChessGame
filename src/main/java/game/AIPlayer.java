package game;

import board.Board;
import board.Position;
import pieces.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AI player that uses Minimax algorithm with Alpha-Beta pruning to make chess moves.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class AIPlayer extends Player {
    private static final int MAX_DEPTH = 3; // Search depth for Minimax
    private static final int KING_VALUE = 10000;
    private static final int QUEEN_VALUE = 900;
    private static final int ROOK_VALUE = 500;
    private static final int BISHOP_VALUE = 300;
    private static final int KNIGHT_VALUE = 300;
    private static final int PAWN_VALUE = 100;
    
    /**
     * Represents a move with from and to positions.
     */
    public static class Move {
        public Position from;
        public Position to;
        
        public Move(Position from, Position to) {
            this.from = from;
            this.to = to;
        }
    }
    
    /**
     * Constructor for AIPlayer class.
     * 
     * @param isWhite true if the AI plays white pieces, false if black
     * @param name the name of the AI player
     */
    public AIPlayer(boolean isWhite, String name) {
        super(isWhite, name);
    }
    
    /**
     * Makes a move using the AI algorithm.
     * 
     * @param board the current board state
     * @return an array containing [from, to] positions
     */
    @Override
    public Position[] makeMove(Board board) {
        Move bestMove = findBestMove(board);
        if (bestMove == null) {
            // No legal moves available (shouldn't happen if game is still ongoing)
            return null;
        }
        return new Position[]{bestMove.from, bestMove.to};
    }
    
    /**
     * Chooses a piece for pawn promotion (always promotes to Queen for AI).
     * 
     * @return "Q" for Queen
     */
    @Override
    public String choosePromotionPiece() {
        return "Q"; // Always promote to Queen (strongest piece)
    }
    
    /**
     * Finds the best move using Minimax with Alpha-Beta pruning.
     * 
     * @param board the current board state
     * @return the best move found
     */
    private Move findBestMove(Board board) {
        List<Move> legalMoves = getAllLegalMoves(board, isWhite());
        
        if (legalMoves.isEmpty()) {
            return null;
        }
        
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        
        for (Move move : legalMoves) {
            // Make the move
            Board testBoard = board.copy();
            testBoard.movePiece(move.from, move.to);
            
            // Evaluate the position
            int moveValue = minimax(testBoard, MAX_DEPTH - 1, false, 
                                   Integer.MIN_VALUE, Integer.MAX_VALUE);
            
            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Minimax algorithm with Alpha-Beta pruning.
     * 
     * @param board the board state to evaluate
     * @param depth remaining depth to search
     * @param isMaximizing true if maximizing player's turn, false otherwise
     * @param alpha alpha value for pruning
     * @param beta beta value for pruning
     * @return the evaluated score
     */
    private int minimax(Board board, int depth, boolean isMaximizing, 
                        int alpha, int beta) {
        // Terminal conditions
        if (depth == 0) {
            return evaluateBoard(board);
        }
        
        boolean currentPlayerIsWhite = isMaximizing ? isWhite() : !isWhite();
        
        // Check for checkmate
        if (board.isCheckmate(currentPlayerIsWhite)) {
            return isMaximizing ? Integer.MIN_VALUE + 1 : Integer.MAX_VALUE - 1;
        }
        
        // Check for stalemate (simplified - no legal moves)
        List<Move> legalMoves = getAllLegalMoves(board, currentPlayerIsWhite);
        if (legalMoves.isEmpty()) {
            return 0; // Stalemate
        }
        
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                Board testBoard = board.copy();
                testBoard.movePiece(move.from, move.to);
                
                int eval = minimax(testBoard, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                if (beta <= alpha) {
                    break; // Alpha-Beta pruning
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : legalMoves) {
                Board testBoard = board.copy();
                testBoard.movePiece(move.from, move.to);
                
                int eval = minimax(testBoard, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                
                if (beta <= alpha) {
                    break; // Alpha-Beta pruning
                }
            }
            return minEval;
        }
    }
    
    /**
     * Evaluates the board position from the AI's perspective.
     * Positive values favor white, negative values favor black.
     * 
     * @param board the board to evaluate
     * @return the evaluation score
     */
    private int evaluateBoard(Board board) {
        int score = 0;
        Piece[][] squares = board.getSquares();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    
                    // Add positional bonuses
                    pieceValue += getPositionalBonus(piece, row, col);
                    
                    if (piece.isWhite()) {
                        score += pieceValue;
                    } else {
                        score -= pieceValue;
                    }
                }
            }
        }
        
        // Check bonus/penalty
        if (board.isCheck(true)) {
            score -= 50; // Penalty for white being in check
        }
        if (board.isCheck(false)) {
            score += 50; // Bonus for black being in check
        }
        
        // Return score from AI's perspective
        return isWhite() ? score : -score;
    }
    
    /**
     * Gets the value of a piece.
     * 
     * @param piece the piece to evaluate
     * @return the piece value
     */
    private int getPieceValue(Piece piece) {
        if (piece instanceof King) {
            return KING_VALUE;
        } else if (piece instanceof Queen) {
            return QUEEN_VALUE;
        } else if (piece instanceof Rook) {
            return ROOK_VALUE;
        } else if (piece instanceof Bishop) {
            return BISHOP_VALUE;
        } else if (piece instanceof Knight) {
            return KNIGHT_VALUE;
        } else if (piece instanceof Pawn) {
            return PAWN_VALUE;
        }
        return 0;
    }
    
    /**
     * Gets positional bonus for a piece based on its position.
     * 
     * @param piece the piece
     * @param row the row position
     * @param col the column position
     * @return positional bonus
     */
    private int getPositionalBonus(Piece piece, int row, int col) {
        int bonus = 0;
        
        // Center control bonus
        if ((row >= 3 && row <= 4) && (col >= 3 && col <= 4)) {
            bonus += 10;
        }
        
        // Pawn advancement bonus
        if (piece instanceof Pawn) {
            if (piece.isWhite()) {
                bonus += (7 - row) * 5; // Closer to promotion is better
            } else {
                bonus += row * 5;
            }
        }
        
        return bonus;
    }
    
    /**
     * Gets all legal moves for a player, considering check constraints.
     * 
     * @param board the current board state
     * @param isWhite true if getting moves for white, false for black
     * @return list of legal moves
     */
    private List<Move> getAllLegalMoves(Board board, boolean isWhite) {
        List<Move> legalMoves = new ArrayList<>();
        Piece[][] squares = board.getSquares();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece != null && piece.isWhite() == isWhite) {
                    Position from = piece.getPosition();
                    List<Position> possibleMoves = piece.possibleMoves(squares);
                    
                    for (Position to : possibleMoves) {
                        // Check if this move is legal (doesn't put own king in check)
                        if (isLegalMove(board, from, to, isWhite)) {
                            legalMoves.add(new Move(from, to));
                        }
                    }
                }
            }
        }
        
        return legalMoves;
    }
    
    /**
     * Checks if a move is legal (doesn't put own king in check).
     * 
     * @param board the current board state
     * @param from the starting position
     * @param to the destination position
     * @param isWhite true if checking for white player, false for black
     * @return true if the move is legal, false otherwise
     */
    private boolean isLegalMove(Board board, Position from, Position to, boolean isWhite) {
        // Make a test move
        Board testBoard = board.copy();
        testBoard.movePiece(from, to);
        
        // Check if this move puts own king in check
        return !testBoard.isCheck(isWhite);
    }
}


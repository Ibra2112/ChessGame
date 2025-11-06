package gui;

import board.Board;
import java.io.Serializable;
import java.util.List;

/**
 * Represents the complete game state for saving and loading games.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Board board;
    private boolean isWhiteTurn;
    private List<MoveRecord> moveHistory;
    
    /**
     * Constructor for GameState.
     * 
     * @param board the chess board
     * @param isWhiteTurn true if it's white's turn
     * @param moveHistory the move history
     */
    public GameState(Board board, boolean isWhiteTurn, List<MoveRecord> moveHistory) {
        this.board = board;
        this.isWhiteTurn = isWhiteTurn;
        this.moveHistory = moveHistory;
    }
    
    /**
     * Gets the board.
     * 
     * @return the board
     */
    public Board getBoard() {
        return board;
    }
    
    /**
     * Checks if it's white's turn.
     * 
     * @return true if white's turn, false otherwise
     */
    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
    
    /**
     * Gets the move history.
     * 
     * @return the move history
     */
    public List<MoveRecord> getMoveHistory() {
        return moveHistory;
    }
}


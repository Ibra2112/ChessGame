package gui;

import board.Board;
import board.Position;
import pieces.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI class for the Chess Game.
 * Provides a graphical user interface for playing chess with Swing.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class ChessGUI extends JFrame {
    private Board board;
    private ChessBoardPanel boardPanel;
    private GameHistoryPanel historyPanel;
    private boolean isWhiteTurn;
    private boolean gameOver;
    private JLabel statusLabel;
    private List<MoveRecord> moveHistory;
    private List<Board> boardHistory; // For undo functionality
    private Color lightSquareColor;
    private Color darkSquareColor;
    private int boardSize;
    
    /**
     * Constructor for ChessGUI class.
     * Initializes the GUI components and sets up the game.
     */
    public ChessGUI() {
        super("Chess Game");
        this.isWhiteTurn = true;
        this.gameOver = false;
        this.moveHistory = new ArrayList<>();
        this.boardHistory = new ArrayList<>();
        this.lightSquareColor = new Color(240, 217, 181); // Light beige
        this.darkSquareColor = new Color(181, 136, 99); // Dark brown
        this.boardSize = 600; // Default board size
        
        initializeBoard();
        initializeGUI();
    }
    
    /**
     * Initializes the chess board.
     */
    private void initializeBoard() {
        this.board = new Board();
        this.boardHistory.add(board.copy());
    }
    
    /**
     * Initializes all GUI components.
     */
    private void initializeGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create menu bar
        createMenuBar();
        
        // Create board panel
        boardPanel = new ChessBoardPanel(board, lightSquareColor, darkSquareColor, boardSize);
        boardPanel.setMoveListener(this::handleMove);
        add(boardPanel, BorderLayout.CENTER);
        
        // Create history panel
        historyPanel = new GameHistoryPanel();
        historyPanel.setUndoListener(() -> undoMove());
        add(historyPanel, BorderLayout.EAST);
        
        // Create status bar
        statusLabel = new JLabel("White's turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    /**
     * Creates the menu bar with game controls.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        gameMenu.add(newGameItem);
        
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener(e -> saveGame());
        gameMenu.add(saveGameItem);
        
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(e -> loadGame());
        gameMenu.add(loadGameItem);
        
        gameMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);
        
        menuBar.add(gameMenu);
        
        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        
        JMenuItem settingsItem = new JMenuItem("Customize Board");
        settingsItem.addActionListener(e -> showSettings());
        settingsMenu.add(settingsItem);
        
        menuBar.add(settingsMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Handles a move made by the user.
     * 
     * @param from the starting position
     * @param to the destination position
     */
    private void handleMove(Position from, Position to) {
        if (gameOver) {
            return;
        }
        
        Piece piece = board.getPiece(from);
        if (piece == null) {
            return;
        }
        
        // Check if it's the correct player's turn
        if (piece.isWhite() != isWhiteTurn) {
            JOptionPane.showMessageDialog(this, 
                "It's " + (isWhiteTurn ? "White" : "Black") + "'s turn!", 
                "Wrong Turn", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if capturing own piece
        Piece targetPiece = board.getPiece(to);
        if (targetPiece != null && targetPiece.isWhite() == piece.isWhite()) {
            JOptionPane.showMessageDialog(this, 
                "Cannot capture your own piece!", 
                "Invalid Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate the move according to chess rules
        if (!piece.isValidMove(to, board.getSquares())) {
            JOptionPane.showMessageDialog(this, 
                "Invalid move! " + getPieceName(piece) + " cannot move to that square.", 
                "Invalid Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if move would put own king in check
        Board testBoard = board.copy();
        testBoard.movePiece(from, to);
        if (testBoard.isCheck(piece.isWhite())) {
            JOptionPane.showMessageDialog(this, 
                "Invalid move! This would put your King in check.", 
                "Invalid Move", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Record move before making it
        MoveRecord moveRecord = new MoveRecord(from, to, piece, targetPiece);
        
        // Make the move
        if (board.movePiece(from, to)) {
            // Save board state for undo
            boardHistory.add(board.copy());
            moveHistory.add(moveRecord);
            
            // Update history panel
            historyPanel.addMove(moveRecord, isWhiteTurn);
            
            // Check if King was captured (shouldn't happen with proper rules, but keep as safety)
            if (targetPiece instanceof King) {
                gameOver = true;
                String winner = piece.isWhite() ? "White" : "Black";
                JOptionPane.showMessageDialog(this, 
                    winner + " wins! The King has been captured!", 
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText(winner + " wins!");
                return;
            }
            
            // Switch turns
            isWhiteTurn = !isWhiteTurn;
            
            // Check for checkmate
            if (board.isCheckmate(!piece.isWhite())) {
                gameOver = true;
                String winner = piece.isWhite() ? "White" : "Black";
                JOptionPane.showMessageDialog(this, 
                    "CHECKMATE! " + winner + " wins!", 
                    "Game Over", 
                    JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("CHECKMATE! " + winner + " wins!");
                boardPanel.repaint();
                return;
            }
            
            // Check for check
            if (board.isCheck(!piece.isWhite())) {
                statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn - CHECK!");
            } else {
                statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn");
            }
            
            // Update board panel
            boardPanel.repaint();
        }
    }
    
    /**
     * Gets the name of a piece for display.
     * 
     * @param piece the piece
     * @return the piece name
     */
    private String getPieceName(Piece piece) {
        if (piece instanceof King) return "King";
        if (piece instanceof Queen) return "Queen";
        if (piece instanceof Rook) return "Rook";
        if (piece instanceof Bishop) return "Bishop";
        if (piece instanceof Knight) return "Knight";
        if (piece instanceof Pawn) return "Pawn";
        return "Piece";
    }
    
    /**
     * Undoes the last move.
     */
    private void undoMove() {
        if (moveHistory.isEmpty() || boardHistory.size() < 2) {
            JOptionPane.showMessageDialog(this, 
                "No moves to undo!", 
                "Undo", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Remove last move from history
        moveHistory.remove(moveHistory.size() - 1);
        boardHistory.remove(boardHistory.size() - 1);
        
        // Restore previous board state
        board = boardHistory.get(boardHistory.size() - 1).copy();
        
        // Switch turn back
        isWhiteTurn = !isWhiteTurn;
        
        // Update status with check information
        if (board.isCheck(isWhiteTurn)) {
            statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn - CHECK!");
        } else {
            statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn");
        }
        
        // Update panels
        boardPanel.setBoard(board);
        historyPanel.removeLastMove();
        boardPanel.repaint();
        
        gameOver = false;
    }
    
    /**
     * Starts a new game.
     */
    private void newGame() {
        int response = JOptionPane.showConfirmDialog(this, 
            "Start a new game? Current progress will be lost.", 
            "New Game", 
            JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            initializeBoard();
            moveHistory.clear();
            boardHistory.clear();
            boardHistory.add(board.copy());
            isWhiteTurn = true;
            gameOver = false;
            statusLabel.setText("White's turn");
            boardPanel.setBoard(board);
            historyPanel.clear();
            boardPanel.repaint();
        }
    }
    
    /**
     * Saves the current game state to a file.
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".chess")) {
                filePath += ".chess";
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                GameState gameState = new GameState(board, isWhiteTurn, moveHistory);
                oos.writeObject(gameState);
                JOptionPane.showMessageDialog(this, 
                    "Game saved successfully!", 
                    "Save Game", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving game: " + e.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Loads a game state from a file.
     */
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Chess Game Files (*.chess)", "chess"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                GameState gameState = (GameState) ois.readObject();
                
                this.board = gameState.getBoard();
                this.isWhiteTurn = gameState.isWhiteTurn();
                this.moveHistory = gameState.getMoveHistory();
                this.gameOver = false;
                
                // Rebuild board history for undo
                boardHistory.clear();
                boardHistory.add(board.copy());
                
                // Update UI
                boardPanel.setBoard(board);
                historyPanel.clear();
                for (int i = 0; i < moveHistory.size(); i++) {
                    MoveRecord move = moveHistory.get(i);
                    historyPanel.addMove(move, i % 2 == 0);
                }
                statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn");
                boardPanel.repaint();
                
                JOptionPane.showMessageDialog(this, 
                    "Game loaded successfully!", 
                    "Load Game", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading game: " + e.getMessage(), 
                    "Load Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Shows the settings dialog.
     */
    private void showSettings() {
        SettingsDialog dialog = new SettingsDialog(this, lightSquareColor, darkSquareColor, boardSize);
        dialog.setVisible(true);
        
        if (dialog.isApplied()) {
            lightSquareColor = dialog.getLightSquareColor();
            darkSquareColor = dialog.getDarkSquareColor();
            boardSize = dialog.getBoardSize();
            
            boardPanel.setColors(lightSquareColor, darkSquareColor);
            boardPanel.setBoardSize(boardSize);
            pack();
            repaint();
        }
    }
    
    /**
     * Main method to launch the GUI.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new ChessGUI().setVisible(true);
        });
    }
}


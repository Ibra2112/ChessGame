package gui;

import board.Board;
import board.Position;
import pieces.*;
import game.AIPlayer;
import network.ChessClient;
import network.NetworkMessage;
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
    private AIPlayer whiteAI;
    private AIPlayer blackAI;
    private boolean whiteIsAI;
    private boolean blackIsAI;
    private ChessClient networkClient;
    private boolean isNetworkMode;
    private boolean isNetworkWhitePlayer;
    
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
        this.whiteIsAI = false;
        this.blackIsAI = false;
        this.whiteAI = null;
        this.blackAI = null;
        this.networkClient = null;
        this.isNetworkMode = false;
        this.isNetworkWhitePlayer = false;
        
        initializeBoard();
        initializeGUI();
        showPlayerSelectionDialog();
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
     * Shows a dialog to select game mode (Two Players or vs Computer).
     */
    private void showPlayerSelectionDialog() {
        JDialog dialog = new JDialog(this, "Game Mode Selection", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        // Main panel with options
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Select Game Mode:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Two players option
        JButton twoPlayersButton = new JButton("Two Players (Same Computer)");
        twoPlayersButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        twoPlayersButton.setPreferredSize(new Dimension(300, 40));
        twoPlayersButton.setFont(new Font("Arial", Font.PLAIN, 12));
        twoPlayersButton.addActionListener(e -> {
            whiteIsAI = false;
            blackIsAI = false;
            whiteAI = null;
            blackAI = null;
            boardPanel.setFlipBoard(false); // Normal orientation for two-player
            updateStatusLabel();
            dialog.dispose();
        });
        
        // VS Computer option
        JButton vsComputerButton = new JButton("Play Against Computer");
        vsComputerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        vsComputerButton.setPreferredSize(new Dimension(300, 40));
        vsComputerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        vsComputerButton.addActionListener(e -> {
            dialog.dispose();
            showComputerSideSelectionDialog();
        });
        
        // Network Play option
        JButton networkButton = new JButton("Network Play (Online)");
        networkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        networkButton.setPreferredSize(new Dimension(300, 40));
        networkButton.setFont(new Font("Arial", Font.PLAIN, 12));
        networkButton.addActionListener(e -> {
            dialog.dispose();
            showNetworkConnectionDialog();
        });
        
        mainPanel.add(twoPlayersButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(vsComputerButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(networkButton);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a dialog to select which side the human wants to play when playing against computer.
     */
    private void showComputerSideSelectionDialog() {
        JDialog dialog = new JDialog(this, "Select Your Side", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(350, 180);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Which side do you want to play?");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // White button
        JButton whiteButton = new JButton("Play as White (Move First)");
        whiteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        whiteButton.setPreferredSize(new Dimension(280, 40));
        whiteButton.setFont(new Font("Arial", Font.PLAIN, 12));
        whiteButton.addActionListener(e -> {
            whiteIsAI = false;
            blackIsAI = true;
            whiteAI = null;
            blackAI = new AIPlayer(false, "AI (Black)");
            boardPanel.setFlipBoard(false); // Normal orientation for White
            updateStatusLabel();
            dialog.dispose();
        });
        
        // Black button
        JButton blackButton = new JButton("Play as Black (Move Second)");
        blackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        blackButton.setPreferredSize(new Dimension(280, 40));
        blackButton.setFont(new Font("Arial", Font.PLAIN, 12));
        blackButton.addActionListener(e -> {
            whiteIsAI = true;
            blackIsAI = false;
            whiteAI = new AIPlayer(true, "AI (White)");
            blackAI = null;
            boardPanel.setFlipBoard(true); // Flip board for Black's perspective
            updateStatusLabel();
            dialog.dispose();
            
            // If white is AI, make first move
            if (whiteIsAI && isWhiteTurn) {
                SwingUtilities.invokeLater(() -> makeAIMove());
            }
        });
        
        mainPanel.add(whiteButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(blackButton);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows a dialog to connect to a network game.
     */
    private void showNetworkConnectionDialog() {
        JDialog dialog = new JDialog(this, "Network Connection", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Connect to Chess Server");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // IP Address input
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ipPanel.add(new JLabel("Server IP:"));
        JTextField ipField = new JTextField("localhost", 15);
        ipPanel.add(ipField);
        mainPanel.add(ipPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Port input
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.add(new JLabel("Port:"));
        JTextField portField = new JTextField("8888", 15);
        portPanel.add(portField);
        mainPanel.add(portPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Connect button
        JButton connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String portStr = portField.getText().trim();
            
            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a server IP address.", 
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int port = 8888;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid port number. Using default 8888.", 
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
            
            dialog.dispose();
            connectToServer(ip, port);
        });
        
        mainPanel.add(connectButton);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Connects to a chess server.
     * 
     * @param host the server hostname or IP
     * @param port the server port
     */
    private void connectToServer(String host, int port) {
        try {
            statusLabel.setText("Connecting to server...");
            boardPanel.repaint();
            
            networkClient = new ChessClient(host, port, new ChessClient.NetworkMessageListener() {
                @Override
                public void onMoveReceived(Position from, Position to) {
                    SwingUtilities.invokeLater(() -> {
                        applyNetworkMove(from, to);
                    });
                }
                
                @Override
                public void onBoardUpdate(boolean isWhiteTurn) {
                    SwingUtilities.invokeLater(() -> {
                        ChessGUI.this.isWhiteTurn = isWhiteTurn;
                        updateStatusLabel();
                        boardPanel.repaint();
                    });
                }
                
                @Override
                public void onBoardReceived(Board board) {
                    SwingUtilities.invokeLater(() -> {
                        ChessGUI.this.board = board;
                        boardPanel.setBoard(board);
                        updateStatusLabel();
                        boardPanel.repaint();
                    });
                }
                
                @Override
                public void onGameOver(String message) {
                    SwingUtilities.invokeLater(() -> {
                        gameOver = true;
                        statusLabel.setText(message);
                        JOptionPane.showMessageDialog(ChessGUI.this, message, 
                            "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        boardPanel.repaint();
                    });
                }
                
                @Override
                public void onError(String error) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(ChessGUI.this, error, 
                            "Network Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                
                @Override
                public void onCheck() {
                    SwingUtilities.invokeLater(() -> {
                        if (board.isCheck(isNetworkWhitePlayer)) {
                            statusLabel.setText((isNetworkWhitePlayer ? "White" : "Black") + 
                                "'s turn - CHECK!");
                        }
                    });
                }
                
                @Override
                public void onCheckmate() {
                    SwingUtilities.invokeLater(() -> {
                        gameOver = true;
                        String winner = isNetworkWhitePlayer ? "Black" : "White";
                        statusLabel.setText("CHECKMATE! " + winner + " wins!");
                        JOptionPane.showMessageDialog(ChessGUI.this, 
                            "CHECKMATE! " + winner + " wins!", 
                            "Game Over", JOptionPane.INFORMATION_MESSAGE);
                        boardPanel.repaint();
                    });
                }
                
                @Override
                public void onDisconnected() {
                    SwingUtilities.invokeLater(() -> {
                        gameOver = true;
                        statusLabel.setText("Disconnected from server");
                        JOptionPane.showMessageDialog(ChessGUI.this, 
                            "Disconnected from server.", 
                            "Connection Lost", JOptionPane.WARNING_MESSAGE);
                    });
                }
            });
            
            isNetworkMode = true;
            isNetworkWhitePlayer = networkClient.isWhitePlayer();
            
            // Flip board if playing as black
            boardPanel.setFlipBoard(!isNetworkWhitePlayer);
            
            statusLabel.setText("Connected! You are playing as " + 
                (isNetworkWhitePlayer ? "White" : "Black"));
            boardPanel.repaint();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to server: " + e.getMessage(), 
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Connection failed");
        }
    }
    
    /**
     * Applies a move received from the network.
     * 
     * @param from starting position
     * @param to destination position
     */
    private void applyNetworkMove(Position from, Position to) {
        Piece piece = board.getPiece(from);
        if (piece == null) return;
        
        Piece targetPiece = board.getPiece(to);
        MoveRecord moveRecord = new MoveRecord(from, to, piece, targetPiece);
        
        if (board.movePiece(from, to)) {
            // Check for pawn promotion
            if (piece instanceof Pawn) {
                int promotionRow = piece.isWhite() ? 0 : 7;
                if (to.getRow() == promotionRow) {
                    Piece newPiece = new Queen(piece.isWhite(), to);
                    board.getSquares()[to.getRow()][to.getColumn()] = newPiece;
                }
            }
            
            boardHistory.add(board.copy());
            moveHistory.add(moveRecord);
            historyPanel.addMove(moveRecord, !isWhiteTurn);
            
            boardPanel.repaint();
        }
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
        
        // Handle network mode
        if (isNetworkMode) {
            // Check if it's the player's turn
            boolean isPlayerTurn = (isWhiteTurn && isNetworkWhitePlayer) || 
                                  (!isWhiteTurn && !isNetworkWhitePlayer);
            if (!isPlayerTurn) {
                JOptionPane.showMessageDialog(this, 
                    "Wait for your opponent's move!", 
                    "Not Your Turn", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validate move locally first
            Piece piece = board.getPiece(from);
            if (piece == null) {
                return;
            }
            
            if (piece.isWhite() != isNetworkWhitePlayer) {
                JOptionPane.showMessageDialog(this, 
                    "That's not your piece!", 
                    "Invalid Move", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Piece targetPiece = board.getPiece(to);
            if (targetPiece != null && targetPiece.isWhite() == piece.isWhite()) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot capture your own piece!", 
                    "Invalid Move", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!piece.isValidMove(to, board.getSquares())) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid move! " + getPieceName(piece) + " cannot move to that square.", 
                    "Invalid Move", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Board testBoard = board.copy();
            testBoard.movePiece(from, to);
            if (testBoard.isCheck(piece.isWhite())) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid move! This would put your King in check.", 
                    "Invalid Move", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Send move to server
            if (networkClient != null && networkClient.isConnected()) {
                networkClient.sendMove(from, to);
            }
            return;
        }
        
        // Check if it's AI's turn
        if ((isWhiteTurn && whiteIsAI) || (!isWhiteTurn && blackIsAI)) {
            return; // Ignore human moves during AI turn
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
            
            updateStatusLabel();
            
            // Update board panel
            boardPanel.repaint();
            
            // Make AI move if it's AI's turn
            if (!gameOver && ((isWhiteTurn && whiteIsAI) || (!isWhiteTurn && blackIsAI))) {
                SwingUtilities.invokeLater(() -> makeAIMove());
            }
        }
    }
    
    /**
     * Makes a move for the AI player.
     */
    private void makeAIMove() {
        if (gameOver) {
            return;
        }
        
        AIPlayer currentAI = isWhiteTurn ? whiteAI : blackAI;
        if (currentAI == null) {
            return;
        }
        
        statusLabel.setText((isWhiteTurn ? "White" : "Black") + " AI is thinking...");
        boardPanel.repaint();
        
        // Use a timer to allow UI to update and make the move feel more natural
        Timer timer = new Timer(500, e -> {
            Position[] move = currentAI.makeMove(board);
            
            if (move == null) {
                gameOver = true;
                statusLabel.setText("No legal moves available");
                return;
            }
            
            Position from = move[0];
            Position to = move[1];
            Piece piece = board.getPiece(from);
            Piece targetPiece = board.getPiece(to);
            
            // Record move before making it
            MoveRecord moveRecord = new MoveRecord(from, to, piece, targetPiece);
            
            // Make the move
            if (board.movePiece(from, to)) {
                // Check for pawn promotion (AI always promotes to Queen)
                if (piece instanceof Pawn) {
                    int promotionRow = piece.isWhite() ? 0 : 7;
                    if (to.getRow() == promotionRow) {
                        Piece newPiece = new Queen(piece.isWhite(), to);
                        board.getSquares()[to.getRow()][to.getColumn()] = newPiece;
                    }
                }
                
                // Save board state for undo
                boardHistory.add(board.copy());
                moveHistory.add(moveRecord);
                
                // Update history panel
                historyPanel.addMove(moveRecord, isWhiteTurn);
                
                // Check if King was captured
                if (targetPiece instanceof King) {
                    gameOver = true;
                    String winner = piece.isWhite() ? "White" : "Black";
                    JOptionPane.showMessageDialog(this, 
                        winner + " wins! The King has been captured!", 
                        "Game Over", 
                        JOptionPane.INFORMATION_MESSAGE);
                    statusLabel.setText(winner + " wins!");
                    boardPanel.repaint();
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
                
                updateStatusLabel();
                boardPanel.repaint();
                
                // If next player is also AI, make their move
                if (!gameOver && ((isWhiteTurn && whiteIsAI) || (!isWhiteTurn && blackIsAI))) {
                    SwingUtilities.invokeLater(() -> makeAIMove());
                }
            }
        });
        
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Updates the status label with current turn and check information.
     */
    private void updateStatusLabel() {
        String playerName = (isWhiteTurn && whiteIsAI) ? "AI (White)" : 
                           (!isWhiteTurn && blackIsAI) ? "AI (Black)" :
                           (isWhiteTurn ? "White" : "Black");
        
        if (board.isCheck(isWhiteTurn)) {
            statusLabel.setText(playerName + "'s turn - CHECK!");
        } else {
            statusLabel.setText(playerName + "'s turn");
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
            
            // Reset AI players
            whiteIsAI = false;
            blackIsAI = false;
            whiteAI = null;
            blackAI = null;
            
            // Reset network mode
            if (networkClient != null) {
                networkClient.disconnect();
                networkClient = null;
            }
            isNetworkMode = false;
            
            // Reset board orientation
            boardPanel.setFlipBoard(false);
            
            boardPanel.setBoard(board);
            historyPanel.clear();
            boardPanel.repaint();
            
            // Show player selection dialog
            showPlayerSelectionDialog();
            
            // If white is AI, make first move
            if (whiteIsAI && isWhiteTurn) {
                SwingUtilities.invokeLater(() -> makeAIMove());
            }
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


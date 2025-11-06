package gui;

import board.Position;
import pieces.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel that displays the game history including moves and captured pieces.
 * Includes an undo button to revert moves.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class GameHistoryPanel extends JPanel {
    private JTextArea moveHistoryArea;
    private JPanel capturedPiecesPanel;
    private JButton undoButton;
    private List<Piece> whiteCapturedPieces;
    private List<Piece> blackCapturedPieces;
    private Runnable undoListener;
    
    /**
     * Constructor for GameHistoryPanel.
     */
    public GameHistoryPanel() {
        this.whiteCapturedPieces = new ArrayList<>();
        this.blackCapturedPieces = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 600));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Game History", 
            TitledBorder.LEFT, 
            TitledBorder.TOP));
        
        // Move history area
        moveHistoryArea = new JTextArea();
        moveHistoryArea.setEditable(false);
        moveHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        moveHistoryArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(moveHistoryArea);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        
        // Captured pieces panel
        capturedPiecesPanel = new JPanel();
        capturedPiecesPanel.setLayout(new GridLayout(2, 1));
        capturedPiecesPanel.setBorder(BorderFactory.createTitledBorder("Captured Pieces"));
        
        JPanel whiteCapturedPanel = new JPanel();
        whiteCapturedPanel.setBorder(BorderFactory.createTitledBorder("White Captured"));
        whiteCapturedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        whiteCapturedPanel.setBackground(Color.WHITE);
        
        JPanel blackCapturedPanel = new JPanel();
        blackCapturedPanel.setBorder(BorderFactory.createTitledBorder("Black Captured"));
        blackCapturedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        blackCapturedPanel.setBackground(Color.BLACK);
        
        capturedPiecesPanel.add(whiteCapturedPanel);
        capturedPiecesPanel.add(blackCapturedPanel);
        
        // Undo button
        undoButton = new JButton("Undo Move");
        undoButton.setFont(new Font("Arial", Font.BOLD, 14));
        undoButton.addActionListener(e -> {
            if (undoListener != null) {
                undoListener.run();
            }
        });
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(scrollPane, BorderLayout.CENTER);
        topPanel.add(capturedPiecesPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.CENTER);
        add(undoButton, BorderLayout.SOUTH);
    }
    
    /**
     * Sets the undo listener.
     * 
     * @param listener the undo listener
     */
    public void setUndoListener(Runnable listener) {
        this.undoListener = listener;
    }
    
    /**
     * Adds a move to the history.
     * 
     * @param move the move record
     * @param isWhiteTurn true if it was white's turn
     */
    public void addMove(MoveRecord move, boolean isWhiteTurn) {
        String player = isWhiteTurn ? "White" : "Black";
        String pieceName = getPieceName(move.getPiece());
        String moveText = String.format("%d. %s: %s %s -> %s\n", 
            moveHistoryArea.getLineCount() / 2 + 1,
            player, 
            pieceName, 
            move.getFrom().toAlgebraicNotation(), 
            move.getTo().toAlgebraicNotation());
        
        moveHistoryArea.append(moveText);
        moveHistoryArea.setCaretPosition(moveHistoryArea.getDocument().getLength());
        
        // Add captured piece if any
        if (move.getCapturedPiece() != null) {
            if (move.getCapturedPiece().isWhite()) {
                whiteCapturedPieces.add(move.getCapturedPiece());
            } else {
                blackCapturedPieces.add(move.getCapturedPiece());
            }
            updateCapturedPiecesDisplay();
        }
    }
    
    /**
     * Removes the last move from the history.
     */
    public void removeLastMove() {
        String text = moveHistoryArea.getText();
        if (!text.isEmpty()) {
            String[] lines = text.split("\n");
            if (lines.length > 0) {
                StringBuilder newText = new StringBuilder();
                for (int i = 0; i < lines.length - 1; i++) {
                    newText.append(lines[i]).append("\n");
                }
                moveHistoryArea.setText(newText.toString());
            }
        }
    }
    
    /**
     * Clears the move history.
     */
    public void clear() {
        moveHistoryArea.setText("");
        whiteCapturedPieces.clear();
        blackCapturedPieces.clear();
        updateCapturedPiecesDisplay();
    }
    
    /**
     * Updates the captured pieces display.
     */
    private void updateCapturedPiecesDisplay() {
        // Get the captured panels
        JPanel whitePanel = (JPanel) capturedPiecesPanel.getComponent(0);
        JPanel blackPanel = (JPanel) capturedPiecesPanel.getComponent(1);
        
        whitePanel.removeAll();
        blackPanel.removeAll();
        
        // Add white captured pieces
        for (Piece piece : whiteCapturedPieces) {
            JLabel label = new JLabel(getPieceSymbol(piece));
            label.setFont(new Font("Arial", Font.PLAIN, 20));
            whitePanel.add(label);
        }
        
        // Add black captured pieces
        for (Piece piece : blackCapturedPieces) {
            JLabel label = new JLabel(getPieceSymbol(piece));
            label.setFont(new Font("Arial", Font.PLAIN, 20));
            label.setForeground(Color.WHITE);
            blackPanel.add(label);
        }
        
        whitePanel.revalidate();
        whitePanel.repaint();
        blackPanel.revalidate();
        blackPanel.repaint();
    }
    
    /**
     * Gets the name of a piece.
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
     * Gets the Unicode symbol for a chess piece.
     * 
     * @param piece the piece
     * @return the Unicode symbol
     */
    private String getPieceSymbol(Piece piece) {
        if (piece instanceof King) {
            return piece.isWhite() ? "\u2654" : "\u265A";
        } else if (piece instanceof Queen) {
            return piece.isWhite() ? "\u2655" : "\u265B";
        } else if (piece instanceof Rook) {
            return piece.isWhite() ? "\u2656" : "\u265C";
        } else if (piece instanceof Bishop) {
            return piece.isWhite() ? "\u2657" : "\u265D";
        } else if (piece instanceof Knight) {
            return piece.isWhite() ? "\u2658" : "\u265E";
        } else if (piece instanceof Pawn) {
            return piece.isWhite() ? "\u2659" : "\u265F";
        }
        return "";
    }
}


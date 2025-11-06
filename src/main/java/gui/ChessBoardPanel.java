package gui;

import board.Board;
import board.Position;
import pieces.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Panel that displays the chessboard and handles user interactions.
 * Supports both click-and-move and drag-and-drop functionality.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class ChessBoardPanel extends JPanel {
    private Board board;
    private Color lightSquareColor;
    private Color darkSquareColor;
    private int squareSize;
    private Position selectedSquare;
    private Position draggedSquare;
    private Point dragOffset;
    private boolean isDragging;
    private MoveListener moveListener;
    
    /**
     * Interface for handling moves.
     */
    public interface MoveListener {
        void onMove(Position from, Position to);
    }
    
    /**
     * Constructor for ChessBoardPanel.
     * 
     * @param board the chess board
     * @param lightSquareColor color for light squares
     * @param darkSquareColor color for dark squares
     * @param boardSize total size of the board in pixels
     */
    public ChessBoardPanel(Board board, Color lightSquareColor, Color darkSquareColor, int boardSize) {
        this.board = board;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        this.squareSize = boardSize / 8;
        this.selectedSquare = null;
        this.isDragging = false;
        
        setPreferredSize(new Dimension(boardSize, boardSize));
        setBackground(Color.GRAY);
        
        // Add mouse listeners for click-and-move and drag-and-drop
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }
    
    /**
     * Sets the move listener.
     * 
     * @param listener the move listener
     */
    public void setMoveListener(MoveListener listener) {
        this.moveListener = listener;
    }
    
    /**
     * Sets the board.
     * 
     * @param board the new board
     */
    public void setBoard(Board board) {
        this.board = board;
    }
    
    /**
     * Sets the square colors.
     * 
     * @param lightSquareColor color for light squares
     * @param darkSquareColor color for dark squares
     */
    public void setColors(Color lightSquareColor, Color darkSquareColor) {
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
    }
    
    /**
     * Sets the board size.
     * 
     * @param boardSize total size of the board in pixels
     */
    public void setBoardSize(int boardSize) {
        this.squareSize = boardSize / 8;
        setPreferredSize(new Dimension(boardSize, boardSize));
        revalidate();
    }
    
    /**
     * Converts mouse coordinates to board position.
     * 
     * @param x mouse x coordinate
     * @param y mouse y coordinate
     * @return the board position, or null if outside the board
     */
    private Position getPositionFromPoint(int x, int y) {
        int col = x / squareSize;
        int row = y / squareSize;
        
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            return new Position(row, col);
        }
        return null;
    }
    
    /**
     * Handles mouse press events.
     * 
     * @param e the mouse event
     */
    private void handleMousePressed(MouseEvent e) {
        Position pos = getPositionFromPoint(e.getX(), e.getY());
        if (pos == null) return;
        
        Piece piece = board.getPiece(pos);
        
        if (piece != null) {
            // Start dragging
            isDragging = true;
            draggedSquare = pos;
            dragOffset = new Point(e.getX() - pos.getColumn() * squareSize, 
                                  e.getY() - pos.getRow() * squareSize);
            selectedSquare = pos;
            repaint();
        } else if (selectedSquare != null) {
            // Click on empty square - complete move
            if (moveListener != null) {
                moveListener.onMove(selectedSquare, pos);
            }
            selectedSquare = null;
            repaint();
        }
    }
    
    /**
     * Handles mouse drag events.
     * 
     * @param e the mouse event
     */
    private void handleMouseDragged(MouseEvent e) {
        if (isDragging && draggedSquare != null) {
            repaint();
        }
    }
    
    /**
     * Handles mouse release events.
     * 
     * @param e the mouse event
     */
    private void handleMouseReleased(MouseEvent e) {
        if (isDragging && draggedSquare != null) {
            Position targetPos = getPositionFromPoint(e.getX(), e.getY());
            
            if (targetPos != null && !targetPos.equals(draggedSquare)) {
                // Complete drag-and-drop move
                if (moveListener != null) {
                    moveListener.onMove(draggedSquare, targetPos);
                }
            }
            
            isDragging = false;
            draggedSquare = null;
            dragOffset = null;
            selectedSquare = null;
            repaint();
        }
    }
    
    /**
     * Paints the chessboard and pieces.
     * 
     * @param g the Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw squares
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor = (row + col) % 2 == 0 ? lightSquareColor : darkSquareColor;
                g2d.setColor(squareColor);
                g2d.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
                
                // Highlight selected square
                if (selectedSquare != null && selectedSquare.getRow() == row && selectedSquare.getColumn() == col) {
                    g2d.setColor(new Color(255, 255, 0, 100)); // Yellow highlight
                    g2d.fillRect(col * squareSize, row * squareSize, squareSize, squareSize);
                }
            }
        }
        
        // Draw pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPiece(pos);
                
                if (piece != null && !(isDragging && draggedSquare != null && 
                    draggedSquare.getRow() == row && draggedSquare.getColumn() == col)) {
                    drawPiece(g2d, piece, col * squareSize, row * squareSize);
                }
            }
        }
        
        // Draw dragged piece
        if (isDragging && draggedSquare != null) {
            Piece piece = board.getPiece(draggedSquare);
            if (piece != null && dragOffset != null) {
                int x = (int) (draggedSquare.getColumn() * squareSize + dragOffset.getX() - squareSize / 2);
                int y = (int) (draggedSquare.getRow() * squareSize + dragOffset.getY() - squareSize / 2);
                drawPiece(g2d, piece, x, y);
            }
        }
    }
    
    /**
     * Draws a chess piece on the board.
     * 
     * @param g2d the Graphics2D object
     * @param piece the piece to draw
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private void drawPiece(Graphics2D g2d, Piece piece, int x, int y) {
        String pieceSymbol = getPieceSymbol(piece);
        Font font = new Font("Arial", Font.BOLD, squareSize * 3 / 4);
        g2d.setFont(font);
        
        // Set color based on piece color
        g2d.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
        
        // Draw piece with shadow for better visibility
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (squareSize - fm.stringWidth(pieceSymbol)) / 2;
        int textY = y + (squareSize + fm.getAscent()) / 2;
        
        // Draw shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawString(pieceSymbol, textX + 2, textY + 2);
        
        // Draw piece
        g2d.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
        g2d.drawString(pieceSymbol, textX, textY);
    }
    
    /**
     * Gets the Unicode symbol for a chess piece.
     * 
     * @param piece the piece
     * @return the Unicode symbol
     */
    private String getPieceSymbol(Piece piece) {
        if (piece instanceof King) {
            return piece.isWhite() ? "\u2654" : "\u265A"; // White/Black King
        } else if (piece instanceof Queen) {
            return piece.isWhite() ? "\u2655" : "\u265B"; // White/Black Queen
        } else if (piece instanceof Rook) {
            return piece.isWhite() ? "\u2656" : "\u265C"; // White/Black Rook
        } else if (piece instanceof Bishop) {
            return piece.isWhite() ? "\u2657" : "\u265D"; // White/Black Bishop
        } else if (piece instanceof Knight) {
            return piece.isWhite() ? "\u2658" : "\u265E"; // White/Black Knight
        } else if (piece instanceof Pawn) {
            return piece.isWhite() ? "\u2659" : "\u265F"; // White/Black Pawn
        }
        return "";
    }
}


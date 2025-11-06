package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for customizing board appearance and size.
 * Allows users to change board colors and size.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class SettingsDialog extends JDialog {
    private JColorChooser lightSquareChooser;
    private JColorChooser darkSquareChooser;
    private JSlider sizeSlider;
    private JLabel sizeLabel;
    private Color lightSquareColor;
    private Color darkSquareColor;
    private int boardSize;
    private boolean applied;
    
    /**
     * Constructor for SettingsDialog.
     * 
     * @param parent the parent frame
     * @param lightSquareColor initial light square color
     * @param darkSquareColor initial dark square color
     * @param boardSize initial board size
     */
    public SettingsDialog(JFrame parent, Color lightSquareColor, Color darkSquareColor, int boardSize) {
        super(parent, "Board Settings", true);
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        this.boardSize = boardSize;
        this.applied = false;
        
        initializeDialog();
    }
    
    /**
     * Initializes the dialog components.
     */
    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Board colors section
        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(2, 1, 10, 10));
        colorPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Board Colors", 
            TitledBorder.LEFT, 
            TitledBorder.TOP));
        
        // Light square color
        JPanel lightPanel = new JPanel(new BorderLayout());
        lightPanel.add(new JLabel("Light Square Color:"), BorderLayout.WEST);
        JButton lightColorButton = new JButton("Choose Color");
        lightColorButton.setBackground(lightSquareColor);
        lightColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Light Square Color", lightSquareColor);
            if (newColor != null) {
                lightSquareColor = newColor;
                lightColorButton.setBackground(lightSquareColor);
            }
        });
        lightPanel.add(lightColorButton, BorderLayout.CENTER);
        colorPanel.add(lightPanel);
        
        // Dark square color
        JPanel darkPanel = new JPanel(new BorderLayout());
        darkPanel.add(new JLabel("Dark Square Color:"), BorderLayout.WEST);
        JButton darkColorButton = new JButton("Choose Color");
        darkColorButton.setBackground(darkSquareColor);
        darkColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Dark Square Color", darkSquareColor);
            if (newColor != null) {
                darkSquareColor = newColor;
                darkColorButton.setBackground(darkSquareColor);
            }
        });
        darkPanel.add(darkColorButton, BorderLayout.CENTER);
        colorPanel.add(darkPanel);
        
        // Preset color schemes
        JPanel presetPanel = new JPanel();
        presetPanel.setBorder(BorderFactory.createTitledBorder("Preset Themes"));
        presetPanel.setLayout(new FlowLayout());
        
        JButton classicButton = new JButton("Classic");
        classicButton.addActionListener(e -> {
            lightSquareColor = new Color(240, 217, 181);
            darkSquareColor = new Color(181, 136, 99);
            lightColorButton.setBackground(lightSquareColor);
            darkColorButton.setBackground(darkSquareColor);
        });
        
        JButton modernButton = new JButton("Modern");
        modernButton.addActionListener(e -> {
            lightSquareColor = new Color(238, 238, 210);
            darkSquareColor = new Color(118, 150, 86);
            lightColorButton.setBackground(lightSquareColor);
            darkColorButton.setBackground(darkSquareColor);
        });
        
        JButton grayButton = new JButton("Gray");
        grayButton.addActionListener(e -> {
            lightSquareColor = new Color(220, 220, 220);
            darkSquareColor = new Color(140, 140, 140);
            lightColorButton.setBackground(lightSquareColor);
            darkColorButton.setBackground(darkSquareColor);
        });
        
        presetPanel.add(classicButton);
        presetPanel.add(modernButton);
        presetPanel.add(grayButton);
        
        // Board size section
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Board Size", 
            TitledBorder.LEFT, 
            TitledBorder.TOP));
        
        sizeSlider = new JSlider(JSlider.HORIZONTAL, 400, 800, boardSize);
        sizeSlider.setMajorTickSpacing(100);
        sizeSlider.setMinorTickSpacing(50);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.addChangeListener(e -> {
            boardSize = sizeSlider.getValue();
            sizeLabel.setText("Size: " + boardSize + " pixels");
        });
        
        sizeLabel = new JLabel("Size: " + boardSize + " pixels");
        sizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        sizePanel.add(sizeSlider, BorderLayout.CENTER);
        sizePanel.add(sizeLabel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        mainPanel.add(colorPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(presetPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(sizePanel);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> {
            applied = true;
            dispose();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            applied = false;
            dispose();
        });
        
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Gets the light square color.
     * 
     * @return the light square color
     */
    public Color getLightSquareColor() {
        return lightSquareColor;
    }
    
    /**
     * Gets the dark square color.
     * 
     * @return the dark square color
     */
    public Color getDarkSquareColor() {
        return darkSquareColor;
    }
    
    /**
     * Gets the board size.
     * 
     * @return the board size in pixels
     */
    public int getBoardSize() {
        return boardSize;
    }
    
    /**
     * Checks if settings were applied.
     * 
     * @return true if applied, false if cancelled
     */
    public boolean isApplied() {
        return applied;
    }
}


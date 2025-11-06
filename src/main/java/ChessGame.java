import game.Game;
import gui.ChessGUI;

/**
 * Main class to run the chess game.
 * Supports both console and GUI modes.
 * 
 * @author Chess Game
 * @version 2.0
 */
public class ChessGame {
    
    /**
     * Main method to start the chess game.
     * 
     * @param args command line arguments:
     *              - "gui" or no arguments: launches GUI
     *              - "console": launches console version
     */
    public static void main(String[] args) {
        try {
            // Default to GUI mode
            if (args.length == 0 || args[0].equalsIgnoreCase("gui")) {
                ChessGUI.main(args);
            } else if (args[0].equalsIgnoreCase("console")) {
                Game game = new Game();
                game.start();
            } else {
                System.out.println("Usage: java ChessGame [gui|console]");
                System.out.println("Default: GUI mode");
                ChessGUI.main(args);
            }
        } catch (Exception e) {
            System.err.println("An error occurred while running the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

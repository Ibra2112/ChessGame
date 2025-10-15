import game.Game;

/**
 * Main class to run the console-based chess game.
 * 
 * @author Chess Game
 * @version 1.0
 */
public class ChessGame {
    
    /**
     * Main method to start the chess game.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            Game game = new Game();
            game.start();
        } catch (Exception e) {
            System.err.println("An error occurred while running the game: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package gipf.intelligence;

import csc3335.gipf_game.GipfGame;
import csc3335.gipf_game.GipfPlayable;

/**
 *
 * @author Alex
 */
public class Agent implements GipfPlayable {

    private GipfGame gipfGame;

    /**
     * Default constructor
     * @param gipfGame Instance of game.
     */
    public Agent(GipfGame gipfGame) {
        this.gipfGame = gipfGame;
    }

    @Override
    public String makeGipfMove(int i) {

        // Returning variables! Edit these to return a position to make a move
        char row = 'a'; // a-i index of rows for the piece to be placed in
        int col = 1; // Depending on the col the value should be between 5-9
        int direction = 0; // 0-5 Direction value representing which way the piece should be placed.

        // Returns the position based
        return "" + row + " " + col + " " + direction;
    }
}

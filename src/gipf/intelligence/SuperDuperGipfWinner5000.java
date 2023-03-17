package gipf.intelligence;

import csc3335.gipf_game.GipfGame;
import csc3335.gipf_game.GipfPlayable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Alex
 */
public class SuperDuperGipfWinner5000 implements GipfPlayable {

    private GipfGame gipfGame;

    /**
     * Default constructor
     * @param gipfGame Instance of game.
     */
    public SuperDuperGipfWinner5000(GipfGame gipfGame) {
        this.gipfGame = gipfGame;
    }

    private static final Integer[][][] moves
            = {
            {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // a
            {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // b
            {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // c
            {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // d
            {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}}, // e
            {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // f
            {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // g
            {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // h
            {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}} // i
    };
    public final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};
    private String convertColToLetter(Integer col) {
        if (col < this.columns.length && col >= 0) {
            return this.columns[col];
        }
        return null;
    }

    /**
     * Generates all possible moves and returns them as states in a set
     *
     * @param g_state Initial state
     * @param player Value of the player who is making the move
     * @return
     */
    private Set<State> generateAllMoves(State g_state, int player) {

        Set<State> states = new HashSet<>();
        GipfGame game = g_state.getGipfGame();

        for(int i = 0; i < moves.length; i++) { // Col

            // Goes over each letter
            for(int j = 0; j < moves[i].length; j++) { // Row
                for(int k = 0; k < moves[i][j].length; k++) { // Direction
                    int dir = moves[i][j][k];
                    // Construct the move
                    Move move = new Move(convertColToLetter(i), j, k);

                    // Create the game & create state & add it to states if it is legal
                    GipfGame t_game = new GipfGame(game); // cloned game
                    boolean legal_move = t_game.makeMove(move.toString(), player); // Make move

                    if(legal_move) { // If the move is allowed.
                        states.add(new State(t_game, move).setParent(g_state)); // add to states
                    }
                }
            }
        }

        g_state.setChildren(states); // Assigns the parent state its children set
        return states;
    }

    /**
     * Generates k states, and returns the leafs of all of the states.
     * Assigns parents and children
     * @param initial_state
     * @param player
     * @param k
     * @return Leafs of the tree
     */
    public Set<State> generateKStates(State initial_state, int player, int k) {
        Set<State> leafs = generateAllMoves(initial_state, player);

        if(k > 0)
            leafs.forEach(n->generateKStates(n, player == 1 ? 0 : 1, k-1));

        return leafs;
    }

    @Override
    public String makeGipfMove(int i) {

        // Generate states
        // Construct current state.
        final int tree_length = 3;
        State state = new State(gipfGame, null);
        Set<State> leafs = generateKStates(state, i, tree_length);


        return "";
    }
}


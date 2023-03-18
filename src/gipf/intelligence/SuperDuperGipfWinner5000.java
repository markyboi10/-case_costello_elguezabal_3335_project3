package gipf.intelligence;

import csc3335.gipf_game.GipfGame;
import csc3335.gipf_game.GipfPlayable;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class SuperDuperGipfWinner5000 implements GipfPlayable {
long startTime = System.currentTimeMillis();
    private GipfGame gipfGame;

    /**
     * Default constructor
     *
     * @param gipfGame Instance of game.
     */
    public SuperDuperGipfWinner5000(GipfGame gipfGame) {
        this.gipfGame = gipfGame;
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

        for (String col_row : game.edgeSpots) {
            String[] split = col_row.split(" ");
            for (int direction = 0; direction < 6; direction++) {
                // Construct the move
                Move move = new Move(split[0], Integer.parseInt(split[1]), direction);

                // Create the game & create state & add it to states if it is legal
                GipfGame t_game = new GipfGame(game); // cloned game
                boolean legal_move = t_game.makeMove(move.toString(), player); // Make move

                if (legal_move) { // If the move is allowed.
                    states.add(new State(t_game, move).setParent(g_state)); // add to states
                }
            }
        }

        g_state.setChildren(states); // Assigns the parent state its children set
        return states;
    }

    /**
     * Generates k states, and returns the leafs of all of the states. Assigns
     * parents and children
     *
     * @param initial_state
     * @param player
     * @param k
     * @return Leafs of the tree
     */
    public Set<State> generateKStates(State initial_state, int player, int k) {
        HashSet<State> leafs = new HashSet<>();
        generateKStatesRecursive(initial_state, player, k, leafs);
        return leafs;
    }

    private void generateKStatesRecursive(State initial_state, int player, int k, Set<State> leafs) {
        if (k <= 0) {
            // Append initial_state to leafs
            leafs.add(initial_state);
            return;
        }

        for (State leaf : generateAllMoves(initial_state, player)) {
            int lvl = k - 1;
            generateKStatesRecursive(leaf, player == 1 ? 0 : 1, lvl, leafs);
        }
    }

    @Override
    public String makeGipfMove(int i) {
        System.out.println("Player " + i);

        // Generate states
        // Construct current state.
        final int tree_length = 3;
        State state = new State(gipfGame, null);
        Set<State> leafs = generateKStates(state, i, tree_length);
        List<State> leafs_as_list = leafs.stream().collect(Collectors.toList());
        //Set<State> children = generateAllMoves(state, i);

        String bestMove = "";
        int alpha = 1000000000;
        int beta = -1000000000;
        int bestMoveValue = -1000000000;
        for (State leaf : leafs_as_list) {
  
            int moveValue = minimax2(leaf, tree_length, 1 - i, alpha, beta);
            if (moveValue > bestMoveValue) {
                bestMoveValue = moveValue;
                bestMove = leaf.getMove().toString();
            }
        }

        System.out.println("Best move: " + bestMove);
long endTime = System.currentTimeMillis();
long elapsedTime = endTime - startTime;
System.out.println("Elapsed time: " + elapsedTime + " ms");
        return leafs.stream().findAny().get().getMove().toString();
    }

    private int minimax2(State position, int depth, int i, int alpha, int beta) {
        //System.out.println("p2" + position.getChildren());
        if (depth == 0 || position.getChildren() == null) {
            return 0;//Reached a leaf node, needs to evaluate the terminal state
        }

        if (i == 0) {
            int maxEval = -1000000000;
            
            for (State child : position.getChildren()) {
                int eval = minimax2(child, depth - 1, i, alpha, beta);
                maxEval = max(maxEval, eval);
                alpha = max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = 1000000000;
            for (State child : position.getChildren()) {
                int eval = minimax2(child, depth - 1, i, alpha, beta);
                minEval = max(minEval, eval);
                beta = min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            // System.out.println("This should never be hit" + minEval);
            return minEval;
        }
    }


}

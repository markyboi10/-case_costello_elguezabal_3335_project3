package gipf.intelligence;

import csc3335.gipf_game.GipfGame;
import csc3335.gipf_game.GipfPlayable;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Alex
 */
public class SuperDuperGipfWinner5000 implements GipfPlayable {

    private GipfGame gipfGame;
    private long startTime;
    private long elapsedTime;
    
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

        /* 
         Compare to, simple implementation to order states based on the priority assigned. In
         theory this will speed up alpha beta pruning. Just using a simple, how many pieces
         do each player have evaluation right now
         */
        PriorityQueue<State> moveQueue = new PriorityQueue<>((State s1, State s2) -> {
            int piecesLeft1 = s1.getGipfGame().getPiecesLeft(player);
            int piecesLeft2 = s2.getGipfGame().getPiecesLeft(player);
            return piecesLeft1 - piecesLeft2;
        });

        for (String col_row : game.edgeSpots) {
            String[] split = col_row.split(" ");
            for (int direction = 0; direction < 6; direction++) {
                // Construct the move
                Move move = new Move(split[0], Integer.parseInt(split[1]), direction);

                // Create the game & create state & add it to states if it is legal
                GipfGame t_game = new GipfGame(game); // cloned game
                boolean legal_move = t_game.makeMove(move.toString(), player); // Make move

                if (legal_move) { // If the move is allowed.
                    moveQueue.offer(new State(t_game, move).setParent(g_state)); // add to priority queue, .offer will add based on prioirity of state
                }
            }
        }
        
        // Add states with highest priority, this is what .poll() does (head of the queue)
        while (!moveQueue.isEmpty()) {
            states.add(moveQueue.poll());
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

    /*
    Makes a move for the gipf game. Takes int i which represents
    the player. 0 is us, 1 is the opp.
     */
    @Override
    public String makeGipfMove(int i) {

        System.out.println("Player " + i);
        int tree_length = 3;
        startTime = System.currentTimeMillis();

        // Generate states
        // Construct current state.
        State state = new State(gipfGame, null);
        Set<State> leafs = generateKStates(state, i, tree_length);
        //List<State> leafs_as_list = leafs.stream().collect(Collectors.toList());

        double alpha = 10; // random initialization for alpha
        double beta = -10; // random initialization for beta
        double eval = minimax(state, tree_length, i, i, alpha, beta);

        State best_move = state.getChildren().stream().filter(n -> n.getEvaluation()==eval).findFirst().orElse(null);

        // If no best state is found, throw an exception
        if(best_move == null) {
            throw new NullPointerException();
        }

        System.out.println(System.currentTimeMillis() - startTime + "ms");

        // Return the best move
        return best_move.getMove().toString();

        /**
        String bestMove = "";

        double bestMoveValue = -10; // random intiialozation for best valued move 

        for (int depth = 1; depth <= tree_length; depth++) {
            Set<State> leafs = generateKStates(state, i, depth);
            List<State> leafs_as_list = leafs.stream().collect(Collectors.toList());
            // for every leaf in the list of terminal leaves
            for (State leaf : leafs_as_list) {
                elapsedTime = System.currentTimeMillis() - startTime;
                //System.out.println("Elapsed time " + elapsedTime);
                //System.out.println("Depth " + depth);

                State parent = leaf.getParent();
                // Call minimax algorithm, takes each state in and performs eval, int 1-i switches between 0 and 1 when it recieves recursive call 
                double moveValue = minimax(parent, tree_length, 1 - i, alpha, beta);
                // if the value of a leaf is greater than a predecessor, update
                if (moveValue > bestMoveValue) {
                    bestMoveValue = moveValue;
                    bestMove = leaf.getMove().toString();
                }
            }

        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println("Best move: " + bestMove);

        return bestMove;
         **/
    }


    /**
     *
     * Implements the minimax algorithm to evaluate a move given a state.
     * The position is the current state being looked at, depth is where we are
     * in the tree, i the player, and alpha and beta to prune the tree
     *
     * @param position Current state
     * @param depth Current depth of the tree
     * @param i Current players turn (MIN or MAX)
     * @param starting_i Starting players turn
     * @param alpha
     * @param beta
     * @return
     */
    private double minimax(State position, int depth, int i, int starting_i, double alpha, double beta) {
//        timeLeft = System.currentTimeMillis() - startTime;
//        System.out.println(timeLeft);
//System.out.println("Is children null? - " + position.getChildren());
        if (depth == 0 || position.getChildren() == null) {
            double evalFct = evaluate();
            return evalFct; //Reached a leaf node, needs to evaluate the terminal state
        }
        
//            if (timeLeft >= 4000) { // check timeLeft before evaluating each state
//        return 0; // return any arbitrary value to stop further evaluation
//    }

        // If us
        //System.out.println("The i is: " + i);
        if (i == starting_i) {
            //System.out.println("The i is: " + i);
            //System.out.println("Inside of i = 0 REACHED");
            // Initialize maxEval
            double maxEval = Double.MIN_VALUE; // -inf
            
            // Evaluate every possible move of that state
            for (State child : position.getChildren()) {
                double eval = minimax(child, depth - 1, 1-starting_i, starting_i, alpha, beta);
                maxEval = Double.max(maxEval, eval);

                // Assigns the evaluation of the parent to be the max evaluation of the children OR if it is 0 ie the first child
                if(position.getEvaluation() == 0 || maxEval > position.getEvaluation()) {
                    position.setEvaluation(maxEval);
                }

                //alpha = max(alpha, eval);
                // If beta is less or equal, prune
                //if (beta <= alpha) {
                    //break;
                //}
            }
            //System.out.println(maxEval);
            return position.getEvaluation();
        // If opp.
        
        } else {
            //System.out.println("The i is: " + i);
            // Initialize minEval
            double minEval = Double.MAX_VALUE; // inf
            // Evaluate every possible move of that state
            for (State child : position.getChildren()) {
                double eval = minimax(child, depth - 1, starting_i, starting_i, alpha, beta);
                minEval = Double.min(minEval, eval);

                // Assign the eval to the parent if it is less then the current eval OR if it is 0 ie first child
                if(position.getEvaluation() == 0 || position.getEvaluation() < minEval) {
                    position.setEvaluation(minEval);
                }

                //beta = min(beta, eval);
                // If beta is less or equal, prune
                //if (beta <= alpha) {
                    //break;
                //}
            }
            // System.out.println("This should never be hit" + minEval);
            return position.getEvaluation();
        }
    }

    // Evaluation function for leaf nodes
    private double evaluate() {
        double score = 0; // Initialize score
        // Eval 1: OurPiecesLeft - TheirPiecesLeft. Simple, but good start
        /*// If it is our turn
        // Eval board by checking who has more pieces
        score = gipfGame.getPiecesLeft(0) - gipfGame.getPiecesLeft(1);*/
        
        // Eval 2: lg(OurPiecesLeft) - StarveVal.
        // Idea: Starve their opponents. If our pieces get low, exponentially recover pieces (lg function accomplishes this). If their pieces get low, keep it that way.
        
        // Prioritize retrieving our pieces logarithmically (if we have a lot of pieces, we're already happy, don't worry about it, but if we're low, start retrieving).
        double prioOurPieces = Math.log(gipfGame.getPiecesLeft(0)) / Math.log(2);
        // Prioritize tracking enemy's pieces by:
            // 1. If they've got 0, we are infinitely happy (negative because we subtract starveEnemy)
            // 2. If not, then we prioritize it by how many they have left rationally
                // a. If they have a lot of pieces, don't worry yet, it will dwindle as we starve
                // b. If they have very few pieces, really try to keep them starving
        double prioEnemyPieces = gipfGame.getPiecesLeft(1) == 0 ? -10000000 : (18 / gipfGame.getPiecesLeft(1)) - 1;
        // Prioritize enemy runs linearly, placing double the weight on 3-in-a-row.
        double enemyRuns = (num2InARow() / 2) + num3InARow();
        // Starve enemies by minimizing runs and enemy pieces
        double starveEnemy = enemyRuns + prioEnemyPieces;

        // Score based on the priority of keeping our pieces vs. priority of starving the enemy's pieces.
        score = prioOurPieces - starveEnemy;
        return score;
    }
    

}

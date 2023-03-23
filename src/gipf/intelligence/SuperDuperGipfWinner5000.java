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
    private static int ourStartingPiecesLeft;
    private static int theirStartingPiecesLeft;
    
    /**
     * Default constructor
     *
     * @param gipfGame Instance of game.
     */
    public SuperDuperGipfWinner5000(GipfGame gipfGame) {
        this.gipfGame = gipfGame;
        ourStartingPiecesLeft = 18;
        theirStartingPiecesLeft = 18;
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
                    states.add(new State(t_game, move).setParent(g_state)); // add to priority queue, .offer will add based on prioirity of state
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

    /*
    Makes a move for the gipf game. Takes int i which represents
    the player. 0 is us, 1 is the opp.
     */
    @Override
    public String makeGipfMove(int i) {

        //System.out.println("Player " + i);
        final int tree_length = 3;
        long startTime = System.currentTimeMillis();

        // Generate states
        // Construct current state.
        State state = new State(gipfGame, null);
        Set<State> leafs = generateKStates(state, i, tree_length);
        //List<State> leafs_as_list = leafs.stream().collect(Collectors.toList());

        System.gc();
        // In case where the children were never created.
        if(state.getChildren() == null) {
            generateKStates(state, i, tree_length);
        }

        double alpha = 10; // random initialization for alpha
        double beta = -10; // random initialization for beta
        double eval = minimax(state, tree_length, true, i, alpha, beta);
        System.out.println("Eval Selected: " + eval);

        State best_move = state.getChildren().stream().filter(n -> n.getEvaluation()==eval).findFirst().orElse(null);

        // If no best state is found, throw an exception
        if(best_move == null) {
            throw new NullPointerException();
        }

        System.out.println(System.currentTimeMillis() - startTime + "ms");

        // Determine the best move string, we do this because I am setting state = null for the GC to come by.
        String move = best_move.getMove().toString();

        // Adjust starting points for pieces left (for eval).
        ourStartingPiecesLeft = best_move.getGipfGame().getPiecesLeft(i);
        theirStartingPiecesLeft = best_move.getGipfGame().getPiecesLeft(1 - i);

        // Break down the state space
        state = null;

        // Return the best move
        return move;

        /** 
         * Depth iterative -- deprecated (?)
         * 
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
     * @param starting_i Starting players turn (our value, since it is our move)
     * @param alpha
     * @param beta
     * @return
     */
    private double minimax(State position, int depth, boolean maximizing, int starting_i, double alpha, double beta) {
//        timeLeft = System.currentTimeMillis() - startTime;
//        System.out.println(timeLeft);
//System.out.println("Is children null? - " + position.getChildren());
        if (depth == 0 || position.getChildren() == null) {
            double evalFct = evaluate(position, starting_i);
            position.setEvaluation(evalFct); // Assigns this position the evaluation
            return evalFct; //Reached a leaf node, needs to evaluate the terminal state
        }
        
//            if (timeLeft >= 4000) { // check timeLeft before evaluating each state
//        return 0; // return any arbitrary value to stop further evaluation
//    }

        // If us
        //System.out.println("The i is: " + i);
        //System.out.println("The i is: " + i);
        //System.out.println("Inside of i = 0 REACHED");
        // Initialize maxEval
        double tempEval = maximizing ? -Double.MAX_VALUE : Double.MAX_VALUE; // -inf or inf depending on if we're maxing or minning
        
        // Evaluate every possible move of that state
        for (State child : position.getChildren()) {
            double eval = minimax(child, depth - 1, !maximizing, starting_i, alpha, beta);
            tempEval = maximizing ? Double.max(tempEval, eval) : Double.min(tempEval, eval);
            
            //if(depth == 1)
                //System.out.println("        Eval: " + eval + " / TempEval: " + tempEval + " / Depth: " + depth + " / Maximizing " + maximizing);
            //else if(depth == 2)
                //System.out.println("    Eval: " + eval + " / TempEval: " + tempEval + " / Depth: " + depth + " / Maximizing " + maximizing);
            //else if(depth == 3)
                //System.out.println("Eval: " + eval + " / TempEval: " + tempEval + " / Depth: " + depth + " / Maximizing " + maximizing);

            //alpha = max(alpha, eval);
            // If beta is less or equal, prune
            //if (beta <= alpha) {
                //break;
            //}
        }
        position.setEvaluation(tempEval);
        //System.out.println(maxEval);
        return position.getEvaluation();
    }

    /**
     * Eval function
     *
     * @param state State containing the GipfGame
     * @param our_player_value value that is our value. Ex. if we are player 1 then player == 1 and our_player_value == 1 if it is our board
     *                         We know since we are evaluating on level 3 that this will always be our evaluation, though if we every go further
     *                         down in the tree this would be something good to know.
     * @return
     */
    // Evaluation function for leaf nodes
    private double evaluate(State state, int our_player_value) {
        double score = 0; // Initialize score
        // Eval 1: OurPiecesLeft - TheirPiecesLeft. Simple, but good start
        /*// If it is our turn
        // Eval board by checking who has more pieces
        score = gipfGame.getPiecesLeft(0) - gipfGame.getPiecesLeft(1);*/
        

        // Eval 2: lg(OurPiecesLeft) + OurRuns - StarveVal. (roughly)
        // Idea: Starve their opponents. If our pieces get low, exponentially recover pieces (lg function accomplishes this). If their pieces get low, keep it that way.
        
        // Prioritize retrieving our pieces logarithmically (if we have a lot of pieces, we're already happy, don't worry about it, but if we're low, start retrieving).
        int ourPieces = state.getGipfGame().getPiecesLeft(our_player_value);
        double prioOurPieces = ourPieces <= 0 ? 0 : Math.floor(18 * (Math.log(state.getGipfGame().getPiecesLeft(our_player_value)) / Math.log(2)));
        double ourRuns = (countRuns(state.getGipfGame(), 2, our_player_value) / 4) + 2 * countRuns(state.getGipfGame(), 3, our_player_value);
        // Prioritize tracking enemy's pieces by a line y = -2x + 36 (we have 2x - 36 because we subtract starveEnemy below)
        double prioEnemyPieces = 2 * state.getGipfGame().getPiecesLeft(1 - our_player_value) - 36;
        // Prioritize enemy runs linearly, placing double the weight on 3-in-a-row.
        double enemyRuns = (countRuns(state.getGipfGame(), 2, 1 - our_player_value) / 2) + countRuns(state.getGipfGame(), 3, 1 - our_player_value);
        // Starve enemies by minimizing runs and enemy pieces
        double starveEnemy = 2 * enemyRuns + prioEnemyPieces;

        // Score based on the priority of keeping our pieces vs. priority of starving the enemy's pieces.
        score = prioOurPieces + 3 * ourRuns - starveEnemy;

        if(((Double)score).isNaN())
            System.out.println("NaN found");

        if(Math.abs(score) == 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000.000000)
            System.out.println("will be null.");
        return score;
    }

    /*
        Helper Methods / Fields
    */

    // Necessary to count runs. Ripped from Prof. Stuetzle's GipfGame class 
    private static final Integer[][][] moves
        = {{{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // a
        {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // b
        {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // c
        {{0, 1}, {1, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}}, // d
        {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}}, // e
        {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // f
        {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // g
        {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}}, // h
        {{0, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}} // i
    };

    /**
     * Copying off of prof's 4-in-a-row checker, we check for the number of 2-3 runs for that player.
     * 
     * @param game current game state
     * @return number of 2 to 3-in-a-row runs
     */
    private double countRuns(GipfGame game, int numInARow, int currPlayer) {
        double runs = 0;
        Integer curRun;
        Integer numInRun;
        int tempCol;
        int tempPos;
        

        Integer[][] board = game.getBoardCopy();

        // For each edge spot
        for (int col = 0; col < board.length; col++) {
            for (int pos = 0; pos < board[col].length; pos++) {
                // Is this the start of a four-in-a-row?
                for (int dir = 0; dir < 6; dir++) {
                    if (!board[col][pos].equals(GipfGame.EMPTY) && board[col][pos] % 2 == currPlayer) {
                        curRun = board[col][pos] % 2;
                        numInRun = 0;
                        tempCol = col;
                        tempPos = pos;
                        // Count the run
                        while (isInRange(tempCol, tempPos, board)
                                && isSamePlayer(board[tempCol][tempPos], curRun)) {
                            tempPos += moves[tempCol][dir][1];
                            tempCol += moves[tempCol][dir][0];
                            numInRun++;
                        }
                        if (numInRun == numInARow) {
                            // Add the run to our list of runs
                            runs++;
                        }
                    }
                }
            }
        }

        return runs;
    }

    private boolean isInRange(int col, int pos, Integer[][] board) {
        return col >= 0 && col < board.length
                && pos >= 0 & pos < board[col].length;
    }

    private boolean isSamePlayer(Integer piece, Integer curRun) {
        return (piece % 2 == curRun % 2) && !piece.equals(GipfGame.EMPTY);
    }
    

}

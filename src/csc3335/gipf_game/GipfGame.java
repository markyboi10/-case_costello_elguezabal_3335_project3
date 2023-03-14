package csc3335.gipf_game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class runs a single game of Gipf. It requires the agent objects that
 * will play the game (of type GipfPlayable) as input to the playGame method,
 * and will continue to have the agents take turns until the game end condition
 * is triggered (one of the players loses).
 *
 * @author stuetzlec
 */
public class GipfGame {

    private GipfPlayable[] players;
    private Integer[] piecesLeft;
    private Integer[] gipfsRemaining;
    private char[] letterValues = "abcdefghi".toCharArray();
    public final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};
    public final String[] edgeSpots = {"a 1", "a 2", "a 3", "a 4", "a 5", "b 1", "b 6",
        "c 1", "c 7", "d 1", "d 8", "e 1", "e 9", "f 1", "f 8", "g 1", "g 7", "h 1", "h 6",
        "i 1", "i 2", "i 3", "i 4", "i 5"};
    private Random rng;
    Integer[][] board = new Integer[9][];
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

// Values for the possible states of each intersection
    public static final Integer WHITE_GIPF = 0;
    public static final Integer BLACK_GIPF = 1;
    public static final Integer WHITE = 2;
    public static final Integer BLACK = 3;
    public static final Integer EMPTY = 14;

    /**
     * The constructor requires a seed to seed the random number generator
     *
     * @param seed Seeds the random number generator
     */
    public GipfGame(int seed) {
        // Initialize the various fields
        this.rng = new Random(seed);
        players = new GipfPlayable[2];
        piecesLeft = new Integer[2];
        gipfsRemaining = new Integer[2];

        initializeEmptyBoard();
    }

    public GipfGame(GipfGame g) {
        this.rng = g.rng;
        this.players = Arrays.copyOf(g.players, g.players.length);
        this.piecesLeft = Arrays.copyOf(g.piecesLeft, g.piecesLeft.length);
        this.gipfsRemaining
                = Arrays.copyOf(g.gipfsRemaining, g.gipfsRemaining.length);

        // Initialize the board
        this.initializeEmptyBoard();

        // Copy the board state
        for (int col = 0; col < 9; col++) {
            for (int pos = 0; pos < this.board[col].length; pos++) {
                this.board[col][pos] = g.board[col][pos];
            }
        }
    }

    /**
     * This method is the driver for the Gipf game. It plays the game between
     * two agents of type GipfPlayable and utilizes the fact that they both must
     * implement the makeGipfMove method to determine a single move, designed by
     * a String object containing the edge location of the move and the
     * direction of the push.
     *
     * @param p1 The first player
     * @param p2 The second player
     * @return Returns a 1 if player 1 wins and a 2 if player 2 wins
     */
    public int playGame(GipfPlayable p1, GipfPlayable p2) {
        // Initialize the playerss
        players[0] = p1;
        players[1] = p2;
        piecesLeft[0] = 18;
        piecesLeft[1] = 18;
        gipfsRemaining[0] = 3;
        gipfsRemaining[1] = 3;
        boolean result;

        // Start by placing the three GIPFs
        int curPlayer = 0;
        for (int i = 0; i < 6; i++) {
            System.out.println("Placing GIPFs");
            piecesLeft[curPlayer] -= 1;
            do {
                result = makeMove(players[curPlayer].makeGipfMove(curPlayer), curPlayer);
            } while (!result);

            curPlayer++;
            curPlayer %= 2;
            printBoard();
        }

        // Continue to alternate players and make moves until the game end
        //   conditions are triggered
        System.out.println("Placing pieces");
        while (gameIsOver() == 0) {
            do {
                result = makeMove(players[curPlayer].makeGipfMove(curPlayer), (curPlayer + 2));
            } while (!result);
            curPlayer++;
            curPlayer %= 2;

            checkForFourInARow();
            printBoard();
        }

        /*
        // used only for testing
        this.board[4][0] = GipfGame.WHITE_GIPF;
        this.board[4][1] = GipfGame.BLACK_GIPF;
        this.board[4][2] = GipfGame.WHITE;
        this.board[4][3] = GipfGame.BLACK;
        this.board[4][4] = GipfGame.BLACK_GIPF;
        this.board[4][5] = GipfGame.BLACK;
        this.board[4][6] = GipfGame.BLACK;
        this.board[4][7] = GipfGame.WHITE_GIPF;
        this.board[4][8] = GipfGame.BLACK;

        this.piecesLeft[0] = 13;
        this.piecesLeft[1] = 11;

        this.printBoard();
        this.checkForFourInARow();
        this.printBoard();

        System.out.println("WHITE: " + this.piecesLeft[0] + "     BLACK: " + this.piecesLeft[1]);

        System.exit(0);
         */
        return gameIsOver();
    }

    /**
     * This method determines if either of the game end conditions (the current
     * player has no pieces to place, or the last gipf of a player has been
     * removed) and calls the winner of the game accordingly.
     *
     * @return Returns a 1 if player 1 wins and a 2 if player 2 wins
     */
    private int gameIsOver() {
        if (piecesLeft[0] == 0) {
            return 2;
        }
        if (piecesLeft[1] == 0) {
            return 1;
        }
        if (gipfsRemaining[0] == 0) {
            return 2;
        }
        if (gipfsRemaining[1] == 0) {
            return 1;
        }
        return 0;
    }

    private void initializeEmptyBoard() {
        for (int i = 0; i < 5; i++) {
            board[i] = new Integer[i + 5];
            board[8 - i] = new Integer[i + 5];
        }
        for (int col = 0; col < 9; col++) {
            for (int pos = 0; pos < board[col].length; pos++) {
                board[col][pos] = GipfGame.EMPTY;
            }
        }
    }

    /**
     * This method takes the move String provided by the current player, the the
     * type of piece being placed, and makes the appropriate move. It will push
     * pieces if it can, and will return false if the move is impossible.
     *
     * @param move The String representing the move of the form "<column>
     * <location> <direction>". For example, "a 1 1".
     * @param piece The piece being placed, of one of the four possible piece
     * types
     * @return Returns true if the move was made successfully, and false if not
     */
    public boolean makeMove(String move, Integer piece) {
        System.out.println(move);
        // Then, make the move
        String[] pMove = move.split(" ");
        Integer col = this.convertLetterToIndex(pMove[0]);
        Integer pos = Integer.parseInt(pMove[1]) - 1;
        Integer dir = Integer.parseInt(pMove[2]);

        // First, check to see if the move is legal
        if (Arrays.binarySearch(edgeSpots, pMove[0] + " " + pMove[1]) < 0) {
            return false;
        }

        // Then, make the move
        boolean result = push(piece, col + moves[col][dir][0], pos + moves[col][dir][1], dir);
        if (result) {
            piecesLeft[piece % 2]--;
        }
        return result;
    }

    /**
     * This recursive method continues to push pieces forward until either a
     * blank is reached, or the algorithm realizes that a push is impossible.
     * The method relies on the ArrayIndexOutOfBoundsException to find
     * impossible pushes.
     *
     * @param piece The piece being pushed
     * @param col The column of the current intersection being pushed from
     * @param pos The position in the column of the current intersection being
     * pushed from
     * @param dir The direction being pushed
     * @return True if the push was successful, false if it failed
     * @throws ArrayIndexOutOfBoundsException
     */
    private boolean push(Integer piece, Integer col, Integer pos, Integer dir)
            throws ArrayIndexOutOfBoundsException {
        // push to the next location
        Integer pieceToMove;
        boolean result;
        // If at the end of the row (and found an edge spot), the move's illegal
        if (!isLegalSpot(col, pos)) {
            return false;
        }
        try {
            // First, is the place we're in null? If so, then make the move and 
            //  return true
            if (board[col][pos].equals(GipfGame.EMPTY)) {
                board[col][pos] = piece;
                return true;
            }
            pieceToMove = board[col][pos];
            result = push(pieceToMove,
                    col + moves[col][dir][0], pos + moves[col][dir][1], dir);
        } catch (ArrayIndexOutOfBoundsException e) {
            result = false;
        }
        if (result) {
            board[col][pos] = piece;
        }
        return result;
    }

    /**
     * This method checks for runs of four or more of the same color and removes
     * the pieces accordingly.
     */
    public void checkForFourInARow() {
        ArrayList< Run> runs = new ArrayList();
        Integer curRun;
        Integer numInRun;
        int tempCol;
        int tempPos;
        // For each edge spot
        for (int col = 0; col < board.length; col++) {
            for (int pos = 0; pos < board[col].length; pos++) {
                // Is this the start of a four-in-a-row?
                for (int dir = 0; dir < 6; dir++) {
                    if (!board[col][pos].equals(GipfGame.EMPTY)) {
                        curRun = board[col][pos] % 2;
                        numInRun = 0;
                        tempCol = col;
                        tempPos = pos;
                        // Count the run
                        while (isInRange(tempCol, tempPos)
                                && isSamePlayer(board[tempCol][tempPos], curRun)) {
                            tempPos += moves[tempCol][dir][1];
                            tempCol += moves[tempCol][dir][0];
                            numInRun++;
                        }
                        if (numInRun >= 4) {
                            // Add the run to our list of runs
                            runs.add(new Run(numInRun, curRun, col, pos, dir));
                        }
                    }
                }
            }
        }

        System.out.println("Processing " + runs.size() + " runs.");
        // Now, we must handle all the runs we found
        for (Run r : runs) {
            Integer col = r.startingCol;
            Integer pos = r.startingPos;
            Integer dir = r.direction;
            // First, remove all the like pieces that aren't gipf pieces
            Integer pieceType = (r.playerValue % 2) + 2;
            Integer numPieces = 0;
            while (numPieces < r.runLength) {
                if (board[col][pos].equals(pieceType)) {
                    board[col][pos] = GipfGame.EMPTY;
                    piecesLeft[pieceType % 2]++;
                }
                col += moves[col][dir][0];
                pos += moves[col][dir][1];
                numPieces++;
            }
            // Then remove the non-player colors from the ends
            while (this.isInRange(col, pos)
                    && !board[col][pos].equals(GipfGame.EMPTY)
                    && !board[col][pos].equals(pieceType)
                    && !board[col][pos].equals(pieceType - 2)) {
                // If the piece removed is a GIPF, updated Gipfs remaining
                if( board[col][pos] < 2 ) {
                    this.gipfsRemaining[(pieceType+1)%2]--;
                }
                board[col][pos] = GipfGame.EMPTY;
                col += moves[col][dir][0];
                pos += moves[col][dir][1];
            }
        }
    }

    private boolean isInRange(int col, int pos) {
        return col >= 0 && col < board.length
                && pos >= 0 & pos < board[col].length;
    }

    private Integer convertLetterToIndex(String letter) {
        return Arrays.binarySearch(this.letterValues, letter.charAt(0));
    }

    private String convertColToLetter(Integer col) {
        if (col < this.columns.length && col >= 0) {
            return this.columns[col];
        }
        return null;
    }

    private boolean isSamePlayer(Integer piece, Integer curRun) {
        return (piece % 2 == curRun % 2) && !piece.equals(GipfGame.EMPTY);
    }

    private boolean isLegalSpot(Integer col, Integer pos) {
        return Arrays.binarySearch(edgeSpots, convertColToLetter(col) + " " + (pos + 1)) < 0;
    }

    // Agent interface methods
    /**
     * Prints the board in a somewhat readable fashion.
     */
    public void printBoard() {
        System.out.println("-------BOARD-------");
        for (int col = 0; col < 9; col++) {
            for (int pos = 0; pos < board[col].length; pos++) {
                System.out.printf("%12s", Character.toString(letterValues[col])
                        + " " + Integer.toString(pos + 1) + ": "
                        + (board[col][pos].equals(GipfGame.EMPTY) ? " " : board[col][pos]));
            }
            System.out.println();
        }
    }

    public int getPiecesLeft(int player) {
        return piecesLeft[player];
    }

    public int getRandomDirection() {
        return rng.nextInt(6);
    }

    public String[] getEdgeSpots() {
        return Arrays.copyOf(edgeSpots, edgeSpots.length);
    }

    /**
     * Returns a random location on the board.
     *
     * @return A random location on the board.
     */
    public String getRandomBoardLocation() {
        // Get a random columns
        int col = rng.nextInt(columns.length);
        String placement = columns[col];
        placement += Integer.toString(1 + rng.nextInt(5 + (col / 2)));

        System.out.println(placement);
        

        return placement;
    }

    /**
     * Query the board
     *
     * @param space A space as a String (e.g. "c 4")
     * @return The type of piece at that location
     */
    public Integer getBoardState(String space) {
        String[] pMove = space.split(" ");
        Integer col = this.convertLetterToIndex(pMove[0]);
        Integer pos = Integer.parseInt(pMove[1]) - 1;

        if (this.isInRange(col, pos)) {
            return board[col][pos];
        }
        return GipfGame.EMPTY;
    }

    public Integer[][] getBoardCopy() {
        Integer[][] newBoard = new Integer[board.length][];
        for (int col = 0; col < board.length; col++) {
            newBoard[col] = new Integer[board[col].length];
            for (int pos = 0; pos < board[col].length; pos++) {
                newBoard[col][pos] = board[col][pos];
            }
        }
        return newBoard;
    }

}

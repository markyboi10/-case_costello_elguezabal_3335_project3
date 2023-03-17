package gipf.intelligence;

import csc3335.gipf_game.GipfGame;

import java.util.Set;

/**
 * State class represents a state
 *
 * Contains a
 *  -  Gipf Game (state & board)
 *  -  Move (move information)
 *  -  Evaluation (init & Integer.MIN_Value)
 */
public class State {

    // * Gipf Game representing the current state
    private GipfGame gipfGame;

    // * Represents the associating move for this game
    // * If this is the initial state, then this is null
    private Move move;

    // * Evaluation of this.gipfGame
    private int evaluation = Integer.MIN_VALUE;

    private State parent;
    private Set<State> children;

    /**
     * Default constructor
     *
     * @param gipfGame
     */
    public State(GipfGame gipfGame, Move move) {
        this.gipfGame = gipfGame;
        this.move = move;
    }

    /**
     * Accessors & Modifiers
     */

    public GipfGame getGipfGame() {
        return gipfGame;
    }

    public Move getMove() {
        return move;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public State getParent() {
        return parent;
    }

    public State setParent(State parent) {
        this.parent = parent;
        return this; // used in construction of states w/ parents
    }

    public Set<State> getChildren() {
        return children;
    }

    public void setChildren(Set<State> children) {
        this.children = children;
    }
}

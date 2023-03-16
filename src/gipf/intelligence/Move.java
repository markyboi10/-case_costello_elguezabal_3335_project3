package gipf.intelligence;


/**
 * Represents a move
 */
public class Move {

    private String col;  // a-i index of rows for the piece to be placed in
    private int row; // Depending on the col the value should be between 5-9
    private int direction; // 0-5 Direction value representing which way the piece should be placed.

    public Move(String col, int row, int direction) {
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    /**
     * Accessors & Modifiers
     */

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        this.col = col;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Construct toString to make the move string
     * @return
     */
    @Override
    public String toString() {
        return getCol() + " " + getRow() + " " + getDirection();
    }
}

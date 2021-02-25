
package minesweeper.model;

/**
 * Represent a single square on the board.
 * All the methods regarding the information of this square, 
 * i.e. amount of surrounding mines and whether this Square has a mine are only accessible if the square has been opened
 * @see Board
 */
public class Square {
    private boolean isMine;
    private boolean opened; 
    private boolean isFlagged;
    private int surroundingMines; //Number of surrounding squares with mines
    private int locationX; 
    private int locationY;

    public Highlight highlight = Highlight.NONE;

    /**
     * Generates a new unopened Square with no mines or flag
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Square(int x, int y) {
        this.isMine = false;
        this.opened = false;
        this.isFlagged = false;
        this.surroundingMines = 0;
        this.locationX = x;
        this.locationY = y;
    }

    /**
     * Get the X coordinate of the Square
     * @return Square's X coordinate
     */
    public int getX() {
        return this.locationX;
    }

    /**
     * Get the Y coordinate of the Square
     * @return Square's Y coordinate
     */
    public int getY() {
        return this.locationY;
    }

    /**
     * Sets the X coordinate of the Square
     * @param x Square's X coordinate
     */
    public void setX(int x) {
        this.locationX = x;
    }

    /**
     * Sets the Y coordinate of the Square
     * @param y Square's Y coordinate
     */
    public void setY(int y) {
        this.locationY = y;
    }


    /**
     * Represents a "click" on this particular square, and sets the state to opened
     */
    public void open() {
        if (!isFlagged) {
            this.opened = true;
        }
    }

    /**
     * Check if this Square contains a mine
     *
     * <u><b>Trying to use this method without first opening the Square will result in an AssertionException</b></u>
     *
     * @return true if this is a mine Square and it has been opened already, false otherwise
     */
    public boolean isMine() {
        assert (this.opened);

        return isMine;
    }

    /**
     * Sets the Square as a mine
     */
    public void setMine() {
        this.isMine = true;
    }

    /**
     * Whether this Square is flagged 
     * @return true if this Square has been flagged by the user
     */
    public boolean isFlagged() {
        return this.isFlagged;
    }
    
    public void toggleFlagged() {
        if (!this.opened) { 
            this.isFlagged = !this.isFlagged;
        }
    }

    /**
     * Number of surrounding Squares that have a mine.
     *
     * <u><b>Trying to use this method without first openeing the Square will result in an AssertionException</b></u>
     *
     * @return The number of surrounding Squares that have a mine.
     */
    public int surroundingMines() {
        assert (this.opened);

        return this.surroundingMines;
    }

    /**
     * Increments the amount this Square has other Squares surrounding it, with mines in them
     */
    public void incrementSurroundingMines() {
        this.surroundingMines++;
    }

    /**
     * Whether this Square is opened
     * @return true if it is opened already, else false
     */    
    public boolean isOpened() {
        return this.opened;
    }
    
    /**
     * Text representation for debugging purposes.
     * @return String
     */
    @Override
    public String toString() {
        if (!this.opened) {
            return "X";
        }

        return this.isMine ? "*" : "" + this.surroundingMines;
    }

    public String locationString() {
        return "(" + locationX + ", " + locationY + ")";
    }
}


package minesweeper.model;

import java.text.DecimalFormat;

/**
 * Class representing a move on the Minesweeper board
 */
public class Move {
    public MoveType type;

    public int x;
    public int y;

    public Highlight highlight = Highlight.NONE;

    // Timestamp in nanoseconds
    public long timestamp;

    public double deltaTime;

    // Distance between the last move and this one
    // NOTE:    This value is initialized externally
    //          by BotExecutor, bots need not and
    //          should not modify this value
    public double euclideanDistance;

    /**
     * Create a Move of a specific type at given X.Y coordinates
     *
     * @param type Move type
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Move(MoveType type, int x, int y) {
        this(type, x, y, Highlight.NONE);
    }

    /**
     * Creates a Highlight move at given X,Y coordinate with a given Highlight
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param highlight Highlight colour
     */
    public Move(int x, int y, Highlight highlight) {
        this(MoveType.HIGHLIGHT, x, y, highlight);
    }

    /**
     * Base constructor for Moves
     *
     * @param type Move type
     * @param x X coordinate
     * @param y Y coordinate
     * @param highlight Highlight colour
     */
    public Move(MoveType type, int x, int y, Highlight highlight) {
        this.type = type;
        
        this.x = x;
        this.y = y;

        this.highlight = highlight;

        this.timestamp = System.nanoTime();
    }

    /**
     * Sets the euclidean distance travelled since last move
     * This function is called by BotExecutor, bots need not
     * worry about this.
     *
     * @param distance The length of the direct line between last move location and current one
     */
    public void setEuclideanDistance(double distance) {
        this.euclideanDistance = distance;
    }

    @Override
    public String toString() {
        DecimalFormat numberFormat = new DecimalFormat("0.0000");
        return "Move: " + type + " Distance: " + numberFormat.format(euclideanDistance) 
                + " Time: " + numberFormat.format(deltaTime / Math.pow(10, 6));
    }

    public String locationString() {
        return "Move on (" + x + ", " + y + ")";
    }
}

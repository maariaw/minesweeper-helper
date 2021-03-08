
package minesweeper.bot;

import minesweeper.model.Square;
import minesweeper.structures.SquareMap;
import minesweeper.structures.SquareSet;

public class MinesweeperConstraint {
    private SquareSet squares;
    public int mineIndicator;

    public MinesweeperConstraint(int mineIndicator, SquareSet squares) {
        this.squares = squares;
        this.mineIndicator = mineIndicator;
    }

    public SquareSet getSquares() {
        return squares;
    }

    /**
     * Checks if the assigned values for the squares of this constraint satisfy
     * the constraint.
     * @param assignment
     * @return True if sum of assigned values to squares equals mineIndicator, or
     * if not all squares are assigned
     */
    public boolean isSatisfied(SquareMap<Integer> assignment) {
        int sum = 0;
        for (Square square : this.squares.getSquares()) {
            if (!assignment.containsKey(square)) {
                return true;
            }
            sum += assignment.get(square);
        }
        return sum == this.mineIndicator;
    }

    @Override
    public String toString() {
        String s = mineIndicator + " mines in squares";
        for (Square square : squares.getSquares()) {
            s += " " + square.locationString();
        }
        return s;
    }

    /**
     * Removes a square from the the constraints and adjusts the mineIndicator
     * according to the square's known value
     * @param square Square to be removed
     * @param knownValue 0 if the square is known to be safe, 1 if known to be mine
     */
    public void removeSquare(Square square, Integer knownValue) {
        if (squares.remove(square)) {
            mineIndicator -= knownValue;
        }
    }

    /**
     * Method to assess if the constraint is trivial, as in all of its squares
     * have to be mines or all have to be safe to satisfy the constraint.
     * @return -1 if the constraint is not trivial, 0 if all squares have to be safe,
     * 1 if all squares have to be mines
     */
    public int triviality() {
        if (mineIndicator == 0) {
            return 0;
        } else if (mineIndicator == squares.size()) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MinesweeperConstraint other = (MinesweeperConstraint) obj;
        if (this.mineIndicator != other.mineIndicator) {
            return false;
        }
        if (!this.squares.equals(other.squares)) {
            return false;
        }
        return true;
    }
}


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

    public void removeSquare(Square square, Integer assignment) {
        mineIndicator -= assignment;
        squares.remove(square);
    }

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

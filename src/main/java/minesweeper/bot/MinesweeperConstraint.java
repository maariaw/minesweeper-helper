
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import minesweeper.model.Square;

public class MinesweeperConstraint {
    private ArrayList<Square> squares;
    public int mineIndicator;

    public MinesweeperConstraint(int mineIndicator, ArrayList<Square> squares) {
        this.squares = squares;
        this.mineIndicator = mineIndicator;
    }

    public ArrayList<Square> getSquares() {
        return squares;
    }

    public boolean isSatisfied(HashMap<Square, Integer> assignment) {
        int sum = 0;
        for (Square square : this.squares) {
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
        for (Square square : squares) {
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
}

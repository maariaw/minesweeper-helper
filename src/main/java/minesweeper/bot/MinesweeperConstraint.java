
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;

public class MinesweeperConstraint<Square, Integer> extends Constraint {
    private int mineIndicator;

    public MinesweeperConstraint(int mineIndicator, ArrayList<Square> variables) {
        super(variables);
        this.mineIndicator = mineIndicator;
    }

    @Override
    public boolean isSatisfied(HashMap assignment) {
        int sum = 0;
        ArrayList<Square> variables = this.getVariables();
        for (Square variable : variables) {
            if (!assignment.containsKey(variable)) {
                return true;
            }
            int value = (int) assignment.get(variable);
            sum += value;
        }
        
        return sum == this.mineIndicator;
    }
    
}

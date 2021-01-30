
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Constraint<V, D> {
    private ArrayList<V> variables;
    
    public Constraint(ArrayList<V> variables) {
        this.variables = variables;
    }

    public ArrayList<V> getVariables() {
        return variables;
    }
    
    public abstract boolean isSatisfied(HashMap<V, D> assignment);
}

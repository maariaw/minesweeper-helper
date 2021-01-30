
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import minesweeper.model.Square;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CSPTest {
    private CSP csp;
    private ArrayList<Square> variables;
    private HashMap<Square, ArrayList<Integer>> domains;
    
    @Before
    public void setUp() {
        this.variables = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Square square = new Square(i, i);
            variables.add(square);
        }
        
        this.domains = new HashMap<>();
        for (Square variable : variables) {
            ArrayList<Integer> domainValues = new ArrayList<>();
            domainValues.add(0);
            domainValues.add(1);
            domains.put(variable, domainValues);
        }
        
        this.csp = new CSP(variables, domains);
    }
    
    @Test
    public void addConstraintsAssignsConstraintsToConstrainedVariables() {
        ArrayList<Square> varSubset = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            varSubset.add(variables.get(i));
        }
        MinesweeperConstraint constraint = new MinesweeperConstraint(2, varSubset);
        csp.addConstraint(constraint);
        HashMap<Square, ArrayList<Constraint>> constraints = csp.getConstraints();
        for (Square variable : varSubset) {
            ArrayList<Constraint> constraintList = constraints.get(variable);
            int listSize = constraintList.size();
            assertEquals(listSize, 1);
        }
    }
    
    @Test
    public void addConstraintsDoesntAddConstraintsToOtherVariables() {
        ArrayList<Square> varSubset = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            varSubset.add(variables.get(i));
        }
        MinesweeperConstraint constraint = new MinesweeperConstraint(2, varSubset);
        csp.addConstraint(constraint);
        HashMap<Square, ArrayList<Constraint>> constraints = csp.getConstraints();
        for (Square variable : variables) {
            if (!varSubset.contains(variable)) {
                ArrayList<Constraint> constraintList = constraints.get(variable);
                int listSize = constraintList.size();
                assertEquals(listSize, 0);
            }
        }
    }
}

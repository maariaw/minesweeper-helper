
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
    // The variables and domains the tested csp is constructed with
    private ArrayList<Square> variables;
    private HashMap<Square, ArrayList<Integer>> domains;
    // A subset of the variables in the csp
    private ArrayList<Square> varSubset;
    
    @Before
    public void setUp() {
        this.variables = new ArrayList<>();
        this.varSubset = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Square square = new Square(i, i);
            variables.add(square);
            if (i < 6) {
                varSubset.add(square);
            }
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
    public void addConstraintsDoesntAddConstraintsIfVariablesNotInCSP() {
        ArrayList<Square> newVars = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            newVars.add(new Square(i, 6-i));
        }
        MinesweeperConstraint constraint = new MinesweeperConstraint(2, newVars);
        csp.addConstraint(constraint);
        HashMap<Square, ArrayList<Constraint>> constraints = csp.getConstraints();
        ArrayList<Square> cspVariables = csp.getVariables();
        for (Square variable : cspVariables) {
            ArrayList<Constraint> constraintList = constraints.get(variable);
            int listSize = constraintList.size();
            assertEquals(listSize, 0);
        }
    }

    @Test
    public void addConstraintsDoesntAddConstraintsToOtherVariables() {
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

    @Test
    public void anAssignmentIsConsistentIfNoConstraints() {
        HashMap<Square, Integer> assignment = new HashMap<>();
        for (Square variable : variables) {
            assignment.put(variable, 0);
        }
        assertTrue(csp.isConsistent(variables.get(0), assignment));
    }

    @Test
    public void assignmentIsConsistentWhenConstraintsMet() {
        HashMap<Square, Integer> assignment = new HashMap<>();
        for (Square variable : variables) {
            assignment.put(variable, 0);
        }
        csp.addConstraint(new MinesweeperConstraint(0, variables));
        assertTrue(csp.isConsistent(variables.get(0), assignment));
    }

    @Test
    public void assignmentIsNotConsistentWhenConstraintsNotMet() {
        HashMap<Square, Integer> assignment = new HashMap<>();
        for (Square variable : variables) {
            assignment.put(variable, 0);
        }
        csp.addConstraint(new MinesweeperConstraint(1, variables));
        assertFalse(csp.isConsistent(variables.get(0), assignment));
    }

    @Test
    public void backTrackingSearchFindsAllZeroAssignment() {
        HashMap<Square, Integer> template = new HashMap<>();
        csp.addConstraint(new MinesweeperConstraint(0, variables));
        ArrayList<HashMap> solutions = csp.startSearch(template);
        HashMap<Square, Integer> assignment = solutions.get(0);
        for (Square square : variables) {
            assertTrue(assignment.get(square) == 0);
        }
    }

    @Test
    public void backTrackingSearchFindsAnAssignmentWhenNoConstraints() {
        HashMap<Square, Integer> template = new HashMap<>();
        ArrayList<HashMap> solutions = csp.startSearch(template);
        assertFalse(solutions.isEmpty());
    }

    @Test
    public void backTrackingSearchReturnsNoSolutionIfConstraintsCantBeSatisfied() {
        HashMap<Square, Integer> template = new HashMap<>();
        csp.addConstraint(new MinesweeperConstraint(999, variables));
        ArrayList<HashMap> solutions = csp.startSearch(template);
        assertTrue(solutions.isEmpty());
    }
}

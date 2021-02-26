
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        csp.addConstraint(varSubset, 2);
        HashMap<Square, ArrayList<MinesweeperConstraint>> constraints = csp.getConstraints();
        for (Square variable : varSubset) {
            ArrayList<MinesweeperConstraint> constraintList = constraints.get(variable);
            int listSize = constraintList.size();
            assertEquals(listSize, 1);
        }
    }

    @Test
    public void addConstraintsDoesntAddConstraintsToOtherVariables() {
        csp.addConstraint(varSubset, 2);
        HashMap<Square, ArrayList<MinesweeperConstraint>> constraints = csp.getConstraints();
        for (Square variable : variables) {
            if (!varSubset.contains(variable)) {
                assertFalse(constraints.keySet().contains(variable));
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
        csp.addConstraint(variables, 0);
        assertTrue(csp.isConsistent(variables.get(0), assignment));
    }

    @Test
    public void assignmentIsNotConsistentWhenConstraintsNotMet() {
        HashMap<Square, Integer> assignment = new HashMap<>();
        for (Square variable : variables) {
            assignment.put(variable, 0);
        }
        csp.addConstraint(variables, 1);
        assertFalse(csp.isConsistent(variables.get(0), assignment));
    }

    @Test
    public void backTrackingSearchFindsAllZeroAssignment() {
        csp.setConstrainedVariables(new HashSet(variables));
        csp.addConstraint(variables, 0);
        ArrayList<HashMap<Square, Integer>> solutions = csp.startSearch();
        HashMap<Square, Integer> assignment = solutions.get(0);
        for (Square square : variables) {
            assertTrue(assignment.get(square) == 0);
        }
    }

    @Test
    public void backTrackingSearchFindsAnAssignmentWhenNoConstraints() {
        csp.setConstrainedVariables(new HashSet(variables));
        ArrayList<HashMap<Square, Integer>> solutions = csp.startSearch();
        assertFalse(solutions.isEmpty());
    }

    @Test
    public void backTrackingSearchReturnsNoSolutionIfConstraintsCantBeSatisfied() {
        csp.setConstrainedVariables(new HashSet(variables));
        csp.addConstraint(variables, 999);
        ArrayList<HashMap<Square, Integer>> solutions = csp.startSearch();
        assertTrue(solutions.isEmpty());
    }

    @Test
    public void findSafeSolutionsGeneratesAllMinesSummaryWhenAllMinesConstraint() {
        csp.addConstraint(variables, variables.size());
        HashMap<Square, Integer> summary = csp.findSafeSolutions(new HashSet(variables));
        for (Square square : summary.keySet()) {
            assertEquals((Integer) 100, summary.get(square));
        }
    }

    @Test
    public void findSafeSolutionsGeneratesZeroMinesSummaryWhenZeroMinesConstraint() {
        csp.addConstraint(variables, 0);
        HashMap<Square, Integer> summary = csp.findSafeSolutions(new HashSet(variables));
        for (Square square : summary.keySet()) {
            assertEquals((Integer) 0, summary.get(square));
        }
    }

    @Test
    public void findSafeSolutionsGeneratesViableSummaryWhenAmbiguousConstraint() {
        csp.addConstraint(variables, varSubset.size());
        HashMap<Square, Integer> summary = csp.findSafeSolutions(new HashSet(variables));
        for (Square square : summary.keySet()) {
            assertTrue(summary.get(square) > 0 && summary.get(square) < 100);
        }
    }
}

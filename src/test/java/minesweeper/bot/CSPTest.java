
package minesweeper.bot;

import minesweeper.model.Square;
import minesweeper.structures.MyList;
import minesweeper.structures.SquareMap;
import minesweeper.structures.SquareSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CSPTest {
    private CSP csp;
    // The variables and domains the tested csp is constructed with
    private SquareSet variables;
    private SquareMap<int[]> domains;
    // A subset of the variables in the csp
    private SquareSet varSubset;
    private int size;
    
    @Before
    public void setUp() {
        this.size = 12;
        this.variables = new SquareSet(size, size);
        this.varSubset = new SquareSet(size, size);
        for (int i = 0; i < size; i++) {
            Square square = new Square(i, i);
            variables.add(square);
            if (i < size / 2) {
                varSubset.add(square);
            }
        }

        this.domains = new SquareMap<>(size, size);
        for (Square variable : variables.getSquares()) {
            domains.put(variable, new int[] { 0, 1 });
        }
        
        this.csp = new CSP(variables, domains);
    }

    @Test
    public void addConstraintsAssignsConstraintsToConstrainedVariables() {
        csp.addConstraint(varSubset, 2);
        SquareMap<MyList<MinesweeperConstraint>> constraints = csp.getConstraints();
        for (Square variable : varSubset.getSquares()) {
            MyList<MinesweeperConstraint> constraintList = constraints.get(variable);
            int listSize = constraintList.size();
            assertEquals(listSize, 1);
        }
    }

    @Test
    public void addConstraintsDoesntAddConstraintsToOtherVariables() {
        csp.addConstraint(varSubset, 2);
        SquareMap<MyList<MinesweeperConstraint>> constraints = csp.getConstraints();
        for (Square variable : variables.getSquares()) {
            if (!varSubset.contains(variable)) {
                assertFalse(constraints.keySet().contains(variable));
            }
        }
    }

    @Test
    public void anAssignmentIsConsistentIfNoConstraints() {
        SquareMap<Integer> assignment = new SquareMap<>(size, size);
        for (Square variable : variables.getSquares()) {
            assignment.put(variable, 0);
        }
        assertTrue(csp.isConsistent(variables.pop(), assignment));
    }

    @Test
    public void assignmentIsConsistentWhenConstraintsMet() {
        SquareMap<Integer> assignment = new SquareMap<>(size, size);
        for (Square variable : variables.getSquares()) {
            assignment.put(variable, 0);
        }
        csp.addConstraint(variables, 0);
        assertTrue(csp.isConsistent(variables.pop(), assignment));
    }

    @Test
    public void assignmentIsNotConsistentWhenConstraintsNotMet() {
        SquareMap<Integer> assignment = new SquareMap<>(size, size);
        for (Square variable : variables.getSquares()) {
            assignment.put(variable, 0);
        }
        csp.addConstraint(variables, 1);
        assertFalse(csp.isConsistent(variables.pop(), assignment));
    }

    @Test
    public void backTrackingSearchFindsAllZeroAssignment() {
        csp.setConstrainedVariables(variables);
        csp.addConstraint(variables, 0);
        MyList<SquareMap<Integer>> solutions = csp.startSearch();
        SquareMap<Integer> assignment = solutions.get(0);
        for (Square square : variables.getSquares()) {
            assertTrue(assignment.get(square) == 0);
        }
    }

    @Test
    public void backTrackingSearchFindsAnAssignmentWhenNoConstraints() {
        csp.setConstrainedVariables(variables);
        MyList<SquareMap<Integer>> solutions = csp.startSearch();
        assertFalse(solutions.size() == 0);
    }

    @Test
    public void backTrackingSearchReturnsNoSolutionIfConstraintsCantBeSatisfied() {
        csp.setConstrainedVariables(variables);
        csp.addConstraint(variables, 999);
        MyList<SquareMap<Integer>> solutions = csp.startSearch();
        assertTrue(solutions.size() == 0);
    }

    @Test
    public void findSafeSolutionsGeneratesAllMinesSummaryWhenAllMinesConstraint() {
        csp.addConstraint(variables, variables.size());
        SquareMap<Integer> summary = csp.findSafeSolutions(variables);
        MyList<Square> solutionSquares = summary.keySet();
        for (int i = 0; i < solutionSquares.size(); i++) {
            Square square = solutionSquares.get(i);
            assertEquals((Integer) 100, summary.get(square));
        }
    }

    @Test
    public void findSafeSolutionsGeneratesZeroMinesSummaryWhenZeroMinesConstraint() {
        csp.addConstraint(variables, 0);
        SquareMap<Integer> summary = csp.findSafeSolutions(variables);
        MyList<Square> solutionSquares = summary.keySet();
        for (int i = 0; i < solutionSquares.size(); i++) {
            Square square = solutionSquares.get(i);
            assertEquals((Integer) 0, summary.get(square));
        }
    }

    @Test
    public void findSafeSolutionsGeneratesViableSummaryWhenAmbiguousConstraint() {
        csp.addConstraint(variables, varSubset.size());
        SquareMap<Integer> summary = csp.findSafeSolutions(variables);
        MyList<Square> solutionSquares = summary.keySet();
        for (int i = 0; i < solutionSquares.size(); i++) {
            Square square = solutionSquares.get(i);
            assertTrue(summary.get(square) > 0 && summary.get(square) < 100);
        }
    }
}

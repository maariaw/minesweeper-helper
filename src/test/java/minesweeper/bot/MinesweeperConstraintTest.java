
package minesweeper.bot;

import java.util.HashMap;
import java.util.Random;
import minesweeper.model.Square;
import minesweeper.structures.SquareMap;
import minesweeper.structures.SquareSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MinesweeperConstraintTest {
    private Random rng;
    private int mines;
    private SquareSet variables;
    private SquareMap<Integer> correctAssignment;
    private MinesweeperConstraint constraint;

    @Before
    public void setUp() {
        this.rng = new Random(6332);
        this.mines = 0;
        this.correctAssignment = new SquareMap<>(6, 6);
        this.variables = new SquareSet(6, 6);
        for (int i = 0; i < 6; i++) {
            Square square = new Square(i, i);
            if (rng.nextBoolean()) {
                square.setMine();
                mines++;
                this.correctAssignment.put(square, 1);
            } else {
                this.correctAssignment.put(square, 0);
            }
            variables.add(square);
        }
        this.constraint = new MinesweeperConstraint(mines, variables);
    }

    @Test
    public void isSatisfiedWhenCorrectAssignment() {
        assertTrue(constraint.isSatisfied(correctAssignment));
    }

    @Test
    public void isNotSatisfiedWhenIncorrectAssignment() {
        Square deviant = this.variables.getSquares()[0];
        SquareMap<Integer> incorrectAssignment = correctAssignment.createAClone();
        if (correctAssignment.get(deviant) == 0) {
            incorrectAssignment.put(deviant, 1);
        } else {
            incorrectAssignment.put(deviant, 0);
        }
        
        assertFalse(constraint.isSatisfied(incorrectAssignment));
    }

    @Test
    public void isSatisfiedWhenAssignmentIncomplete() {
        Square missing = this.variables.getSquares()[0];
        SquareMap<Integer> incompleteAssignment = correctAssignment.createAClone();
        incompleteAssignment.remove(missing);
        assertTrue(constraint.isSatisfied(incompleteAssignment));
    }

    @Test
    public void constraintsAreEqualIfSameMinesAndSquares() {
        MinesweeperConstraint other = new MinesweeperConstraint(mines, variables);
        assertTrue(this.constraint.equals(other));
    }

    @Test
    public void constraintsAreEqualIfSameMinesAndSimilarlyMadeSquares() {
        SquareSet otherSquares = new SquareSet(variables.width, variables.height);
        otherSquares.addAll(variables);
        MinesweeperConstraint other = new MinesweeperConstraint(mines, otherSquares);
        assertTrue(this.constraint.equals(other));
    }

    @Test
    public void constraintsAreNotEqualIfMinesDiffer() {
        MinesweeperConstraint other = new MinesweeperConstraint(mines + 1, variables);
        assertFalse(this.constraint.equals(other));
    }

    @Test
    public void constraintsAreNotEqualIfSquaresDiffer() {
        SquareSet otherSquares = new SquareSet(variables.width, variables.height);
        otherSquares.addAll(variables);
        otherSquares.add(new Square(0, 1));
        MinesweeperConstraint other = new MinesweeperConstraint(mines, otherSquares);
        assertFalse(this.constraint.equals(other));
    }

    @Test
    public void removingExistingSquareDecreasesIndicatorByGivenValue() {
        int reduction = 1;
        Square toRemove = variables.getSquares()[0];
        constraint.removeSquare(toRemove, reduction);
        assertEquals(mines - reduction, constraint.mineIndicator);
    }
}

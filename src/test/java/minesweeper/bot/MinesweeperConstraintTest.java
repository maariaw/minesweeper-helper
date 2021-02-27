
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import minesweeper.model.Square;
import minesweeper.structures.SquareSet;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MinesweeperConstraintTest {
    private Random rng;
    private int mines;
    private SquareSet variables;
    private HashMap<Square, Integer> correctAssignment;
    private MinesweeperConstraint constraint;
    
    @Before
    public void setUp() {
        this.rng = new Random(6332);
        this.mines = 0;
        this.correctAssignment = new HashMap<>();
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
        HashMap<Square, Integer> incorrectAssignment = new HashMap<>(correctAssignment);
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
        HashMap<Square, Integer> incompleteAssignment = new HashMap<>(correctAssignment);
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
}

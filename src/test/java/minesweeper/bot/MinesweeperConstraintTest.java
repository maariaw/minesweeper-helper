
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import minesweeper.model.Square;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MinesweeperConstraintTest {
    private Random rng;
    private int mines;
    private ArrayList<Square> variables;
    private HashMap<Square, Integer> correctAssignment;
    private MinesweeperConstraint constraint;
    
    @Before
    public void setUp() {
        this.rng = new Random(6332);
        this.mines = 0;
        this.correctAssignment = new HashMap<>();
        this.variables = new ArrayList<>();
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
        Square deviant = this.variables.get(0);
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
        Square missing = this.variables.get(0);
        HashMap<Square, Integer> incompleteAssignment = new HashMap<>(correctAssignment);
        incompleteAssignment.remove(missing);
        assertTrue(constraint.isSatisfied(incompleteAssignment));
    }
}


package minesweeper.structures;

import minesweeper.bot.MinesweeperConstraint;
import minesweeper.model.Square;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConstraintSetTest {
    ConstraintSet constraintSet;
    SquareSet squareSet;
    MinesweeperConstraint constraint;
    
    @Before
    public void setUp() {
        constraintSet = new ConstraintSet();
        squareSet = makeSquareSet();
        this.constraint = new MinesweeperConstraint(0, squareSet);
    }
    
    @Test
    public void newConstraintSetHasSizeZero() {
        assertEquals(0, constraintSet.size());
    }
    
    @Test
    public void addingConstraintIncreasesSetSize() {
        constraintSet.add(constraint);
        assertEquals(1, constraintSet.size());
    }
    
    @Test
    public void containsReturnsTrueForAddedConstraint() {
        constraintSet.add(constraint);
        assertTrue(constraintSet.contains(constraint));
    }
    
    @Test
    public void containsReturnsFalseForConstraintWithDifferentSquares() {
        constraintSet.add(constraint);
        MinesweeperConstraint newConstraint = new MinesweeperConstraint(0, makeSquareSet());
        assertFalse(constraintSet.contains(newConstraint));
    }
    
//    @Test
//    public void containsReturnsTrueForNewConstraintWithSameIndicatorAndSquares() {
//        constraintSet.add(constraint);
//        MinesweeperConstraint newConstraint = new MinesweeperConstraint(0, squareSet);
//        assertTrue(constraintSet.contains(newConstraint));
//    }
    
    private static SquareSet makeSquareSet() {
        SquareSet squareSet = new SquareSet(6, 6);
        for (int i = 0; i < 6; i++) {
            Square square = new Square(i, i);
            squareSet.add(square);
        }
        return squareSet;
    }
}

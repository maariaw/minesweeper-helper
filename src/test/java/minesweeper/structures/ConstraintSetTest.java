
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
    public void containsReturnsFalseForNotAddedConstraint() {
        constraintSet.add(constraint);
        MinesweeperConstraint other = new MinesweeperConstraint(0, squareSet);
        assertFalse(constraintSet.contains(other));
    }
    
    @Test
    public void addingHundredConstraintsMakesSizeHundred() {
        for (int i = 0; i < 100; i++) {
            constraintSet.add(new MinesweeperConstraint(i, squareSet));
        }
        assertEquals(100, constraintSet.size());
    }
    
    @Test
    public void containsReturnsTrueAfterAddingMoreConstraints() {
        constraintSet.add(constraint);
        for (int i = 0; i < 100; i++) {
            constraintSet.add(new MinesweeperConstraint(i, squareSet));
        }
        assertTrue(constraintSet.contains(constraint));
    }
    
    @Test
    public void createdCloneContainsAllConstraintsInOriginal() {
        MyList<MinesweeperConstraint> constraintList = new MyList<>();
        for (int i = 0; i < 100; i++) {
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(i, squareSet);
            constraintSet.add(newConstraint);
            constraintList.add(newConstraint);
        }
        ConstraintSet clone = constraintSet.createAClone();
        for (int i = 0; i < constraintList.size(); i++) {
            assertTrue(clone.contains(constraintList.get(i)));
        }
    }
    
    @Test
    public void removingExistingConstraintReducesSize() {
        constraintSet.add(constraint);
        constraintSet.remove(constraint);
        assertEquals(0, constraintSet.size());
    }
    
    @Test
    public void removingNonExistingConstraintDoesntReduceSize() {
        constraintSet.add(constraint);
        MinesweeperConstraint other = new MinesweeperConstraint(0, squareSet);
        constraintSet.remove(other);
        assertEquals(1, constraintSet.size());
    }
    
    @Test
    public void listReturnedByGetListContainsAllAddedConstraints() {
        MyList<MinesweeperConstraint> constraintList = new MyList<>();
        for (int i = 0; i < 100; i++) {
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(i, squareSet);
            constraintSet.add(newConstraint);
            constraintList.add(newConstraint);
        }
        MyList<MinesweeperConstraint> gotList = constraintSet.getList();
        for (int i = 0; i < constraintList.size(); i++) {
            assertTrue(gotList.contains(constraintList.get(i)));
        }
    }
    
    private static SquareSet makeSquareSet() {
        SquareSet squareSet = new SquareSet(6, 6);
        for (int i = 0; i < 6; i++) {
            Square square = new Square(i, i);
            squareSet.add(square);
        }
        return squareSet;
    }
}

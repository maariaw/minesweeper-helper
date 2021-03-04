
package minesweeper.structures;

import minesweeper.model.Square;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SquareSetTest {
    SquareSet set;
    int width;
    int height;
    Square[] squares;
    
    @Before
    public void setUp() {
        width = 53;
        height = 47;
        set = new SquareSet(width, height);
        int amount = 22;
        squares = new Square[amount];
        for (int i = 0; i < amount; i++) {
            Square newSquare = new Square(width - 1 - 2 * i, i);
            squares[i] = newSquare;
        }
    }
    
    @Test
    public void constructorSetsCorrectWidthAndHeight() {
        assertEquals(width, set.width);
        assertEquals(height, set.height);
    }
    
    @Test
    public void sizeStartsAtZero() {
        assertEquals(0, set.size());
    }
    
    @Test
    public void createdSetIsEmpty() {
        assertTrue(set.isEmpty());
    }
    
    @Test
    public void addingIncreasesSize() {
        set.add(squares[0]);
        assertEquals(1, set.size());
    }
    
    @Test
    public void addedSquareSetIncreasesSizeBySetSize() {
        SquareSet otherSet = new SquareSet(width, height);
        for (int i = 0; i < squares.length; i++) {
            otherSet.add(squares[i]);
        }
        set.addAll(otherSet);
        assertEquals(squares.length, set.size());
    }
    
    @Test
    public void getSquaresReturnsArrayWithSizeEqualToNumberOfAddedSquares() {
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
        }
        assertEquals(squares.length, set.getSquares().length);
    }
    
    @Test
    public void containsReturnsTrueForAddedSquare() {
        set.add(squares[0]);
        assertTrue(set.contains(squares[0]));
    }
    
    @Test
    public void containsReturnsFalseForNotAddedSquare() {
        set.add(squares[0]);
        assertFalse(set.contains(squares[1]));
    }
    
    @Test
    public void containsReturnsTrueForSquareAddedWithAddAll() {
        SquareSet otherSet = new SquareSet(width, height);
        for (int i = 0; i < squares.length; i++) {
            otherSet.add(squares[i]);
        }
        set.addAll(otherSet);
        assertTrue(set.contains(squares[0]));
    }
    
    @Test
    public void containsReturnsFalseForSquareNotAddedWithAddAll() {
        SquareSet otherSet = new SquareSet(width, height);
        for (int i = 0; i < squares.length - 1; i++) {
            otherSet.add(squares[i]);
        }
        set.addAll(otherSet);
        assertFalse(set.contains(squares[squares.length - 1]));
    }
    
    @Test
    public void removingExistingSquareDecreasesSize() {
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
        }
        set.remove(squares[0]);
        assertEquals(squares.length - 1, set.size());
    }
    
    @Test
    public void removingNonExistingSquareDoesntDecreaseSize() {
        for (int i = 1; i < squares.length; i++) {
            set.add(squares[i]);
        }
        set.remove(squares[0]);
        assertEquals(squares.length - 1, set.size());
    }
    
    @Test
    public void containsReturnsFalseForRemovedSquare() {
        set.add(squares[0]);
        set.remove(squares[0]);
        assertFalse(set.contains(squares[0]));
    }
    
    @Test
    public void popReturnsLatestAddedSquare() {
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
        }
        assertEquals(squares[squares.length - 1], set.pop());
    }
    
    @Test
    public void popReducesSizeByOne() {
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
        }
        set.pop();
        assertEquals(squares.length - 1, set.size());
    }
    
    @Test
    public void containsReturnsFalseForPoppedSquare() {
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
        }
        Square removed = set.pop();
        assertFalse(set.contains(removed));
    }
    
    @Test
    public void squareSetsWithEqualDimensionsAndSameSquaresAreEqual() {
        SquareSet other = new SquareSet(width, height);
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
            other.add(squares[i]);
        }
        assertTrue(set.equals(other));
    }
    
    @Test
    public void squareSetsWithDifferentDimensionsAreNotEqual() {
        SquareSet other = new SquareSet(width + 1, height + 1);
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
            other.add(squares[i]);
        }
        assertFalse(set.equals(other));
    }
    
    @Test
    public void squareSetsWithDifferentSquaresAreNotEqual() {
        SquareSet other = new SquareSet(width, height);
        for (int i = 0; i < squares.length; i++) {
            set.add(squares[i]);
            other.add(squares[i]);
        }
        other.pop();
        assertFalse(set.equals(other));
    }
}

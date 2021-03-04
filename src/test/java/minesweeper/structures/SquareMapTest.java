
package minesweeper.structures;

import minesweeper.model.Square;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SquareMapTest {
    SquareMap<Integer> map;
    int width;
    int height;
    
    @Before
    public void setUp() {
        width = 57;
        height = 54;
        map = new SquareMap<Integer>(width, height);
    }
    
    @Test
    public void sizeIsZeroWhenCreated() {
        assertEquals(0, map.size());
    }
    
    @Test
    public void puttingNewKeyIncreasesSize() {
        Square square = new Square(0, 0);
        map.put(square, 0);
        assertEquals(1, map.size());
    }
    
    @Test
    public void puttingExistingKeyDoesntIncreaseSize() {
        Square square = new Square(0, 0);
        map.put(square, 0);
        map.put(square, 1);
        assertEquals(1, map.size());
    }
    
    @Test
    public void getReturnsCurrentValueForKey() {
        Square square = new Square(0, 0);
        map.put(square, 0);
        map.put(square, 1);
        assertEquals((Integer) 1, map.get(square));
    }
    
    @Test
    public void getReturnsNullIfNoMapping() {
        Square square = new Square(0, 0);
        assertNull(map.get(square));
    }
    
    @Test
    public void containsKeyReturnsTrueIfMappingPut() {
        Square square = new Square(0, 0);
        map.put(square, 0);
        assertTrue(map.containsKey(square));
    }
    
    @Test
    public void containsKeyReturnsFalseIfMappingNotPut() {
        Square square = new Square(0, 0);
        assertFalse(map.containsKey(square));
    }
    
    @Test
    public void keySetIncludesAllAddedKeys() {
        Square[] squares = makeSquares();
        for (int i = 0; i < squares.length; i++) {
            map.put(squares[i], i);
        }
        for (int i = 0; i < squares.length; i++) {
            assertTrue(map.keySet().contains(squares[i]));
        }
    }
    
    @Test
    public void createdCloneContainsAllKeysFromOriginal() {
        Square[] squares = makeSquares();
        for (int i = 0; i < squares.length; i++) {
            map.put(squares[i], i);
        }
        SquareMap<Integer> clone = map.createAClone();
        for (int i = 0; i < map.keySet().size(); i++) {
            assertTrue(clone.containsKey(map.keySet().get(i)));
        }
    }
    
    private static Square[] makeSquares() {
        Square[] squares = new Square[20];
        for (int i = 0; i < 20; i++) {
            Square square = new Square(i, 20 - i);
            squares[i] = square;
        }
        return squares;
    }
}

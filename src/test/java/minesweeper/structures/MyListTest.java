
package minesweeper.structures;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyListTest {
    MyList<Integer> list;
    
    @Before
    public void setUp() {
        this.list = new MyList<>();
    }
    
    @Test
    public void newListIsEmpty() {
        assertEquals(0, list.size());
    }
    
    @Test
    public void addingIncreasesListSize() {
        list.add(6);
        assertEquals(1, list.size());
    }
    
    @Test
    public void addingTwentyMakesListSizeTwenty() {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        assertEquals(20, list.size());
    }
    
    @Test
    public void getReturnsCorrectValueFromIndex() {
        for (int i = 0; i < 20; i++) {
            list.add(i + 7);
        }
        assertEquals((Integer) 9, list.get(2));
    }
    
    @Test
    public void removeDecreasesListSizeByOne() {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        list.remove(5);
        assertEquals(19, list.size());
    }
    @Test
    public void removeShiftsItemsLeft() {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        list.remove(5);
        assertEquals((Integer) 6, list.get(5));
    }
    
    @Test
    public void containsReturnsTrueForAddedItem() {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        assertTrue(list.contains(17));
    }
    
    @Test
    public void containsReturnsFalseForNotAddedItem() {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        assertFalse(list.contains(27));
    }
    
    @Test
    public void getContentReturnsAnArray() {
        Integer[] comparison = new Integer[0];
        assertArrayEquals(comparison, list.getContent());
    }
}

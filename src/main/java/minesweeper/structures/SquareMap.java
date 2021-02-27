
package minesweeper.structures;

import minesweeper.model.Square;

public class SquareMap<Type> {
    private MyCouple[][] map;
    private int size;
    private int width;
    private int height;
    
    public SquareMap(int width, int height) {
        this(width, height, new MyCouple[width][height], 0);
    }
    
    public SquareMap(int width, int height, MyCouple[][] map, int size) {
        this.map = map;
        this.size = size;
        this.width = width;
        this.height = height;
    }
    
    public void put(Square square, Type item) {
        if (this.map[square.getX()][square.getY()] == null) {
            size++;
        }
        this.map[square.getX()][square.getY()] = new MyCouple(square, item);
    }
    
    public Type get(Square square) {
        return (Type) this.map[square.getX()][square.getY()].getValue();
    }
    
    public boolean containsKey(Square square) {
        return this.map[square.getX()][square.getY()] != null;
    }
    
    public int size() {
        return size;
    }
    
    public SquareMap createAClone() {
        MyCouple[][] newMap = new MyCouple[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map[x][y] == null) {
                    continue;
                }
                newMap[x][y] = new MyCouple(map[x][y].getKey(), map[x][y].getValue());
            }
        }
        return new SquareMap(width, height, newMap, size);
    }
    
    public MyList<Square> keySet() {
        MyList<Square> keySet = new MyList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map[x][y] != null) {
                    keySet.add((Square) map[x][y].getKey());
                }
            }
        }
        return keySet;
    }

    public boolean remove(Square square) {
        if (this.map[square.getX()][square.getY()] == null) {
            return false;
        }
        this.map[square.getX()][square.getY()] = null;
        size--;
        return true;
    }
}

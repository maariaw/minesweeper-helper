
package minesweeper.structures;

import minesweeper.model.Square;

public class SquareSet {
    public boolean[][] containGrid;
    private Square[] squares;
    private int next;
    public int width;
    public int height;

    public SquareSet(int width, int height) {
        this.containGrid = new boolean[width][height];
        this.squares = new Square[width * height];
        this.next = 0;
        this.width = width;
        this.height = height;
    }

    public void add(Square square) {
        if (!containGrid[square.getX()][square.getY()]) {
            containGrid[square.getX()][square.getY()] = true;
            squares[next] = square;
            next++;
        }
    }

    public void addAll(SquareSet squareSet) {
        for (Square square : squareSet.getSquares()) {
            add(square);
        }
    }

    public int size() {
        return next;
    }

    public Square[] getSquares() {
        Square[] realSquares = new Square[next];
        for (int i = 0; i < next; i++) {
            realSquares[i] = this.squares[i];
        }
        return realSquares;
    }

    public boolean contains(Square square) {
        return containGrid[square.getX()][square.getY()];
    }

    public boolean remove(Square square) {
        if (contains(square)) {
            Square[] newSquares = new Square[squares.length];
            int newNext = 0;
            for (Square s : this.getSquares()) {
                if (s.equals(square)) {
                    continue;
                }
                newSquares[newNext] = s;
                newNext++;
            }
            this.squares = newSquares;
            this.next = newNext;
            this.containGrid[square.getX()][square.getY()] = false;
            return true;
        }
        return false;
    }
    
    public Square pop() {
        if (next == 0) {
            return null;
        }
        Square last = squares[next - 1];
        next--;
        containGrid[last.getX()][last.getY()] = false;
        return last;
    }

    public boolean isEmpty() {
        return next == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SquareSet other = (SquareSet) obj;
        if (this.width != other.width) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                if (this.containGrid[x][y] != other.containGrid[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
}

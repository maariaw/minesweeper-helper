
package minesweeper.structures;

import minesweeper.bot.MinesweeperConstraint;

public class ConstraintSet {
    private MyList<MinesweeperConstraint>[] constraints;
    private int size;

    public ConstraintSet() {
        this(new MyList[101], 0);
    }
    
    public ConstraintSet(MyList<MinesweeperConstraint>[] constraints, int size) {
        this.constraints = constraints;
        this.size = size;
    }

    public void add(MinesweeperConstraint constraint) {
        MyList<MinesweeperConstraint> savedConstraints = getListByValue(constraint);
        for (int i = 0; i < savedConstraints.size(); i++) {
            if (savedConstraints.get(i).equals(constraint)) {
                return;
            }
        }
        savedConstraints.add(constraint);
        size++;

        if (1.0 * size / constraints.length > 0.75) {
            grow();
        }
    }

    private void grow() {
        MyList<MinesweeperConstraint>[] newTable = new MyList[constraints.length * 2];
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] == null) {
                continue;
            }
            copy(newTable, i);
        }
        constraints = newTable;
    }
    
    public ConstraintSet createAClone() {
        MyList<MinesweeperConstraint>[] newTable = new MyList[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] == null) {
                continue;
            }
            copy(newTable, i);
        }
        return new ConstraintSet(newTable, size);
    }
    
    private void copy(MyList<MinesweeperConstraint>[] newTable, int fromIndex) {
        for (int i = 0; i < constraints[fromIndex].size(); i++) {
            MinesweeperConstraint constraint = constraints[fromIndex].get(i);
            int hashValue = Math.abs(constraint.hashCode() % newTable.length);
            if (newTable[hashValue] == null) {
                newTable[hashValue] = new MyList<>();
            }
            newTable[hashValue].add(constraint);
        }
    }

    private MyList<MinesweeperConstraint> getListByValue(MinesweeperConstraint constraint) {
        int hashValue = Math.abs(constraint.hashCode() % constraints.length);
        if (constraints[hashValue] == null) {
            constraints[hashValue] = new MyList<>();
        }
        return constraints[hashValue];
    }

    public boolean remove(MinesweeperConstraint constraint) {
        MyList<MinesweeperConstraint> constraintsAtIndex = getListByValue(constraint);

        for (int i = 0; i < constraintsAtIndex.size(); i++) {
            if (constraintsAtIndex.get(i).equals(constraint)) {
                constraintsAtIndex.remove(i);
                size--;
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(MinesweeperConstraint constraint) {
        MyList<MinesweeperConstraint> constraintsAtIndex = getListByValue(constraint);
        return constraintsAtIndex.contains(constraint);
    }

    public int size() {
        return size;
    }

    public MyList<MinesweeperConstraint> getList() {
        MyList<MinesweeperConstraint> fullList = new MyList<>();
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] != null) {
                MyList<MinesweeperConstraint> listAtIndex = constraints[i];
                for (int j = 0; j < listAtIndex.size(); j++) {
                    fullList.add(listAtIndex.get(j));
                }
            }
        }
        return fullList;
    }
}

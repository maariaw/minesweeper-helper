
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import minesweeper.model.Square;

/**
 * A constraint satisfaction problem solver for Minesweeper.
 * 
 * <p>
 * A CSP is used to frame the Minesweeper game as a constraint satisfaction problem
 * and generating solutions to a board situation. The variables are Square objects
 * that represent squares on a Minesweeper board that may or may not be mines.
 * The domains are Integers 0 for no mine and 1 for a mine. The constraints are
 * MinesweeperConstraint objects, that represent the basic principle that the sum
 * of mines around an opened square must match the number on a square.
 * </p>
 * 
 * <p>
 * Given these three elements, the backtrackingSearch() method iterates through
 * possible mine configurations to find ones that satisfy all constraints. The
 * search is started with the findSafeSolutions() method, which then returns a
 * summary of the solutions.
 * </p>
 */
public class CSP {
    private ArrayList<Square> variables;
    private HashMap<Square, ArrayList<Integer>> domains;
    private HashMap<Square, ArrayList<MinesweeperConstraint>> constraints;
    private HashSet<MinesweeperConstraint> constraintSet;
    private HashSet<Square> constrainedVariables;
    private ArrayList<Square> safeSquares;
    private ArrayList<Square> mineSquares;

    public CSP(ArrayList<Square> variables, HashMap<Square, ArrayList<Integer>> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new HashMap<>();
        this.safeSquares = new ArrayList<>();
        this.mineSquares = new ArrayList<>();
        this.constraintSet = new HashSet<>();
    }
    
    /**
     * Add a constraint by linking it with all the variables it concerns
     * @param constraint A constraint to be added
     */
    public boolean addConstraint(ArrayList<Square> squares, int mineIndicator) {
        ArrayList<Square> updatedSquareList = new ArrayList<>();
        int updatedMineCount = mineIndicator;
        for (Square square : squares) {
            if (domains.get(square).size() == 1) {
                updatedMineCount -= domains.get(square).get(0);
            } else {
                updatedSquareList.add(square);
            }
        }
        if (updatedMineCount == 0) {
            for (Square square : updatedSquareList) {
                reduceDomain(square, 1);
            }
            System.out.println("Throwing out zero mine constraint");
            return false;
        } else if (updatedMineCount == updatedSquareList.size()) {
            for (Square square : updatedSquareList) {
                reduceDomain(square, 0);
            }
            System.out.println("Throwing out all mine constraint");
            return false;
        } else {
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(updatedMineCount, updatedSquareList);
            for (Square square : updatedSquareList) {
                if (!constraints.containsKey(square)) {
                    constraints.put(square, new ArrayList<>());
                    System.out.println("Adding " + square.locationString() + " to constraint map");
                }
                this.constraints.get(square).add(newConstraint);
            }
            constraintSet.add(newConstraint);
            System.out.println("Added constraint to CSP: " + newConstraint.toString());
            return true;
        }
    }

    /**
     * Check if the all the constraints of a given variable are satisfied with
     * the given assignment
     * @param variable Check the constraints of this variable
     * @param assignment The current assignment of domain values to variables
     * @return True if all constraints of this variable are satisfied
     */
    public boolean isConsistent(Square variable, HashMap<Square, Integer> assignment) {
        if (!constraints.containsKey(variable)) {
            return true;
        }
        for (MinesweeperConstraint constraint : this.constraints.get(variable)) {
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Iterate through possible assignments of domain values to variables to find
     * assignments that satisfy all constraints
     * @param assignment
     * @param solutions 
     */
    private void backtrackingSearch(HashMap<Square, Integer> assignment, ArrayList<HashMap<Square, Integer>> solutions) {
        if (assignment.size() == this.constrainedVariables.size()) {
            solutions.add(new HashMap(assignment));
            if (solutions.size() < 10) {
                System.out.println("Found solution number " + solutions.size());
            }
            if (solutions.size() == 100000) {
                System.out.println("100 000 solutions!!");
            }
            if (solutions.size() == 1000000) {
                System.out.println("Literally a million solutions!!!!");
            }
            return;
        }

        Square unAssigned = null;
        for (Square square : constrainedVariables) {
            if (!assignment.containsKey(square)) {
                unAssigned = square;
                break;
            }
        }

        for (Integer domainValue : this.domains.get(unAssigned)) {
            HashMap<Square, Integer> localAssignment = new HashMap(assignment);
            localAssignment.put(unAssigned, domainValue);
            if (isConsistent(unAssigned, localAssignment)) {
//                    System.out.println("Assigning square " + unAssigned.locationString() + " value " + domainValue);
                backtrackingSearch(localAssignment, solutions);
            }
        }
    }

    /**
     * Initialize the solutions and assignment and start the backtracking search.
     * @return A list of assignments that satisfy current constraints
     */
    public ArrayList<HashMap<Square, Integer>> startSearch() {
        ArrayList<HashMap<Square, Integer>> solutions = new ArrayList<>();
        HashMap<Square, Integer> assignment = new HashMap<>();
        System.out.println("Total of " + constrainedVariables.size() + " squares of interest");
        for (Square square : constrainedVariables) {
            if (domains.get(square).size() == 1) {
                assignment.put(square, domains.get(square).get(0));
            }
        }
        System.out.println(assignment.size() + " squares assigned by default");
        backtrackingSearch(assignment, solutions);
        return solutions;
    }

    /**
     * Perform the backtracking search and summarize the findings.
     * @param constrainedVariables The set of Squares that have constraints
     * @return A mapping of Squares to the percentage of solutions that assign
     * them as mines
     */
    public HashMap<Square, Integer> findSafeSolutions(HashSet<Square> constrainedVariables) {
        this.constrainedVariables = constrainedVariables;
        ArrayList<HashMap<Square, Integer>> solutions = startSearch();
        System.out.println("Search completed, creating summary...");

        // Find the squares that are consistently mines or not mines
        HashMap<Square, Integer> solutionSummary = new HashMap<>();
        for (Square square : constrainedVariables) {
            if (domains.get(square).size() == 1) {
                int summary = domains.get(square).get(0) * 100;
                solutionSummary.put(square, summary);
                System.out.println(square.locationString() + " is summarized as " + summary);
                continue;
            }
            int mineSolutions = 0;
            for (HashMap solution : solutions) {
                if (solution.get(square).equals(1)) {
                    mineSolutions++;
                }
            }
            if (mineSolutions == 0) {
                solutionSummary.put(square, 0);
                reduceDomain(square, 1);
                System.out.println(square.locationString() + " is not a mine, value 0");
            } else if (mineSolutions == solutions.size()) {
                solutionSummary.put(square, 100);
                reduceDomain(square, 0);
                System.out.println(square.locationString() + " is a mine, value 100");
            } else {
                int minePercentage = mineSolutions * 100 / solutions.size();
                solutionSummary.put(square, minePercentage);
                System.out.println(square.locationString() + " is a mine with a " + minePercentage + "% chance");
            }
        }
        return solutionSummary;
    }

    public HashMap<Square, ArrayList<MinesweeperConstraint>> getConstraints() {
        return constraints;
    }

    public ArrayList<Square> getVariables() {
        return variables;
    }

    public void setConstrainedVariables(HashSet<Square> constrainedVariables) {
        this.constrainedVariables = constrainedVariables;
    }

    private void reduceDomains(ArrayList<Square> squaresToReduce, int domainToRemove) {
        for (Square square : squaresToReduce) {
            reduceDomain(square, domainToRemove);
        }
    }

    public void reduceDomain(Square square, int domainToRemove) {
        if (domains.get(square).size() == 1) {
            if (domains.get(square).get(0) == domainToRemove) {
                System.out.println("--------------- CONFLICTING REDUCTIONS -----------------");
            }
            return;
        }
        if (domainToRemove == 1) {
            safeSquares.add(square);
        } else {
            mineSquares.add(square);
        }
        this.domains.get(square).remove(Integer.valueOf(domainToRemove));
        this.updateKnownSquaresConstraints(square);
        System.out.println("Square " + square.locationString() + " discarded domain " + domainToRemove);
    }

    public Square getSafeSquare() {
        int numberOfSafes = safeSquares.size();
        if (numberOfSafes == 0) {
            return null;
        }
        Square safe = safeSquares.remove(numberOfSafes - 1);
        numberOfSafes--;
        while (safe != null && safe.isOpened()) {
            if (numberOfSafes > 0) {
                safe = safeSquares.remove(numberOfSafes - 1);
                numberOfSafes--;
            } else {
                safe = null;
            }
        }
        return safe;
    }

    public Square getFlaggableSquare() {
        int numberOfMineSquares = mineSquares.size();
        if (numberOfMineSquares == 0) {
            return null;
        }
        Square mineSquare = mineSquares.remove(numberOfMineSquares - 1);
        numberOfMineSquares--;
        while (mineSquare != null && mineSquare.isFlagged()) {
            if (numberOfMineSquares > 0) {
                mineSquare = mineSquares.remove(numberOfMineSquares - 1);
                numberOfMineSquares--;
            } else {
                mineSquare = null;
            }
        }
        return mineSquare;
    }

    public void updateKnownSquaresConstraints(Square square) {
        if (constraints.containsKey(square) && domains.get(square).size() == 1) {
            for (MinesweeperConstraint constraint : constraints.get(square)) {
                constraint.removeSquare(square, domains.get(square).get(0));
                System.out.println("Updated constraints for " + square.locationString());
            }
            constraints.remove(square);
        }
    }

    public boolean updateConstraints() {
        HashSet<MinesweeperConstraint> constraintsToCheck = new HashSet<>(constraintSet);
        System.out.println(constraintsToCheck.size() + " constraints to go through");
        int nonTrivial = 0;
        for (MinesweeperConstraint constraint : constraintsToCheck) {
            if (constraint.triviality() == -1) {
                nonTrivial++;
                continue;
            }
            constraintSet.remove(constraint);
            ArrayList<Square> squares = new ArrayList<>(constraint.getSquares());
            if (constraint.triviality() == 0) {
                for (Square square : squares) {
                    reduceDomain(square, 1);
                }
            } else if (constraint.triviality() == 0) {
                for (Square square : squares) {
                    reduceDomain(square, 0);
                }
            }
        }
        if (nonTrivial == constraintsToCheck.size()) {
            System.out.println("Current non-trivial constraints:");
            for (MinesweeperConstraint constraint : constraintsToCheck) {
                System.out.println("   " + constraint.toString());
            }
        }
        return nonTrivial != constraintsToCheck.size();
    }
}

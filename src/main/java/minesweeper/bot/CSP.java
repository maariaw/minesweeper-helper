
package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
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
    private HashSet<Square> constrainedVariables;

    public CSP(ArrayList<Square> variables, HashMap<Square, ArrayList<Integer>> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new HashMap<>();
//        for (Square variable : variables) {
//            this.constraints.put(variable, new ArrayList<>());
//            if (!this.domains.containsKey(variable)) {
//                System.out.println("Every variable should have a domain assigned");
//            }
//        }
    }
    
    /**
     * Add a constraint by linking it with all the variables it concerns
     * @param constraint A constraint to be added
     */
    public boolean addConstraint(MinesweeperConstraint constraint) {
        ArrayList<Square> squares = constraint.getSquares();

        if (constraint.mineIndicator == 0) {
            for (Square square : squares) {
                reduceDomain(square, 1);
                System.out.println("Throwing out zero mine constraint");
            }
            return false;
        } else if (constraint.mineIndicator == squares.size()) {
            for (Square square : squares) {
                reduceDomain(square, 0);
            }
            System.out.println("Throwing out all mine constraint");
            return false;
        } else {
            int updatedMineCount = constraint.mineIndicator;
            ArrayList<Square> updatedSquareList = new ArrayList<>();
            for (Square square : squares) {
                if (domains.get(square).size() == 1) {
                    updatedMineCount -= domains.get(square).get(0);
                } else {
                    updatedSquareList.add(square);
                }
            }
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(updatedMineCount, updatedSquareList);
            for (Square square : updatedSquareList) {
                if (!constraints.containsKey(square)) {
                    constraints.put(square, new ArrayList<>());
                    System.out.println("Adding " + square.locationString() + " to constraint map");
                }
                this.constraints.get(square).add(newConstraint);
            }
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
        System.out.println("Checking consistency for square " + variable.locationString());
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

        HashMap<Square, Integer> localAssignment = new HashMap(assignment);
        ArrayList<Integer> unAssignedDomains = this.domains.get(unAssigned);
        if (unAssignedDomains.size() == 1) {
            localAssignment.put(unAssigned, unAssignedDomains.get(0));
        } else {
            for (Integer domainValue : unAssignedDomains) {
            localAssignment.put(unAssigned, domainValue);
                if (isConsistent(unAssigned, localAssignment)) {
                    backtrackingSearch(localAssignment, solutions);
                }
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
                System.out.println(square.locationString() + " is not a mine");
            } else if (mineSolutions == solutions.size()) {
                solutionSummary.put(square, 100);
                reduceDomain(square, 0);
                System.out.println(square.locationString() + " is a mine");
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

    private void reduceDomain(Square square, int domainToRemove) {
        if (domains.get(square).size() == 1) {
            if (domains.get(square).get(0) == domainToRemove) {
                System.out.println("--------------- CONFLICTING REDUCTIONS -----------------");
            }
            return;
        }
        this.domains.get(square).remove(Integer.valueOf(domainToRemove));
        System.out.println("Square " + square.locationString() + " discarded domain " + domainToRemove);
    }

    public void updateConstraints() {
        System.out.println("Updating constraints");
        // A list to track the constraints that have been handled, to not add duplicates
        HashSet<MinesweeperConstraint> handledConstraints = new HashSet<>();
        // A list of the constraints made by simplifying existing constraints
        HashSet<MinesweeperConstraint> newConstraints = new HashSet<>();
        // Going through all the squares and their attached constraints
        for (Square square : constraints.keySet()) {
            // If the square has just one domain, it doesn't need constraints
            if (domains.get(square).size() == 1) {
                continue;
            }
            for (MinesweeperConstraint constraint : constraints.get(square)) {
                if (!handledConstraints.contains(constraint)) {
                    ArrayList<Square> updatedVariables = new ArrayList<>();
                    int updatedMineCount = constraint.mineIndicator;
                    // If a square in the constraint has just one domain, it can be
                    // left out of the constraint, and the sum of mines adjusted.
                    // Unknown squares will be in the constraint
                    for (Square variable : constraint.getSquares()) {
                        if (domains.get(variable).size() == 1) {
                            updatedMineCount -= domains.get(variable).get(0);
                        } else {
                            updatedVariables.add(variable);
                        }
                    }
                    MinesweeperConstraint newConstraint =
                            new MinesweeperConstraint(updatedMineCount, updatedVariables);
                    newConstraints.add(newConstraint);
                    handledConstraints.add(constraint);
                }
            }
        }
        // Creating new constraint map
        this.constraints = new HashMap<>();
        for (MinesweeperConstraint newConstraint : newConstraints) {
            this.addConstraint(newConstraint);
        }
        System.out.println("Updated constraints");
    }
}

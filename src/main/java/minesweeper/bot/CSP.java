
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
        for (Square variable : variables) {
            this.constraints.put(variable, new ArrayList<>());
            if (!this.domains.containsKey(variable)) {
                System.out.println("Every variable should have a domain assigned");
            }
        }
    }
    
    /**
     * Add a constraint by linking it with all the variables it concerns
     * @param constraint A constraint to be added
     */
    public void addConstraint(MinesweeperConstraint constraint) {
        ArrayList<Square> squares = constraint.getSquares();
        for (Square square : squares) {
            if (!this.variables.contains(square)) {
                System.out.println("Variable in constraint not in CSP");
            } else {
                this.constraints.get(square).add(constraint);
                System.out.println("Added constraint to CSP: " + constraint.toString());
            }
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

        ArrayList<Integer> unAssignedDomains = this.domains.get(unAssigned);
        for (Integer domainValue : unAssignedDomains) {
            HashMap<Square, Integer> localAssignment = new HashMap(assignment);
            localAssignment.put(unAssigned, domainValue);
            if (unAssignedDomains.size() == 1 || isConsistent(unAssigned, localAssignment)) {
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
                domains.get(square).remove(Integer.valueOf(1));
                System.out.println(square.locationString() + " is not a mine");
            } else if (mineSolutions == solutions.size()) {
                solutionSummary.put(square, 100);
                domains.get(square).remove(Integer.valueOf(0));
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
}


package minesweeper.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.lang.Integer;

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
 * 
 * @param <V> Variable value, in the case of Minesweeper this is a Square object
 * @param <D> Domain value, in Minesweeper this is an Integer (0 for no mine, 1 for a mine)
 */
public class CSP<V, D> {
    private ArrayList<V> variables;
    private HashMap<V, ArrayList<D>> domains;
    private HashMap<V, ArrayList<Constraint<V, D>>> constraints;
    private HashSet<V> constrainedVariables;

    public CSP(ArrayList<V> variables, HashMap<V, ArrayList<D>> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new HashMap<>();
        for (V variable : variables) {
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
    public void addConstraint(Constraint constraint) {
        ArrayList<V> vars = constraint.getVariables();
        for (V variable : vars) {
            if (!this.variables.contains(variable)) {
                System.out.println("Variable in constraint not in CSP");
            } else {
                this.constraints.get(variable).add(constraint);
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
    public boolean isConsistent(V variable, HashMap<V, D> assignment) {
        for (Constraint constraint : this.constraints.get(variable)) {
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
    private void backtrackingSearch(HashMap<V, D> assignment, ArrayList<HashMap> solutions) {
        if (assignment.size() == this.constrainedVariables.size()) {
            solutions.add(new HashMap(assignment));
            return;
        }

        ArrayList<V> unassigned = this.constrainedVariables.stream()
                .filter((variable) -> (!assignment.containsKey(variable)))
                .collect(Collectors.toCollection(ArrayList::new));

        V first = unassigned.get(0);
        for (D domainValue : this.domains.get(first)) {
            HashMap<V, D> localAssignment = new HashMap(assignment);
            localAssignment.put(first, domainValue);
            if (isConsistent(first, localAssignment)) {
                backtrackingSearch(localAssignment, solutions);
            }
        }
    }

    /**
     * Initialize the solutions and assignment and start the backtracking search.
     * @return A list of assignments that satisfy current constraints
     */
    public ArrayList<HashMap> startSearch() {
        ArrayList<HashMap> solutions = new ArrayList<>();
        HashMap<V, D> assignment = new HashMap<>();
        backtrackingSearch(assignment, solutions);
        return solutions;
    }

    /**
     * Perform the backtracking search and summarize the findings.
     * @param constrainedVariables The set of Squares that have constraints
     * @return A mapping of Squares to the percentage of solutions that assign
     * them as mines
     */
    public HashMap<V, D> findSafeSolutions(HashSet<V> constrainedVariables) {
        this.constrainedVariables = constrainedVariables;
        ArrayList<HashMap> solutions = startSearch();

        // Find the squares that are consistently mines or not mines
        HashMap<V, D> solutionSummary = new HashMap<>();
        for (V square : constrainedVariables) {
            int mineSolutions = 0;
            for (HashMap solution : solutions) {
                if (solution.get(square).equals(1)) {
                    mineSolutions++;
                }
            }
            if (mineSolutions == 0) {
                solutionSummary.put(square, (D) Integer.valueOf(0));
                domains.get(square).remove((D) Integer.valueOf(1));
            } else if (mineSolutions == solutions.size()) {
                solutionSummary.put(square, (D) Integer.valueOf(100));
                domains.get(square).remove((D) Integer.valueOf(0));
            } else {
                int minePercentage = mineSolutions * 100 / solutions.size();
                solutionSummary.put(square, (D) Integer.valueOf(minePercentage));
            }
        }
        return solutionSummary;
    }

    public HashMap<V, ArrayList<Constraint<V, D>>> getConstraints() {
        return constraints;
    }

    public ArrayList<V> getVariables() {
        return variables;
    }

    public void setConstrainedVariables(HashSet<V> constrainedVariables) {
        this.constrainedVariables = constrainedVariables;
    }
}

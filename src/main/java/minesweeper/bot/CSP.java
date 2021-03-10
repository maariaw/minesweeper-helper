
package minesweeper.bot;

import minesweeper.model.Square;
import minesweeper.structures.ConstraintSet;
import minesweeper.structures.MyList;
import minesweeper.structures.SquareMap;
import minesweeper.structures.SquareSet;

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
    private SquareSet variables;
    private SquareMap<int[]> domains;
    private SquareMap<MyList<MinesweeperConstraint>> constraints;
    private ConstraintSet constraintSet;
    private SquareSet constrainedVariables;
    private SquareSet safeSquares;
    private SquareSet mineSquares;
    private long solutionCount;
    private SquareMap<Long> solutionSummary;

    public CSP(SquareSet variables, SquareMap<int[]> domains) {
        this.variables = variables;
        this.domains = domains;
        this.constraints = new SquareMap<>(variables.width, variables.height);
        this.safeSquares = new SquareSet(variables.width, variables.height);
        this.mineSquares = new SquareSet(variables.width, variables.height);
        this.constraintSet = new ConstraintSet();
    }
    
    /**
     * Add a constraint by linking it with all the variables it concerns.
     *
     * This method simplifies the constraint by checking for already known squares
     * that can be left out of the constraint, adjusting the mine count accordingly.
     * After that it checks if the constraint indicates that none of the squares are
     * mines or all of them are, in which case that is recorded for each square.
     * Otherwise it saves the constraint to CSP.
     *
     * @param squares The squares that this constraint concerns
     * @param mineIndicator The number on the constraining open square
     * @return True if a constraint was added and not thrown away as trivial
     * @see #reduceDomain(minesweeper.model.Square, int)
     */
    public boolean addConstraint(SquareSet squares, int mineIndicator) {
        SquareSet updatedSquareList = new SquareSet(variables.width, variables.height);
        int updatedMineCount = mineIndicator;
        for (Square square : squares.getSquares()) {
            // Is there only one possible solution to this square?
            if (domains.get(square).length == 1) {
                // If this square can only be a mine (value 1), the remaining
                // squares will have one less mines among them
                updatedMineCount -= domains.get(square)[0];
            } else {
                // If the square is not known, it will be added in the constraint
                updatedSquareList.add(square);
            }
        }
        if (updatedMineCount == 0) {
            // None of the squares are mines
            for (Square square : updatedSquareList.getSquares()) {
                reduceDomain(square, 1);
            }
            return false;
        } else if (updatedMineCount == updatedSquareList.size()) {
            // All of the squares are mines
            for (Square square : updatedSquareList.getSquares()) {
                reduceDomain(square, 0);
            }
            return false;
        } else {
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(updatedMineCount, updatedSquareList);
            // The new constraint will be linked to every square it concerns
            for (Square square : updatedSquareList.getSquares()) {
                if (!constraints.containsKey(square)) {
                    constraints.put(square, new MyList<>());
                }
                this.constraints.get(square).add(newConstraint);
            }
            constraintSet.add(newConstraint);
            return true;
        }
    }

    /**
     * Check if the all the constraints of a given variable are satisfied with
     * the given assignment.
     *
     * @param variable Check the constraints of this variable
     * @param assignment The current assignment of domain values to variables
     * @return True if all constraints of this variable are satisfied
     * @see MinesweeperConstraint#isSatisfied(minesweeper.structures.SquareMap)
     */
    public boolean isConsistent(Square variable, SquareMap<Integer> assignment) {
        if (!constraints.containsKey(variable)) {
            // If the variable in question does not have constraints, the assignment
            // is satisfied
            return true;
        }
        MyList<MinesweeperConstraint> constraintList = this.constraints.get(variable);
        for (int i = 0; i < constraintList.size(); i++) {
            // Going through every constraint linked to this square
            MinesweeperConstraint constraint = constraintList.get(i);
            if (!constraint.isSatisfied(assignment)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Iterate through possible assignments of domain values to variables to find
     * assignments that satisfy all constraints.
     *
     * @param assignment A mapping of integers to squares representing the solution in the works
     * @see #isConsistent(minesweeper.model.Square, minesweeper.structures.SquareMap)
     */
    private void backtrackingSearch(SquareMap<Integer> assignment) {
        if (assignment.size() == this.constrainedVariables.size()) {
            // If all the squares that are currently of interest have a number
            // (0 for no mine or 1 for a mine) assigned, a solution has been found
            // The recursion starts folding back.
            addSolution(assignment);
            return;
        }

        // Looking for a square that has not yet been assigned
        Square unAssigned = null;
        for (Square square : constrainedVariables.getSquares()) {
            if (!assignment.containsKey(square)) {
                unAssigned = square;
                break;
            }
        }

        // Check for consistency with both possible values in turn
        for (Integer domainValue : this.domains.get(unAssigned)) {
            assignment.put(unAssigned, domainValue);
            if (isConsistent(unAssigned, assignment)) {
                // Here the magic of recursion happens
                backtrackingSearch(assignment);
            }
        }
        // The square must be removed from the assignment before backtracking
        // happens
        assignment.remove(unAssigned);
    }

    /**
     * Initialize the solutions and assignment and start the backtracking search.
     *
     * The solutions are a mapping of squares to the amount of solutions that
     * assign them as mines. The assignment maps a square to either 1 if the
     * square is a mine in the solution, or to 0 if not.
     *
     * @see #backtrackingSearch(minesweeper.structures.SquareMap, minesweeper.structures.MyList)
     */
    public void startSearch() {
        this.solutionCount = 0;
        this.solutionSummary = new SquareMap<>(variables.width, variables.height);
        SquareMap<Integer> assignment = new SquareMap<>(variables.width, variables.height);
        // First any known squares are added to the assignment, so they don't
        // unnecessarily bloat the backtracking
        for (Square square : constrainedVariables.getSquares()) {
            // Also initialising solution summary
            solutionSummary.put(square, (long) 0);
            if (domains.get(square).length == 1) {
                assignment.put(square, domains.get(square)[0]);
            }
        }
        backtrackingSearch(assignment);
    }

    /**
     * Perform the backtracking search and summarize the findings.
     *
     * @param constrainedVariables The set of Squares that have constraints
     * @return A mapping of Squares to the percentage of solutions that assign
     * them as mines
     * @see #startSearch()
     */
    public SquareMap<Integer> findSafeSolutions(SquareSet constrainedVariables) {
        this.constrainedVariables = constrainedVariables;

        startSearch();

        SquareMap<Integer> mineProbabilities = new SquareMap<>(variables.width, variables.height);

        // Find the squares that are consistently mines or not mines
        for (Square square : constrainedVariables.getSquares()) {
            if (domains.get(square).length == 1) {
                // The known squares don't need to go through solution summary
                int summary = domains.get(square)[0] * 100;
                mineProbabilities.put(square, summary);
                continue;
            }
            long mineSolutions = solutionSummary.get(square);
            if (mineSolutions == 0) {
                // None of the solutions assigned this square as mine
                mineProbabilities.put(square, 0);
                // Record this square as known safe
                reduceDomain(square, 1);
            } else if (mineSolutions == solutionCount) {
                // This square is mine in all solutions
                mineProbabilities.put(square, 100);
                // Record this square as known mine
                reduceDomain(square, 0);
            } else {
                // This is not an exact percentage, just an approximation to guide
                // the bot in case no safe squares are found
                double mineP =  1.0 * mineSolutions / solutionCount;
                int minePercentage = (int) (mineP * 100);
                mineProbabilities.put(square, minePercentage);
            }
        }
        return mineProbabilities;
    }

    public SquareMap<MyList<MinesweeperConstraint>> getConstraints() {
        return constraints;
    }

    public SquareSet getVariables() {
        return variables;
    }

    public void setConstrainedVariables(SquareSet constrainedVariables) {
        this.constrainedVariables = constrainedVariables;
    }

    /**
     * Removes an impossible value from the domain of a square.
     *
     * @param square Square that is now known
     * @param domainToRemove The value that the square can not have
     * @see #updateKnownSquaresConstraints(minesweeper.model.Square)
     */
    public void reduceDomain(Square square, int domainToRemove) {
        if (domains.get(square).length == 1) {
            // Square was already recorded as known
            return;
        }
        if (domainToRemove == 1) {
            // This square can not be a mine
            safeSquares.add(square);
            this.domains.put(square, new int[] { 0 });
        } else {
            // This square is a sure mine
            mineSquares.add(square);
            this.domains.put(square, new int[] { 1 });
        }
        // Update constraints to reflect new knowledge
        this.updateKnownSquaresConstraints(square);
    }

    /**
     * This method returns an unopened square that is known to be safe, or null
     * if none exist.
     *
     * @return A safe Square or null
     */
    public Square getSafeSquare() {
        int numberOfSafes = safeSquares.size();
        if (numberOfSafes == 0) {
            return null;
        }
        Square safe = safeSquares.pop();
        numberOfSafes--;
        while (safe != null && safe.isOpened()) {
            if (numberOfSafes > 0) {
                safe = safeSquares.pop();
                numberOfSafes--;
            } else {
                safe = null;
            }
        }
        return safe;
    }

    /**
     * Method for getting a Square that is known to be a mine and that has not
     * yet been flagged, or null if none exist.
     *
     * @return A Square that is known to be a mine, or null
     */
    public Square getFlaggableSquare() {
        int numberOfMineSquares = mineSquares.size();
        if (numberOfMineSquares == 0) {
            return null;
        }
        Square mineSquare = mineSquares.pop();
        numberOfMineSquares--;
        while (mineSquare != null && mineSquare.isFlagged()) {
            if (numberOfMineSquares > 0) {
                mineSquare = mineSquares.pop();
                numberOfMineSquares--;
            } else {
                mineSquare = null;
            }
        }
        return mineSquare;
    }

    /**
     * A method for removing a known square from the constraint map and from all
     * constraints that it is included in, updating the constraint's mine count
     * accordingly.
     *
     * @param square A square for which constraints should be updated
     * @see MinesweeperConstraint#removeSquare(minesweeper.model.Square, java.lang.Integer)
     */
    public void updateKnownSquaresConstraints(Square square) {
        // Checking that the constraint map contains the square, and that it is
        // indeed known
        if (constraints.containsKey(square) && domains.get(square).length == 1) {
            MyList<MinesweeperConstraint> constraintList = this.constraints.get(square);
            for (int i = 0; i < constraintList.size(); i++) {
                MinesweeperConstraint constraint = constraintList.get(i);
                constraint.removeSquare(square, domains.get(square)[0]);
            }
            constraints.remove(square);
        }
    }

    /**
     * This method goes through all the constraints in the csp and checks if
     * constraints have become trivial for example due to known squares having
     * been removed. It handles trivial constraints by recording all their squares
     * as mines or non-mines, and discards the constraint. Designed to be looped
     * until all constraints are non-trivial.
     *
     * @return True if there were trivial constraints
     * @see MinesweeperConstraint#triviality()
     * @see #reduceDomain(minesweeper.model.Square, int)
     */
    public boolean updateConstraints() {
        MyList<MinesweeperConstraint> constraintList = constraintSet.getList();
        int nonTrivial = 0;
        for (int i = 0; i < constraintList.size(); i++) {
            MinesweeperConstraint constraint = constraintList.get(i);
            if (constraint.triviality() == -1) {
                nonTrivial++;
                continue;
            }
            // The constraint is trivial, so either all squares are mines or none
            constraintSet.remove(constraint);
            SquareSet squareSet = new SquareSet(constraint.getSquares().width, constraint.getSquares().height);
            squareSet.addAll(constraint.getSquares());
            if (constraint.triviality() == 0) {
                for (Square square : squareSet.getSquares()) {
                    reduceDomain(square, 1);
                }
            } else if (constraint.triviality() == 0) {
                for (Square square : squareSet.getSquares()) {
                    reduceDomain(square, 0);
                }
            }
        }
        return nonTrivial != constraintList.size();
    }

    /**
     * Updates the total of solutions found and adds one to the tally of mine
     * solutions for each square that is assigned as mine in this solution.
     * @param solution The solution to be tallied
     */
    private void addSolution(SquareMap<Integer> solution) {
        for (Square square : this.constrainedVariables.getSquares()) {
            Long mineSolutions = solutionSummary.get(square) + solution.get(square);
            solutionSummary.put(square, mineSolutions);
        }
        solutionCount++;
    }

    /**
     * This method is to facilitate testing.
     * @return The amount of solutions found in last backtracking search.
     */
    public Long getSolutionCount() {
        return solutionCount;
    }
}

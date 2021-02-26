package minesweeper.bot;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Square;
import minesweeper.structures.SquareSet;


/**
 * A bot that calculates the best possible moves for playing minesweeper.
 *
 * <p>
 * There are two ways to call MyBot. The methods are given a Board object, that
 * represents the current state of the game. MyBot finds possible solutions for
 * the situation and whether some squares are certainly not mines. The makeMove()
 * method returns a Move object representing the opening of one of such squares,
 * or another square if no safe squares were determined. GetPossibleMoves() method
 * returns a list of Move objects with the Highlight property. Move objects with
 * green highlight are created for squares that are determined not to be mines.
 * Certain mines are given red highlight and undetermined squares a black highlight.
 * Refer to model/Move.java for details.
 * </p>
 */
public class MyBot implements Bot {

    private GameStats gameStats;
    private CSP csp;
    private SquareSet numberSquares;

    /**
     * Make a single decision based on the given Board state
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        SquareSet squaresOfInterest = new SquareSet(board.width, board.height);

        if (board.firstMove) {
            System.out.println("First move detected");
            return getFirstMove(board);
        } else {
            // If it's not the first move, update csp with open squares
            for (Square openSquare : board.getOpenSquares()) {
                csp.reduceDomain(openSquare, 1);
            }
            //  Also update csp with new constraints and constrained squares.
            System.out.println("Number squares before update: " + numberSquares.size());
            for (Square square : getConstrainingSquares(board).getSquares()) {
                SquareSet constrainedBySquare = getConstrainedSquares(board, square);
                squaresOfInterest.addAll(constrainedBySquare);
                if (!numberSquares.contains(square)) {
                    numberSquares.add(square);
                    csp.addConstraint(constrainedBySquare, square.surroundingMines());
                }
            }
            System.out.println("Number squares after update: " + numberSquares.size());
        }
        // Adding constraints may have already found known squares due to all mine or
        // zero mine constraints, so updating constraints
        while (csp.updateConstraints()) {
            System.out.println("Updating...");
        }
        // Checking if constraint simplification has found safe squares
        Square safe = csp.getSafeSquare();
        if (safe != null) {
            Move newMove = new Move(MoveType.OPEN, safe.getX(), safe.getY());
            System.out.println("Making a quick move: " + newMove.locationString());
            return newMove;
        }
        System.out.println("No quick move");
        // To better understand what's happening, here's a step for flagging all known mines
        Square flaggable = csp.getFlaggableSquare();
        if (flaggable != null) {
            Move newMove = new Move(MoveType.FLAG, flaggable.getX(), flaggable.getY());
            System.out.println("Making a flagging move: " + newMove.locationString());
            return newMove;
        }
        System.out.println("No flagging move");
        // Make an opening move based on the list of possible moves csp creates
        // Opening move is created for the first safe square in the solution summary
        HashMap<Square, Integer> solutionSummary = csp.findSafeSolutions(squaresOfInterest);
        int sumOfMineProbability = 0;
        for (Square square : solutionSummary.keySet()) {
            if (solutionSummary.get(square).equals(0)) {
                Move newMove = new Move(MoveType.OPEN, square.getX(), square.getY());
                System.out.println("Making a move: " + newMove.locationString());
                return newMove;
            } else if (solutionSummary.get(square) < 100) {
                sumOfMineProbability += solutionSummary.get(square);
            }
        }
        System.out.println("No safe move found");

        // Get unopened squares that have no constraints
        SquareSet mysterySquares = new SquareSet(board.width, board.height);
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Square square = board.getSquareAt(x, y);
                System.out.println("Checking if square " + square.locationString() + " is a mystery square");
                if (!square.isOpened() && !squaresOfInterest.contains(square)) {
                    mysterySquares.add(square);
                    System.out.println("It was");
                }
            }
        }
        System.out.println("MysterySquares: " + mysterySquares.size());
        // Calculate probability of a mystery square being mine
        // Subtract mines that are not yet flagged, but informed by constraints
        Square leastLikelyMine;
        Integer lowestLikelihood;
        if (!mysterySquares.isEmpty()) {
            int mysteryMines = board.getUnflaggedMines() - sumOfMineProbability / 100;
            System.out.println("MysteryMines = " + mysteryMines);
            int mysteryChance = mysteryMines * 100 / mysterySquares.size();
            System.out.println("MysteryChance = " + mysteryChance);
            lowestLikelihood = mysteryChance;
            leastLikelyMine = mysterySquares.pop();
        } else {
            lowestLikelihood = 100;
            leastLikelyMine = new Square(0, 0);
        }
        // Opening the square that has the least likelihood of being mine
        for (Square square : solutionSummary.keySet()) {
            if (solutionSummary.get(square) <= lowestLikelihood) {
                lowestLikelihood = solutionSummary.get(square);
                leastLikelyMine = square;
            }
        }
        Move riskyMove = new Move(MoveType.OPEN, leastLikelyMine.getX(), leastLikelyMine.getY());
        System.out.println("Making a risky move: " + riskyMove.locationString());
        return riskyMove;
    }

    /**
     * Return multiple highlight moves based on current board state.
     * 
     * Highlight moves for all squares surrounding opened squares are generated.
     * Green highlight indicates a safe square, red highlight a mine square.
     * Squares that could be either are given a black highlight (not visible in
     * current UI).
     * 
     * @param board The current board state.
     * @return List of highlight moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        
        // Creates a new csp and finds all the constraints
        CSP solver = createCsp(board);
        SquareSet constrainedSquares = new SquareSet(board.width, board.height);
        for (Square square : getConstrainingSquares(board).getSquares()) {
            SquareSet constrainedBySquare = getConstrainedSquares(board, square);
            constrainedSquares.addAll(constrainedBySquare);
            solver.addConstraint(constrainedBySquare, square.surroundingMines());
        }
        // How about an update loop?
        while (solver.updateConstraints()) {
            System.out.println("Updating...");
        }
        // Excecute the search for solutions
        HashMap<Square, Integer> solutionSummary = solver.findSafeSolutions(constrainedSquares);
        if (solutionSummary.isEmpty()) {
            return movesToMake;
        }
        
        // Adding moves according to the solution summary
        for (Square square : solutionSummary.keySet()) {
            int moveX = square.getX();
            int moveY = square.getY();
            Move moveToMake;
            if (solutionSummary.get(square).equals(100)) {
                moveToMake = new Move(moveX, moveY, Highlight.RED);
            } else if (solutionSummary.get(square).equals(0)) {
                moveToMake = new Move(moveX, moveY, Highlight.GREEN);
            } else {
                moveToMake = new Move(moveX, moveY, Highlight.BLACK);
            }
            movesToMake.add(moveToMake);
        }
        return movesToMake;
    }

    /**
     * Used to pass the bot the gameStats object, useful for tracking previous moves
     */
    @Override
    public void setGameStats(GameStats gameStats) {
        this.gameStats = gameStats;
    }

    /**
     * Handle the first move on a board.
     *
     * For efficiency, this method creates and saves a CSP object to be used
     * for subsequent calls of the makeMove() method.
     *
     * Since this implementation of minesweeper guarantees a safe zone of 9
     * squares, MyBot starts at a place where there's room for squares around
     * the safe zone, to make the most educated next move.
     *
     * @param board The current board state
     * @return An opening move near the upper left corner of the board
     */
    private Move getFirstMove(Board board) {
        this.csp = createCsp(board);
        System.out.println("Created CSP with all closed squares as variables");
        this.numberSquares = new SquareSet(board.width, board.height);
        Move firstMove = new Move(MoveType.OPEN, 0, 0);

        for (int i = 2; i > 0; i--) {
            if (board.withinBoard(i, i)) {
                firstMove = new Move(MoveType.OPEN, i, i);
                return firstMove;
            }
        }
        System.out.println("Returning first move: " + firstMove.locationString());
        return firstMove;
    }

    /**
     * Create a CSP object based on the Board object given as a parameter.
     * @param board Current state of the board
     * @return A CSP object corresponding to the board
     */
    private CSP createCsp(Board board) {
        // The variables are all the unopened squares of the board
        SquareSet variableList = new SquareSet(board.width, board.height);
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Square square = board.getSquareAt(x, y);
                if (!square.isOpened()) {
                    variableList.add(square);
                }
            }
        }
        System.out.println("Got all closed squares: " + variableList.size());
        // Domains for CSP is a hashmap of values and lists containing 0 and 1.
        HashMap<Square, ArrayList<Integer>> domains = new HashMap<>();
        for (Square variable : variableList.getSquares()) {
            ArrayList<Integer> domainValues = new ArrayList<>();
            domainValues.add(0);
            domainValues.add(1);
            domains.put(variable, domainValues);
        }

        return new CSP(variableList, domains);
    }

    /**
     * Find all the squares on the board that are opened and have an indicator number
     * @param board Current state of the board
     * @return A set of opened squares that have mines around them
     */
    private SquareSet getConstrainingSquares(Board board) {
        SquareSet constrainingSquares = new SquareSet(board.width, board.height);
        for (Square square : board.getOpenSquares()) {
            if (square.surroundingMines() != 0) {
                constrainingSquares.add(square);
            }
        }
        System.out.println("Fetched " + constrainingSquares.size() + " constraining squares");
        return constrainingSquares;
    }

    /**
     * Find all the unopened squares around the given square on the given board
     * @param board The current state of the board
     * @param constrainingSquare A square whose surrounding squares are to be found
     * @return A list of unopened squares around the given square
     */
    private SquareSet getConstrainedSquares(Board board, Square constrainingSquare) {
        SquareSet constrainedSquares = new SquareSet(board.width, board.height);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                int currentX = constrainingSquare.getX() + x;
                int currentY = constrainingSquare.getY() + y;
                if (board.withinBoard(currentX, currentY)) {
                    Square candidate = board.getSquareAt(currentX, currentY);
                    if (!candidate.isOpened()) {
                        constrainedSquares.add(candidate);
                    }
                }
            }
        }
        return constrainedSquares;
    }
}

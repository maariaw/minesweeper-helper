package minesweeper.bot;

import java.util.ArrayList;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Square;
import minesweeper.structures.MyList;
import minesweeper.structures.SquareMap;
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
    public boolean wasGuess;

    /**
     * Make a single decision based on the given Board state.
     *
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        SquareSet squaresOfInterest = new SquareSet(board.width, board.height);
        wasGuess = false;

        if (board.firstMove) {
            return getFirstMove(board);
        } else {
            // If it's not the first move, update csp with open squares
            for (Square openSquare : board.getOpenSquares()) {
                csp.reduceDomain(openSquare, 1);
            }
            //  Also update csp with new constraints and constrained squares.
            for (Square square : getConstrainingSquares(board).getSquares()) {
                SquareSet constrainedBySquare = getConstrainedSquares(board, square);
                squaresOfInterest.addAll(constrainedBySquare);
                if (!numberSquares.contains(square)) {
                    numberSquares.add(square);
                    csp.addConstraint(constrainedBySquare, square.surroundingMines());
                }
            }
        }
        // Adding constraints may have already found known squares due to all mine or
        // zero mine constraints, so updating constraints
        while (csp.updateConstraints()) {
        }
        // Checking if constraint simplification has found safe squares
        Square safe = csp.getSafeSquare();
        if (safe != null) {
            Move newMove = new Move(MoveType.OPEN, safe.getX(), safe.getY());
//            System.out.println("Making a quick move: " + newMove.locationString());
            return newMove;
        }
        // To better understand what's happening, here's a step for flagging all known mines
        Square flaggable = csp.getFlaggableSquare();
        if (flaggable != null) {
            Move newMove = new Move(MoveType.FLAG, flaggable.getX(), flaggable.getY());
//            System.out.println("Making a flagging move: " + newMove.locationString());
            return newMove;
        }
        // Make an opening move based on the list of possible moves csp creates
        // Opening move is created for the first safe square in the solution summary
        SquareMap<Integer> solutionSummary = csp.findSafeSolutions(squaresOfInterest);
        // Tracking the squares that are uncertain, to use for guessing if needed
        int sumOfMineProbability = 0;
        MyList<Square> solutionSquares = solutionSummary.keySet();
        for (int i = 0; i < solutionSquares.size(); i++) {
            Square square = solutionSquares.get(i);
            if (solutionSummary.get(square).equals(0)) {
                Move newMove = new Move(MoveType.OPEN, square.getX(), square.getY());
//                System.out.println("Making a move: " + newMove.locationString());
                return newMove;
            } else if (solutionSummary.get(square) < 100) {
                sumOfMineProbability += solutionSummary.get(square);
            }
        }
        // If we get here, the move will have to rely on guesswork
        // Get unopened squares that have no constraints
        SquareSet mysterySquares = new SquareSet(board.width, board.height);
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Square square = board.getSquareAt(x, y);
                if (!square.isOpened() && !squaresOfInterest.contains(square)) {
                    mysterySquares.add(square);
                }
            }
        }
        // Calculate probability of a mystery square being mine
        // Subtract mines that are not yet flagged, but informed by constraints
        // by approximating with the sumOfMineProbability gathered earlier
        Square leastLikelyMine;
        Integer lowestLikelihood;
        if (!mysterySquares.isEmpty()) {
            int mysteryMines = board.getUnflaggedMines() - sumOfMineProbability / 100;
            int mysteryChance = mysteryMines * 100 / mysterySquares.size();
            lowestLikelihood = mysteryChance;
            leastLikelyMine = mysterySquares.getSquares()[0];
        } else {
            lowestLikelihood = 100;
            leastLikelyMine = new Square(0, 0);
        }
        // Finding the square that has the least likelihood of being mine by
        // comparing the mystery mine probability with the solution-informed
        // probabilities, and choosing lowest
        MyList<Square> solvedSquares = solutionSummary.keySet();
        for (int i = 0; i < solvedSquares.size(); i++) {
            Square square = solvedSquares.get(i);
            if (solutionSummary.get(square) <= lowestLikelihood) {
                lowestLikelihood = solutionSummary.get(square);
                leastLikelyMine = square;
            }
        }
        wasGuess = true;
        Move riskyMove = new Move(MoveType.OPEN, leastLikelyMine.getX(), leastLikelyMine.getY());
//        System.out.println("Making a risky move: " + riskyMove.locationString());
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
        // This method has to use ArrayList, because it instructs the UI to draw
        // highlights on the board. I do not want to tamper with that.
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
        }
        // Excecute the search for solutions
        SquareMap<Integer> solutionSummary = solver.findSafeSolutions(constrainedSquares);
        if (solutionSummary.size() == 0) {
            return movesToMake;
        }
        
        // Adding highlight moves according to the solution summary
        MyList<Square> solutionSquares = solutionSummary.keySet();
        for (int i = 0; i < solutionSquares.size(); i++) {
            Square square = solutionSquares.get(i);
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
     * Used to pass the bot the gameStats object, useful for tracking previous moves.
     * Not used... yet.
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
     * A move closer to the corner or to the centre might be even better, but
     * that is not explored in this project.
     *
     * @param board The current board state
     * @return An opening move near the upper left corner of the board
     */
    private Move getFirstMove(Board board) {
        this.csp = createCsp(board);
        this.numberSquares = new SquareSet(board.width, board.height);
        Move firstMove = new Move(MoveType.OPEN, 0, 0);
        // Safeguarding for malicious gamers who create extremely tiny boards
        for (int i = 2; i > 0; i--) {
            if (board.withinBoard(i, i)) {
                firstMove = new Move(MoveType.OPEN, i, i);
                return firstMove;
            }
        }
        return firstMove;
    }

    /**
     * Create a CSP object based on the Board object given as a parameter.
     *
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
        // Domains for CSP is a map of Squares and arrays containing 0 and 1.
        SquareMap<int[]> domains = new SquareMap<>(board.width, board.height);
        for (Square variable : variableList.getSquares()) {
            domains.put(variable, new int[] {0, 1});
        }

        return new CSP(variableList, domains);
    }

    /**
     * Find all the squares on the board that are opened and have an indicator number.
     *
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
        return constrainingSquares;
    }

    /**
     * Find all the unopened squares around the given square on the given board.
     *
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

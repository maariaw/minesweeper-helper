package minesweeper.bot;

import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import minesweeper.model.Board;
import minesweeper.model.GameStats;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import minesweeper.model.Highlight;
import minesweeper.model.Pair;
import minesweeper.model.Square;


/**
 * A bot that calculates the best possible moves for playing minesweeper.
 *
 * <p>
 * The Bot will be called externally (the interface for this is the makeMove()
 * function) and be given the current game state represented by a Board object.
 * The bot determines the best action to take by returning a Move object, which represents
 * one action that can be executed onto the board. Refer to model/Move.java for
 * details.
 * </p>
 */
public class MyBot implements Bot {

    private Random rng = new Random();
    private GameStats gameStats;
    private CSP csp;

    /**
     * Make a single decision based on the given Board state [[DUMMY]]
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        // Since this implementation of minesweeper quarantees a safe zone of 9
        // squares, my intuition is to start at a place where there's room for
        // squares around the safe zone, to make most educated next move
        if (board.firstMove) {
            for (int i = 2; i >= 0; i--) {
                if (board.withinBoard(i, i)) {
                    return new Move(MoveType.OPEN, i, i);
                }
            }
        }

        // Make an opening move based on the list of possible moves csp creates
        ArrayList<Move> movesToMake = getPossibleMoves(board);
        for (Move move : movesToMake) {
            if (move.highlight.equals(Highlight.GREEN)) {
                return new Move(MoveType.OPEN, move.x, move.y);
            }
        }

        // Failing to find a safe move, for now the bot will just open an unsafe one
        // In the future, the guess should be informed by probabilities
        for (Move move : movesToMake) {
            if (move.highlight.equals(Highlight.BLACK)) {
                return new Move(MoveType.OPEN, move.x, move.y);
            }
        }

        // Any valid square
        Pair<Integer> where = new Pair(0, 0);
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Square here = board.getSquareAt(x, y);
                if (!board.getOpenSquares().contains(here)) {
                    where = new Pair(x, y);
                }
            }
        }
        return new Move(MoveType.OPEN, where.first, where.second);
    }

    /**
     * Return multiple possible moves to make based on current board state.
     * Used by a helper bot to provide multiple highlights at once.
     * @param board The current board state.
     * @return List of moves for current board.
     */
    @Override
    public ArrayList<Move> getPossibleMoves(Board board) {
        ArrayList<Move> movesToMake = new ArrayList<>();
        
        HashSet<Square> indicators = new HashSet<>();
        
        // Gets the open squares of the board that have mines around them
        for (Square square : board.getOpenSquares()) {
            if (square.surroundingMines() != 0) {
                indicators.add(square);
            }
        }
        
        // The CSP variables are all unopened squares next to indicator squares.
        // Collect the unopened squares by their indicator to get the constraint groups
        HashSet<Square> variables = new HashSet<>();
        ArrayList<MinesweeperConstraint> constraintList = new ArrayList<>();
        for (Square indicator : indicators) {
            int number = indicator.surroundingMines();
            ArrayList<Square> constraintVars = new ArrayList<>();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    int currentX = indicator.getX() + x;
                    int currentY = indicator.getY() + y;
                    if (board.withinBoard(currentX, currentY)) {
                        Square candidate = board.getSquareAt(currentX, currentY);
                        if (!candidate.isOpened()) {
                            variables.add(candidate);
                            constraintVars.add(candidate);
                        }
                    }
                }
            }
            MinesweeperConstraint newConstraint = new MinesweeperConstraint(number, constraintVars);
            constraintList.add(newConstraint);
        }
        ArrayList<Square> variableList = new ArrayList<>(variables);
        
        // Domains for CSP is a hashmap of values and lists containing 0 and 1.
        HashMap<Square, ArrayList<Integer>> domains = new HashMap<>();
        for (Square variable : variables) {
            ArrayList<Integer> domainValues = new ArrayList<>();
            domainValues.add(0);
            domainValues.add(1);
            domains.put(variable, domainValues);
        }
        
        // Now we can construct CSP
        CSP solver = new CSP(variableList, domains);
        // And add the constraints
        for (MinesweeperConstraint constraint : constraintList) {
            solver.addConstraint(constraint);
        }
        
        // Excecute the search for solutions
        HashMap<Square, Integer> template = new HashMap<>();
        ArrayList<HashMap> solutions = solver.startSearch(template);
        if (solutions.isEmpty()) {
            return movesToMake;
        }
        
        // I need to find the assigments that are shared with all the solutions
        for (Square square : variableList) {
            int squareSolutions = 0;
            int mineSolutions = 0;
            for (HashMap solution : solutions) {
                squareSolutions++;
                if ((Integer) solution.get(square) == 1) {
                    mineSolutions++;
                }
            }
            // If all solutions say mine, add a red highlight move to the
            // suggested moves list, if all say no mine, make it green
            int moveX = square.getX();
            int moveY = square.getY();
            Move moveToMake;
            if (mineSolutions == squareSolutions) {
                moveToMake = new Move(moveX, moveY, Highlight.RED);
            } else if (mineSolutions == 0) {
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

    public CSP createCsp(Board board) {
        // The variables are all the unopened squares of the board
        ArrayList<Square> variableList = new ArrayList<>();
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Square square = board.getSquareAt(x, y);
                if (!board.getOpenSquares().contains(square)) {
                    variableList.add(square);
                }
            }
        }
        // Domains for CSP is a hashmap of values and lists containing 0 and 1.
        HashMap<Square, ArrayList<Integer>> domains = new HashMap<>();
        for (Square variable : variableList) {
            ArrayList<Integer> domainValues = new ArrayList<>();
            domainValues.add(0);
            domainValues.add(1);
            domains.put(variable, domainValues);
        }

        return new CSP(variableList, domains);
    }

    public HashSet<Square> getConstrainingSquares(Board board) {
        HashSet<Square> constrainingSquares = new HashSet<>();
        for (Square square : board.getOpenSquares()) {
            if (square.surroundingMines() != 0) {
                constrainingSquares.add(square);
            }
        }
        return constrainingSquares;
    }
}

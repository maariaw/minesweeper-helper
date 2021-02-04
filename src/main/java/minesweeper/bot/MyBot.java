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

    /**
     * Make a single decision based on the given Board state [[DUMMY]]
     * @param board The current board state
     * @return Move to be made onto the board
     */
    @Override
    public Move makeMove(Board board) {
        // Find the coordinate of an unopened square
        Pair<Integer> pair = findUnopenedSquare(board);
        int x = (int) pair.first;
        int y = (int) pair.second;

        // The TestBot isn't very smart and randomly
        // decides what move should be made using java.util.Random
        Integer type = rng.nextInt(10);

        // Certain move types are given more weight
        // but these moves are still extremely random
        // and most likely not correct
        if (type < 5) {
            return new Move(MoveType.OPEN, x, y);
        } else if (type < 8) {
            return new Move(MoveType.FLAG, x, y);
        } else {
            if (rng.nextInt(2) == 0) {
                return new Move(x, y, Highlight.GREEN);
            } else {
                return new Move(x, y, Highlight.RED);
            }
        }
    }
    /**
     * Return multiple possible moves to make based on current board state.
     * Suggested to be used for a "helper" bot to provide multiple highlights at once.
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
        
        // Excecute the search for solution
        HashMap<Square, Integer> template = new HashMap<>();
        HashMap<Square, Integer> solution = solver.backtrackingSearch(template);
        
        for (Square square : solution.keySet()) {
            System.out.println("Ruutu (" + square.getX() + ", " + square.getY() + "): " + solution.get(square));
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
     * Find the (X, Y) coordinate pair of an unopened square
     * from the current board
     * @param board The current board state
     * @return An (X, Y) coordinate pair
     */
    public Pair<Integer> findUnopenedSquare(Board board) {
        Boolean unOpenedSquare = false;

        // board.getOpenSquares allows access to already opened squares
        HashSet<Square> opened = board.getOpenSquares();
        int x;
        int y;

        Pair<Integer> pair = new Pair<>(0, 0);

        // Randomly generate X,Y coordinate pairs that are not opened
        while (!unOpenedSquare) {
            x = rng.nextInt(board.width);
            y = rng.nextInt(board.height);
            if (!opened.contains(board.board[x][y])) {
                unOpenedSquare = true;
                pair = new Pair<Integer>(x, y);
            }
        }

        // This pair should point to an unopened square now
        return pair;
    } 
}

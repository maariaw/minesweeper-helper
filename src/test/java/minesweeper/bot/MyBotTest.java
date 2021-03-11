
package minesweeper.bot;

import java.util.ArrayList;
import java.util.Random;
import minesweeper.TestApp;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    private Random rng;

    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 10, 10);
        this.rng = new Random();
    }

    @Test
    public void makeMoveMakesFirstMoveOnThirdDiagonalSquareFromTopLeftIfRoom() {
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x == 2 && move.y == 2);
    }

    @Test
    public void makeMoveMakesFirstMoveOnSecondDiagonalSquareFromTopLeftIfTinyBoard() {
        Board tinyBoard = new Board(generator, 2, 2, 1);
        Move move = this.bot.makeMove(tinyBoard);
        assertTrue(move.x == 1 && move.y == 1);
    }

    @Test
    public void makeMoveMakesFirstMoveOnTopLeftIfOneTileBoard() {
        Board tinyBoard = new Board(generator, 1, 1, 0);
        Move move = this.bot.makeMove(tinyBoard);
        assertTrue(move.x == 0 && move.y == 0);
    }

    @Test
    public void makeMoveMakesLaterMovesWithinBoard() {
        this.board.makeMove(this.bot.makeMove(this.board));
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x >= 0 && move.x < 10);
        assertTrue(move.y >= 0 && move.y < 10);
    }

    @Test
    public void getPossibleMovesReturnsEmptyListIfNoMovesMade() {
        ArrayList<Move> moves = this.bot.getPossibleMoves(board);
        assertTrue(moves.isEmpty());
    }

    @Test
    public void getPossibleMovesGeneratesMovesWithinBoard() {
        this.board.makeMove(new Move(MoveType.OPEN, 2, 2));
        ArrayList<Move> moves = this.bot.getPossibleMoves(board);
        for (Move move : moves) {
            assertTrue(move.x >= 0 && move.x < 10);
            assertTrue(move.y >= 0 && move.y < 10);
        }
    }

    @Test
    public void getPossibleMovesGeneratesHighlightMoves() {
        this.board.makeMove(new Move(MoveType.OPEN, 2, 2));
        ArrayList<Move> moves = this.bot.getPossibleMoves(board);
        for (Move move : moves) {
            assertTrue(move.type == MoveType.HIGHLIGHT);
        }
    }

    @Test
    public void allMediumMapLowMineDensityLossesAreGuesses() {
        for (int game = 0; game < 500; game++) {
            long seed = Math.abs(rng.nextLong());
            TestApp app = new TestApp(seed, 18, 18, 40);
            if (app.board.gameLost) {
                MyBot lostBot = (MyBot) app.bot;
                assertTrue(lostBot.wasGuess);
            }
        }
    }

    @Test
    public void allMediumMapHighMineDensityLossesAreGuesses() {
        for (int game = 0; game < 100; game++) {
            long seed = Math.abs(rng.nextLong());
            TestApp app = new TestApp(seed, 18, 18, 80);
            if (app.board.gameLost) {
                MyBot lostBot = (MyBot) app.bot;
                assertTrue(lostBot.wasGuess);
            }
        }
    }

    @Test
    public void allBigMapLowMineDensityLossesAreGuesses() {
        for (int game = 0; game < 100; game++) {
            long seed = Math.abs(rng.nextLong());
            TestApp app = new TestApp(seed, 30, 30, 100);
            if (app.board.gameLost) {
                MyBot lostBot = (MyBot) app.bot;
                assertTrue(lostBot.wasGuess);
            }
        }
    }

//    // Even these 10 games take a bit too long to run every time
//    @Test
//    public void allBigMapHighMineDensityLossesAreGuesses() {
//        for (int game = 0; game < 10; game++) {
//            long seed = Math.abs(rng.nextLong());
//            TestApp app = new TestApp(seed, 30, 30, 200);
//            if (app.board.gameLost) {
//                MyBot lostBot = (MyBot) app.bot;
//                assertTrue(lostBot.wasGuess);
//            }
//        }
//    }
}

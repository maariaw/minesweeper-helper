
package minesweeper.bot;

import java.util.ArrayList;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Highlight;
import minesweeper.model.Move;
import minesweeper.model.MoveType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;

    @Before
    public void setUp() {
        this.bot = BotSelect.getBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 10, 10);
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
        this.bot.makeMove(this.board);
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x >= 0 && move.x < 10);
        assertTrue(move.y >= 0 && move.y < 10);
    }

    @Test
    public void getPossibleMovesReturnsEmptyListIfNoMovesMade() {
        ArrayList<Move> moves = this.bot.getPossibleMoves(board);
        assertTrue(!moves.isEmpty());
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
}

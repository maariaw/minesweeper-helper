
package minesweeper.bot;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import minesweeper.bot.Bot;
import minesweeper.bot.BotSelect;
import minesweeper.bot.TestBot;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Move;
import minesweeper.model.Highlight;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestBotTest {
    private Bot bot;
    private MinefieldGenerator generator;
    private Board board;
    @Before
    public void setUp() {
        this.bot = new TestBot();
        this.generator = new MinefieldGenerator();
        this.board = new Board(generator, 10, 10, 3);
    }
    
    @After
    public void tearDown() {
    // empty method
    }

    @Test
    public void testBotCanMakeValidMoves() {
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x >= 0 && move.x < 10);
        assertTrue(move.y >= 0 && move.y < 10);
    }

    @Test
    public void testBotCanProvideListOfValidMoves() {
        ArrayList<Move> moves = this.bot.getPossibleMoves(this.board);
        for (Move m : moves) {
            assertTrue(m.x >= 0 && m.x < 10);
            assertTrue(m.y >= 0 && m.y < 10);
            assertTrue(m.highlight != Highlight.NONE);
        }
    }
}
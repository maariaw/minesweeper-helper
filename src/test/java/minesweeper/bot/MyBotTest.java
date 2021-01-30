
package minesweeper.bot;

import java.util.ArrayList;
import minesweeper.generator.MinefieldGenerator;
import minesweeper.model.Board;
import minesweeper.model.Highlight;
import minesweeper.model.Move;
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
        this.board = new Board(generator, 10, 10, 3);
    }
    
    @Test
    public void myBotCanMakeValidMoves() {
        Move move = this.bot.makeMove(this.board);
        assertTrue(move.x >= 0 && move.x < 10);
        assertTrue(move.y >= 0 && move.y < 10);
    }

//    @Test
//    public void testBotCanProvideListOfValidMoves() {
//        ArrayList<Move> moves = this.bot.getPossibleMoves(this.board);
//        for (Move m : moves) {
//            assertTrue(m.x >= 0 && m.x < 10);
//            assertTrue(m.y >= 0 && m.y < 10);
//            assertTrue(m.highlight != Highlight.NONE);
//        }
//    }
    
    
    
}

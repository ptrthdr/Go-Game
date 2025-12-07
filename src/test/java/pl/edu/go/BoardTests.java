package pl.edu.go;

import org.junit.jupiter.api.Test;
import pl.edu.go.board.Board;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTests {

    @Test
    public void testPlaceStone() {
        Board b = new Board(5);
        assertTrue(b.playMove(Board.BLACK, 0, 0));
        assertFalse(b.playMove(Board.WHITE, 0, 0)); // zajęte
    }

    @Test
    public void testSuicide() {
        Board b = new Board(3);
        b.playMove(Board.BLACK, 0, 1);
        b.playMove(Board.BLACK, 1, 0);
        b.playMove(Board.BLACK, 1, 2);
        b.playMove(Board.BLACK, 2, 1);

        assertFalse(b.playMove(Board.WHITE, 1, 1));
    }
}

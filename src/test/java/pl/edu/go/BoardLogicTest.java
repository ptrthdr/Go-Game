package pl.edu.go;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import pl.edu.go.board.Board;

public class BoardLogicTest {

    @Test
    public void testPlaceStoneOnEmptyField() {
        Board b = new Board(5);
        assertTrue(b.playMove(Board.BLACK, 2, 2),
                "Powinno się udać postawić kamień na pustym polu");
    }

    @Test
    public void testCannotPlaceOnOccupiedField() {
        Board b = new Board(5);
        b.playMove(Board.BLACK, 2, 2);

        assertFalse(b.playMove(Board.WHITE, 2, 2),
                "Nie wolno postawić kamienia na zajętym polu");
    }

    @Test
    public void testCannotPlaceOutsideBoard() {
        Board b = new Board(5);

        assertFalse(b.playMove(Board.BLACK, -1, 0));
        assertFalse(b.playMove(Board.BLACK, 0, -1));
        assertFalse(b.playMove(Board.BLACK, 5, 0));
        assertFalse(b.playMove(Board.BLACK, 0, 5));
    }

    @Test
    public void testSingleStoneLibertiesCenter() throws Exception {
        Board b = new Board(5);
        b.playMove(Board.BLACK, 2, 2);

        var getGroup = b.getClass().getDeclaredMethod("getGroup", int.class, int.class);
        getGroup.setAccessible(true);

        Object group = getGroup.invoke(b, 2, 2);
        var countLiberties = b.getClass().getDeclaredMethod(
                "countLiberties",
                group.getClass());
        countLiberties.setAccessible(true);

        int liberties = (int) countLiberties.invoke(b, group);

        assertEquals(4, liberties,
                "Kamień w środku planszy powinien mieć 4 oddechy");
    }

    @Test
    public void testConnectedStonesFormGroup() throws Exception {
        Board b = new Board(5);

        b.playMove(Board.BLACK, 1, 1);
        b.playMove(Board.BLACK, 2, 1);

        var gmethod = b.getClass().getDeclaredMethod("getGroup", int.class, int.class);
        gmethod.setAccessible(true);

        Object group = gmethod.invoke(b, 1, 1);

        var stonesMethod = group.getClass().getMethod("getStones");
        int size = ((java.util.Set<?>) stonesMethod.invoke(group)).size();

        assertEquals(2, size,
                "Dwa sąsiadujące kamienie powinny tworzyć jedną grupę");
    }

    @Test
    public void testCaptureSingleStone() {
        Board b = new Board(3);

        // Otoczenie kamienia
        b.playMove(Board.BLACK, 1, 0);
        b.playMove(Board.BLACK, 0, 1);
        b.playMove(Board.BLACK, 2, 1);
        b.playMove(Board.BLACK, 1, 2);

        b.playMove(Board.WHITE, 1, 1);

        b.playMove(Board.BLACK, 1, 1);

        int[][] state = b.getState();

        assertEquals(Board.BLACK, state[1][1],
                "Biały kamień powinien zostać zbity i zastąpiony czarnym");
    }

    @Test
    public void testCaptureGroup() {
        Board b = new Board(5);

        b.playMove(Board.WHITE, 2, 1);
        b.playMove(Board.WHITE, 2, 2);

        b.playMove(Board.BLACK, 1, 1);
        b.playMove(Board.BLACK, 3, 1);
        b.playMove(Board.BLACK, 1, 2);
        b.playMove(Board.BLACK, 3, 2);
        b.playMove(Board.BLACK, 2, 3);

        b.playMove(Board.BLACK, 2, 0);

        int[][] state = b.getState();

        assertEquals(Board.EMPTY, state[2][1],
                "Pierwszy kamień białej grupy powinien zostać zbity");
        assertEquals(Board.EMPTY, state[2][2],
                "Drugi kamień białej grupy powinien zostać zbity");
    }

    @Test
    public void testSuicideForbiddenUnlessCapturing() {
        Board b = new Board(3);

        b.playMove(Board.BLACK, 1, 0);
        b.playMove(Board.BLACK, 0, 1);
        b.playMove(Board.BLACK, 2, 1);
        b.playMove(Board.BLACK, 1, 2);

        assertFalse(b.playMove(Board.WHITE, 1, 1),
                "Ruch samobójczy powinien być niedozwolony");
    }

}

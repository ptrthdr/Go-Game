package pl.edu.go;

import pl.edu.go.board.Board;
import pl.edu.go.move.MoveAdapter;

public class MainTest {

    public static void main(String[] args) {

        Board board = new Board(5);

        System.out.println("Pusta plansza:");
        System.out.println(board);

        int[] pos = MoveAdapter.toInternal("B2");
        board.playMove(Board.BLACK, pos[0], pos[1]);

        System.out.println("Po ruchu czarnych B2:");
        System.out.println(board);
    }
}

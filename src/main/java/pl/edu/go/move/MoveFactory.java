package pl.edu.go.move;

public class MoveFactory {
    public static Move createMove(int color, int x, int y) {
        return new Move(color, x, y);
    }
}

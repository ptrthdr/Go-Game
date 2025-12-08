package pl.edu.go.move;

public class Move {
    private final int color;
    private final int x;
    private final int y;

    public Move(int color, int x, int y) {
        this.color = color;
        this.x = x;

        this.y = y;

    }

    public int getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

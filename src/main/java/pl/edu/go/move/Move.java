package pl.edu.go.move;

/**
 * Klasa Move — pojedynczy ruch w grze Go (kolor + współrzędne).
 *
 * Rola klasy:
 * - przechowuje kolor gracza oraz współrzędne (x, y),
 * - może być używana jako wygodna reprezentacja ruchu między warstwami
 *   (np. między adapterem wejścia a logiką Board / Game).
 *
 * Uwaga:
 * - W obecnej wersji Game korzysta bezpośrednio z Board.playMove(color, x, y),
 *   ale obiekt Move może być użyteczny do dalszego rozszerzania projektu
 *   (np. historia ruchów, cofanie ruchu).
 */


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

package pl.edu.go.move;

/**
 * Wzorzec: Factory Method
 * ---------------------------------
 * Izoluje tworzenie obiektu Move.
 * Dzięki temu możemy w przyszłości rozbudować tworzenie ruchów
 * (np. walidacje, parsowanie, logowanie).
 */
public class MoveFactory {
    public static Move createMove(int color, int x, int y) {
        return new Move(color, x, y);
    }
}

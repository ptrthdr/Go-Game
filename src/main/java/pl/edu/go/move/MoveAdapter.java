package pl.edu.go.move;

/**
 * Klasa MoveAdapter — adapter pomiędzy notacją użytkownika a współrzędnymi planszy.
 *
 * Wzorzec projektowy:
 * - Adapter:
 *   - Tłumaczy zewnętrzną reprezentację ruchu (np. tekstową notację "D4")
 *     na współrzędne (x, y) i ewentualnie obiekt Move.
 *   - Pozwala utrzymać klasę Board niezależną od formatu danych wejściowych.
 *
 * Rola klasy:
 * - zamiana danych z UI/klienta na formę zrozumiałą dla logiki gry.
 */


public class MoveAdapter {

    public static int[] toInternal(String move) {
        char column = Character.toUpperCase(move.charAt(0));
        int x = column - 'A';
        int y = Integer.parseInt(move.substring(1)) - 1;
        return new int[] { x, y };
    }

    public static String toExternal(int x, int y) {
        char col = (char) ('A' + x);
        return "" + col + (y + 1);
    }
}

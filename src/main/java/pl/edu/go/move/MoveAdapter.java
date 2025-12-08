package pl.edu.go.move;

/**
 * Wzorzec: Adapter
 * ---------------------------------
 * Adaptuje notację tekstową Go (np. "D4") do współrzędnych tablicy (x,y).
 * GUI lub klient sieciowy może przesyłać ruchy jako stringi,
 * a logika gry działa na indeksach tablicy.
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

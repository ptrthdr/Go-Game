package pl.edu.go.move;

public class MoveAdapter {

    // "D4" → (3, 3)
    public static int[] toInternal(String move) {
        char column = Character.toUpperCase(move.charAt(0));
        int x = column - 'A';
        int y = Integer.parseInt(move.substring(1)) - 1;
        return new int[]{x, y};
    }

    // (3, 3) → "D4"
    public static String toExternal(int x, int y) {
        char col = (char) ('A' + x);
        return "" + col + (y + 1);
    }
}

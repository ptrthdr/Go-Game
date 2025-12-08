package pl.edu.go.game;

import pl.edu.go.board.Board;

public enum PlayerColor {
    BLACK(Board.BLACK),
    WHITE(Board.WHITE);

    private final int boardColor;

    PlayerColor(int boardColor) {
        this.boardColor = boardColor;
    }

    /** Kolor w postaci int-a używanego przez Board (Board.BLACK / Board.WHITE). */
    public int toBoardColor() {
        return boardColor;
    }

    public PlayerColor opposite() {
        return this == BLACK ? WHITE : BLACK;
    }

    public static PlayerColor fromBoardColor(int color) {
        return switch (color) {
            case Board.BLACK -> BLACK;
            case Board.WHITE -> WHITE;
            default -> throw new IllegalArgumentException("Not a stone color: " + color);
        };
    }
}

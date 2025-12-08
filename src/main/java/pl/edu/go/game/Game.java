package pl.edu.go.game;

import pl.edu.go.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Board board;
    private PlayerColor currentPlayer = PlayerColor.BLACK;
    private boolean finished = false;
    private GameResult result;
    private int consecutivePasses = 0; // można wykorzystać później

    private final List<GameObserver> observers = new ArrayList<>();

    public Game(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public PlayerColor getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isFinished() {
        return finished;
    }

    public GameResult getResult() {
        return result;
    }

    // ------- OBSERVER API -------

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyBoardChanged() {
        for (GameObserver o : observers) {
            o.onBoardChanged(board);
        }
    }

    private void notifyPlayerToMoveChanged() {
        for (GameObserver o : observers) {
            o.onPlayerToMoveChanged(currentPlayer);
        }
    }

    private void notifyGameEnded() {
        for (GameObserver o : observers) {
            o.onGameEnded(result);
        }
    }

    // ------- LOGIKA WYSOKIEGO POZIOMU -------

    /**
     * Próbuje zagrać ruch. Zwraca true, jeśli ruch był legalny.
     */
    public boolean playMove(PlayerColor player, int x, int y) {
        if (finished) {
            return false;
        }
        // opcjonalne pilnowanie kolejności ruchu:
        if (player != currentPlayer) {
            return false;
        }

        // TU korzystamy z Twojego API Board:
        boolean ok = board.playMove(player.toBoardColor(), x, y);
        if (!ok) {
            return false;
        }

        // ruch legalny -> resetujemy pass-y i zmiana gracza
        consecutivePasses = 0;
        currentPlayer = currentPlayer.opposite();

        notifyBoardChanged();
        notifyPlayerToMoveChanged();
        return true;
    }

    /**
     * Pass danego gracza.
     */
    public void pass(PlayerColor player) {
        if (finished) {
            return;
        }
        if (player != currentPlayer) {
            return;
        }

        consecutivePasses++;
        if (consecutivePasses >= 2) {
            finished = true;
            // na Iterację 1 nie ma jeszcze liczenia punktów, więc winner może być null
            result = new GameResult(null, "two passes");
            notifyGameEnded();
            return;
        }

        currentPlayer = currentPlayer.opposite();
        notifyPlayerToMoveChanged();
    }

    /**
     * Rezygnacja.
     */
    public void resign(PlayerColor player) {
        if (finished) {
            return;
        }
        finished = true;
        result = new GameResult(player.opposite(), "resign");
        notifyGameEnded();
    }
}

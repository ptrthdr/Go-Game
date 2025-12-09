package pl.edu.go.game;

import pl.edu.go.board.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa Game — logika gry na wyższym poziomie niż sama plansza.
 *
 * Wzorce projektowe:
 * - Observer:
 *   - Game pełni rolę "subject" (obserwowanego obiektu).
 *   - Przechowuje listę GameObserver i powiadamia ich o:
 *     * zmianie planszy (onBoardChanged),
 *     * zmianie gracza (onPlayerToMoveChanged),
 *     * zakończeniu gry (onGameEnded).
 *
 * Rola klasy:
 * - przechowuje referencję do Board (silnika logiki Go),
 * - pilnuje kolejności ruchów (BLACK/WHITE),
 * - obsługuje wysokopoziomowe akcje:
 *   * playMove(...) — wykonanie ruchu na planszy,
 *   * pass(...) — pas, z licznikiem kolejnych passów,
 *   * resign(...) — rezygnacja, ustawienie zwycięzcy i zakończenie gry,
 * - powiadamia obserwatorów o każdej zmianie stanu gry.
 *
 * Klasa nie zna szczegółów komunikacji sieciowej ani UI — od tego są inne warstwy.
 */
public class Game {

    private final Board board;

    // który gracz ma aktualnie ruch (zaczyna BLACK)
    private PlayerColor currentPlayer = PlayerColor.BLACK;

    // czy gra została zakończona
    private boolean finished = false;

    // wynik gry (ustawiany przy resign lub dwóch pasach)
    private GameResult result;

    // licznik kolejnych passów (np. dwa pasy = koniec gry)
    private int consecutivePasses = 0;

    // lista zarejestrowanych obserwatorów (np. GameSession)
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
     * Próbuje wykonać ruch gracza player na polu (x, y).
     *
     * Zwraca:
     * - true, jeśli ruch był legalny i został wykonany,
     * - false, jeśli ruch był nielegalny (np. zły gracz, samobójstwo).
     */
    public boolean playMove(PlayerColor player, int x, int y) {
        if (finished) {
            return false;
        }
        // można tu dodatkowo pilnować kolejności ruchów
        if (player != currentPlayer) {
            return false;
        }

        // delegacja do Board — niskopoziomowa logika ruchu
        boolean ok = board.playMove(player.toBoardColor(), x, y);
        if (!ok) {
            return false;
        }

        // ruch legalny -> resetujemy liczbę passów
        consecutivePasses = 0;

        // zmiana gracza
        currentPlayer = currentPlayer.opposite();

        // powiadamiamy obserwatorów (np. GameSession) o zmianie planszy i gracza
        notifyBoardChanged();
        notifyPlayerToMoveChanged();
        return true;
    }

    /**
     * Gracz player wykonuje PASS.
     * Dwa kolejne passy mogą oznaczać koniec gry (w tej wersji wynik jest uproszczony).
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
            // w tej iteracji nie liczymy punktów — zwycięzca może być null
            result = new GameResult(null, "two passes");
            notifyGameEnded();
            return;
        }

        // zmiana gracza i powiadomienie obserwatorów
        currentPlayer = currentPlayer.opposite();
        notifyPlayerToMoveChanged();
    }

    /**
     * Gracz player poddaje grę (RESIGN).
     * Zwycięzcą zostaje przeciwnik.
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

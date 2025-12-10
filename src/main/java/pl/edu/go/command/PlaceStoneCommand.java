package pl.edu.go.command;

/**
 * PlaceStoneCommand — komenda reprezentująca ruch z postawieniem kamienia.
 *
 * Wzorzec projektowy:
 * - Command:
 *   - Implementuje GameCommand, enkapsuluje dane ruchu (x, y, kolor gracza)
 *     oraz logikę jego wykonania.
 *
 * Rola klasy:
 * - w metodzie execute(Game game) próbuje wykonać ruch:
 *   * wywołuje game.playMove(player, x, y),
 *   * w razie nielegalnego ruchu zgłasza wyjątek z opisem.
 *
 * Użycie:
 * - tworzona na serwerze w TextCommandFactory na podstawie komunikatu
 *   tekstowego "MOVE x y".
 */

import pl.edu.go.game.Game;
import pl.edu.go.game.PlayerColor;

public class PlaceStoneCommand implements GameCommand {

    private final int x;
    private final int y;
    private final PlayerColor player;

    public PlaceStoneCommand(int x, int y, PlayerColor player) {
        this.x = x;
        this.y = y;
        this.player = player;
    }

    @Override
    public void execute(Game game) {
        // Sprawdzenie tury: jeśli to nie ten gracz, zgłaszamy błąd
        if (game.getCurrentPlayer() != player) {
            throw new IllegalStateException("Not your turn: " + player.name());
        }

        boolean ok = game.playMove(player, x, y);
        if (!ok) {
            // Tu mamy inne przypadki: samobójstwo, zajęte pole itd.
            throw new IllegalStateException("Illegal move at (" + x + ", " + y + ")");
        }
    }
}

package pl.edu.go.command;

/**
 * PassCommand — komenda reprezentująca pas gracza.
 *
 * Wzorzec projektowy:
 * - Command:
 *   - Implementuje GameCommand, enkapsuluje akcję "gracz pasuje".
 *
 * Rola klasy:
 * - przechowuje informację, który gracz pasuje (PlayerColor),
 * - w metodzie execute(Game game) wywołuje game.pass(player),
 *   co może zmienić aktualnego gracza lub zakończyć grę po dwóch pasach.
 *
 * Użycie:
 * - tworzona w TextCommandFactory na podstawie komunikatu "PASS"
 *   otrzymanego od klienta.
 */

import pl.edu.go.game.Game;
import pl.edu.go.game.PlayerColor;

public class PassCommand implements GameCommand {

    private final PlayerColor player;

    public PassCommand(PlayerColor player) {
        this.player = player;
    }

    @Override
    public void execute(Game game) {
        if (game.getCurrentPlayer() != player) {
            throw new IllegalStateException("Not your turn: " + player.name());
        }
        game.pass(player);
    }
}

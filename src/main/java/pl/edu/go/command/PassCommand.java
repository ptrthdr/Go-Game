package pl.edu.go.command;

import pl.edu.go.game.Game;
import pl.edu.go.game.PlayerColor;

public class PassCommand implements GameCommand {

    private final PlayerColor player;

    public PassCommand(PlayerColor player) {
        this.player = player;
    }

    @Override
    public void execute(Game game) {
        game.pass(player);
    }
}

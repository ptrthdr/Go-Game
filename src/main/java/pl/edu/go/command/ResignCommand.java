package pl.edu.go.command;

import pl.edu.go.game.Game;
import pl.edu.go.game.PlayerColor;

public class ResignCommand implements GameCommand {

    private final PlayerColor player;

    public ResignCommand(PlayerColor player) {
        this.player = player;
    }

    @Override
    public void execute(Game game) {
        game.resign(player);
    }
}

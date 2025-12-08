package pl.edu.go.command;

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
    public void execute(Game game) throws Exception {
        boolean ok = game.playMove(player, x, y);
        if (!ok) {
            throw new Exception("Illegal move at (" + x + ", " + y + ")");
        }
    }
}

package pl.edu.go.client.gui;

import pl.edu.go.game.Game;
import pl.edu.go.game.GameObserver;
import pl.edu.go.game.GameResult;
import pl.edu.go.game.PlayerColor;
import pl.edu.go.board.Board;

public class GameModel implements GameObserver {

    private final Game game;

    public GameModel(Game game) {
        this.game = game;
        this.game.addObserver(this);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void onBoardChanged(Board board) {
        // TODO: odświeżenie GUI (przez kontroler / property)
    }

    @Override
    public void onGameEnded(GameResult result) {
        // TODO
    }

    @Override
    public void onPlayerToMoveChanged(PlayerColor player) {
        // TODO
    }
}

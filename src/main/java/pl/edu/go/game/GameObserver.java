package pl.edu.go.game;

import pl.edu.go.board.Board;

public interface GameObserver {
    void onBoardChanged(Board board);
    void onGameEnded(GameResult result);
    void onPlayerToMoveChanged(PlayerColor player);
}

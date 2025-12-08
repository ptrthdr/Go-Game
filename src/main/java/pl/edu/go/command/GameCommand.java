package pl.edu.go.command;

import pl.edu.go.game.Game;

public interface GameCommand {
    void execute(Game game) throws Exception; // możesz zrobić własny InvalidMoveException
}

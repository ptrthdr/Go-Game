package pl.edu.go.server;

import pl.edu.go.board.Board;
import pl.edu.go.command.GameCommand;
import pl.edu.go.command.TextCommandFactory;
import pl.edu.go.game.Game;
import pl.edu.go.game.GameObserver;
import pl.edu.go.game.GameResult;
import pl.edu.go.game.PlayerColor;

public class GameSession implements GameObserver {

    private final Game game;
    private final TextCommandFactory commandFactory = new TextCommandFactory();

    private ClientHandler blackPlayer;
    private ClientHandler whitePlayer;

    public GameSession(Game game) {
        this.game = game;
        this.game.addObserver(this);
    }

    public synchronized void setPlayer(PlayerColor color, ClientHandler handler) {
        if (color == PlayerColor.BLACK) {
            blackPlayer = handler;
        } else {
            whitePlayer = handler;
        }
    }


    public synchronized void handleClientMessage(ClientHandler from, String message) {
        String trimmed = message == null ? "" : message.trim();
        if (trimmed.isEmpty()) {
            return;
        }

        if (game.isFinished()) {
            from.sendLine("INFO Game already finished. Please close client.");
            return;
        }
        
        // >>> DODANE LOGOWANIE <<<
        System.out.println("Received from " + from.getColor() + ": " + trimmed);

        try {
            GameCommand command = commandFactory.fromNetworkMessage(trimmed, from.getColor());
            command.execute(game);
        } catch (Exception e) {
            from.sendLine("ERROR " + e.getMessage());
            System.out.println("Error for " + from.getColor() + ": " + e.getMessage());
        }
    }



    public synchronized void startGame() {
        if (blackPlayer != null) {
            blackPlayer.sendLine("WELCOME BLACK");
        }
        if (whitePlayer != null) {
            whitePlayer.sendLine("WELCOME WHITE");
        }
        broadcast("INFO Game started. BLACK moves first.");
        // Początkowy stan
        onBoardChanged(game.getBoard());
        onPlayerToMoveChanged(game.getCurrentPlayer());
    }

    void broadcast(String line) {
        if (blackPlayer != null) {
            blackPlayer.sendLine(line);
        }
        if (whitePlayer != null) {
            whitePlayer.sendLine(line);
        }
    }

    // --------- GameObserver ---------

    @Override
    public void onBoardChanged(Board board) {
        int[][] state = board.getState();
        int size = state.length;

        broadcast("BOARD " + size);
        for (int y = 0; y < size; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < size; x++) {
                int cell = state[x][y];
                char symbol = switch (cell) {
                    case Board.BLACK -> 'X';
                    case Board.WHITE -> 'O';
                    default -> '.';
                };
                row.append(symbol);
            }
            broadcast("ROW " + row);
        }
        broadcast("END_BOARD");
    }

    @Override
    public void onGameEnded(GameResult result) {
        String winnerStr = result.getWinner() == null
                ? "NONE"
                : result.getWinner().name();
        broadcast("END " + winnerStr + " " + result.getReason());
    }

    @Override
    public void onPlayerToMoveChanged(PlayerColor player) {
        broadcast("TURN " + player.name());
    }
}

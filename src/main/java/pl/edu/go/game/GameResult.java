package pl.edu.go.game;

public class GameResult {
    private final PlayerColor winner;
    private final String reason; // np. "resign", "both passed"

    public GameResult(PlayerColor winner, String reason) {
        this.winner = winner;
        this.reason = reason;
    }

    public PlayerColor getWinner() {
        return winner;
    }

    public String getReason() {
        return reason;
    }
}

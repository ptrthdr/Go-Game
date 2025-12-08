package pl.edu.go.command;

import pl.edu.go.game.PlayerColor;

public class TextCommandFactory {

    // przykład: "MOVE 3 4", "PASS", "RESIGN"
    public GameCommand fromNetworkMessage(String message, PlayerColor player) {
        String[] parts = message.trim().split("\\s+");
        String keyword = parts[0].toUpperCase();

        return switch (keyword) {
            case "MOVE" -> {
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                yield new PlaceStoneCommand(x, y, player);
            }
            case "PASS" -> new PassCommand(player);
            case "RESIGN" -> new ResignCommand(player);
            default -> throw new IllegalArgumentException("Unknown command: " + message);
        };
    }
}

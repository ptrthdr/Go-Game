package pl.edu.go.command;

import pl.edu.go.game.PlayerColor;

/**
 * Klasa TextCommandFactory — fabryka komend na podstawie tekstu protokołu.
 *
 * Wzorce projektowe:
 * - Factory Method:
 *   - Na podstawie tekstu (np. "MOVE 3 4") tworzy odpowiedni obiekt
 *     konkretnej komendy: PlaceStoneCommand, PassCommand, ResignCommand.
 *
 * - Command:
 *   - Tworzone obiekty implementują GameCommand i są później wykonywane
 *     na obiekcie Game.
 *
 * Rola klasy:
 * - parsuje linie otrzymane od klienta,
 * - wykrywa typ komendy (MOVE / PASS / RESIGN),
 * - zwraca gotowy obiekt GameCommand do wykonania przez serwer.
 */
public class TextCommandFactory {

    /**
     * Tworzy obiekt GameCommand na podstawie wiadomości tekstowej.
     *
     * Przykłady:
     * - "MOVE 3 4"
     * - "PASS"
     * - "RESIGN"
     */
    public GameCommand fromNetworkMessage(String message, PlayerColor player) {
        String[] parts = message.trim().split("\\s+");
        String keyword = parts[0].toUpperCase();

        return switch (keyword) {
            case "MOVE" -> {
                // spodziewamy się dwóch liczb po słowie MOVE
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

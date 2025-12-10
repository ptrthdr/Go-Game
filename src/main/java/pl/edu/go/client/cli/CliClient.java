package pl.edu.go.client.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Klasa CliClient — klient konsolowy gry Go.
 *
 * Rola klasy:
 * - łączy się z serwerem (host + port),
 * - w osobnym wątku nasłuchuje komunikatów z serwera:
 *   * INFO, WELCOME, TURN, ERROR, END,
 *   * BOARD / ROW / END_BOARD — opis aktualnej planszy,
 * - parsuje BOARD / ROW / END_BOARD i rysuje planszę w czytelnej formie
 *   (siatka z numerami wierszy i kolumn),
 * - w głównej pętli czyta komendy użytkownika z klawiatury i wysyła je
 *   do serwera (MOVE x y, PASS, RESIGN),
 * - po otrzymaniu komunikatu END ... automatycznie kończy działanie.
 *
 * Klasa pełni rolę prostego interfejsu tekstowego (UI) dla gry Go.
 */
public class CliClient {

    // flaga sterująca główną pętlą; zmieniana przez wątek nasłuchujący
    private static volatile boolean running = true;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;

        // opcjonalne parametry: host port
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to " + host + ":" + port);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()), true);

            // Czytanie komend z klawiatury
            Scanner scanner = new Scanner(System.in);
            System.out.println("Commands: MOVE <col> <row> (np. MOVE B 2) | PASS | RESIGN  (or: exit)");

            // Wątek nasłuchujący serwera (startujemy po wypisaniu komend,
            // żeby nie mieszać się z pierwszym rysowaniem planszy)
            Thread listener = new Thread(() -> listenToServer(in), "ServerListener");
            listener.setDaemon(true);
            listener.start();

            // Główna pętla: odczyt linii od użytkownika
            while (running && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // użytkownik chce zakończyć klienta ręcznie
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                }

                // Obsługa skróconego formatu: MOVE B 2 (kolumna jako litera)
                String toSend = line;
                // Umożliwiamy format: MOVE C 3 (kolumna jako litera)
                String upper = line.toUpperCase();

                if (upper.startsWith("MOVE")) {
                    String[] parts = upper.split("\\s+");
                    if (parts.length == 3) {
                        // parts[0] = MOVE, parts[1] = kolumna (np. B), parts[2] = wiersz (np. 2)
                        String colPart = parts[1];
                        String rowPart = parts[2];

                        if (colPart.length() == 1 && colPart.charAt(0) >= 'A' && colPart.charAt(0) <= 'Z') {
                            char colChar = colPart.charAt(0);
                            int x = colChar - 'A';           // A -> 0, B -> 1, ...
                            int y = Integer.parseInt(rowPart); // zakładamy, że wiersze są podawane jako 0,1,2,...

                            toSend = "MOVE " + x + " " + y;
                        }
                    }
                    // Jeśli format nie pasuje (np. MOVE 1 2), to wysyłamy linię bez zmian.
                }

                // wysyłamy komendę do serwera
                out.println(toSend);
            }

            System.out.println("Client exiting...");
        } catch (IOException e) {
            System.out.println("Cannot connect: " + e.getMessage());
        }
    }

    /**
     * Wątek nasłuchujący komunikatów z serwera.
     *
     * Tutaj parsujemy:
     * - BOARD <size>
     * - ROW <ciąg znaków X/O/.>
     * - END_BOARD
     *
     * oraz wypisujemy inne komunikaty (INFO, TURN, ERROR, END).
     */
    private static void listenToServer(BufferedReader in) {
        Integer boardSize = null;
        List<String> boardRows = new ArrayList<>();

        try {
            String line;
            while ((line = in.readLine()) != null) {

                // ---- Parsowanie planszy (BOARD/ROW/END_BOARD) ----

                if (line.startsWith("BOARD ")) {
                    // początek nowej planszy
                    try {
                        boardSize = Integer.parseInt(line.substring("BOARD ".length()).trim());
                    } catch (NumberFormatException e) {
                        boardSize = null;
                    }
                    boardRows.clear();
                    // nie wypisujemy surowej linii BOARD
                    continue;
                }

                if (line.startsWith("ROW ")) {
                    // kolejny wiersz planszy
                    if (boardSize != null) {
                        String row = line.substring("ROW ".length());
                        boardRows.add(row);
                    }
                    // nie wypisujemy surowej linii ROW
                    continue;
                }

                if ("END_BOARD".equals(line)) {
                    // koniec opisu planszy -> rysujemy ją
                    if (boardSize != null && boardRows.size() == boardSize) {
                        displayBoard(boardSize, boardRows);
                    } else {
                        System.out.println("(Received incomplete board data)");
                    }
                    boardSize = null;
                    boardRows.clear();
                    continue;
                }

                // ---- Inne komunikaty ----

                System.out.println(line);

                // koniec gry -> kończymy klienta
                if (line.startsWith("END ")) {
                    running = false;
                    break;
                }
            }

            System.out.println("Server closed connection.");
        } catch (IOException e) {
            System.out.println("Connection lost: " + e.getMessage());
            running = false;
        }
    }

    /**
     * Rysuje planszę w terminalu na podstawie listy wierszy ('.', 'X', 'O').
     *
     * Przykład:
     *      0 1 2 3 4
     *   0  . . X . .
     *   1  . O . . .
     *   ...
     */
    private static void displayBoard(int size, List<String> rows) {
        System.out.println();
        System.out.println("Current board:");

        // nagłówek z literami kolumn (A, B, C, ...)
        System.out.print("    ");
        for (int x = 0; x < size; x++) {
            char col = (char) ('A' + x);
            System.out.print(col + " ");
        }
        System.out.println();


        // każdy wiersz planszy
        for (int y = 0; y < size; y++) {
            String row = rows.get(y);

            // numer wiersza z lewej
            System.out.printf("%2d  ", y);

            for (int x = 0; x < size; x++) {
                char c = (x < row.length()) ? row.charAt(x) : '.';

                // mapowanie na ładniejsze symbole
                char symbol = switch (c) {
                    case 'X' -> '●';  // black
                    case 'O' -> '○';  // white
                    case '.' -> '.';
                    default -> c;
                };

                System.out.print(symbol + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

package pl.edu.go.client.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Prosty klient konsolowy:
 * - łączy się z serwerem Go
 * - w osobnym wątku nasłuchuje komunikatów z serwera
 * - w głównym wątku czyta komendy użytkownika i wysyła je na serwer
 *
 * Komendy:
 *   MOVE x y
 *   PASS
 *   RESIGN
 *   exit / quit  -> zakończenie klienta
 *
 * Po otrzymaniu od serwera komunikatu "END ..." klient automatycznie kończy pracę.
 */
public class CliClient {

    // flaga sterująca główną pętlą; zmieniana przez wątek nasłuchujący
    private static volatile boolean running = true;

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

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

            // --- Wątek nasłuchujący serwera ---
            Thread listener = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);

                        // jeśli serwer ogłasza koniec gry, kończymy klienta
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
            }, "ServerListener");

            // daemon = nie blokuje zamknięcia JVM po zakończeniu main
            listener.setDaemon(true);
            listener.start();

            // --- Główna pętla: czytanie komend użytkownika ---
            Scanner scanner = new Scanner(System.in);
            System.out.println("Commands: MOVE x y | PASS | RESIGN  (or: exit)");

            while (running && scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // użytkownik chce zakończyć klienta
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                }

                // wysyłamy komendę do serwera
                out.println(line);
            }

            System.out.println("Client exiting...");
        } catch (IOException e) {
            System.out.println("Cannot connect: " + e.getMessage());
        }
    }
}

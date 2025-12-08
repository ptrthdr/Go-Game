package pl.edu.go.server;

import pl.edu.go.game.PlayerColor;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameSession session;
    private final PlayerColor color;
    private PrintWriter out;

    public ClientHandler(Socket socket, GameSession session, PlayerColor color) {
        this.socket = socket;
        this.session = session;
        this.color = color;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void sendLine(String line) {
        if (out != null) {
            out.println(line);
            out.flush();
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            this.out = writer;
            sendLine("INFO Connected as " + color.name());

            String line;
            while ((line = in.readLine()) != null) {
                session.handleClientMessage(this, line);
            }
        } catch (IOException e) {
            System.out.println("Client " + color + " disconnected: " + e.getMessage());
        }
    }
}

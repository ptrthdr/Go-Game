package pl.edu.go.client.gui;

import java.io.PrintWriter;
import java.net.Socket;

public class GameController {

    private final GameModel model;
    private final BoardView view;
    private final PrintWriter serverOut; // do wysyłania komend do serwera

    public GameController(GameModel model, BoardView view, Socket socket) throws Exception {
        this.model = model;
        this.view = view;
        this.serverOut = new PrintWriter(socket.getOutputStream(), true);
    }

    public void onFieldClicked(int x, int y) {
        // kliknięcie w GUI -> komenda do serwera
        serverOut.println("MOVE " + x + " " + y);
    }

    public void onPassClicked() {
        serverOut.println("PASS");
    }

    public void onResignClicked() {
        serverOut.println("RESIGN");
    }
}

package pl.edu.go.game;

public interface ObservableGame {
    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
}

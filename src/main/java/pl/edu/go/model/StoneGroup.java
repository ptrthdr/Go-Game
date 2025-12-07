package pl.edu.go.model;

import java.util.HashSet;
import java.util.Set;

public class StoneGroup {
    private final int color;
    private final Set<Stone> stones = new HashSet<>();

    public StoneGroup(int color) {
        this.color = color;
    }

    public void addStone(Stone s) {
        stones.add(s);
    }

    public int getColor() {
        return color;
    }

    public Set<Stone> getStones() {
        return stones;
    }

    public Set<String> getPositions() {
        Set<String> result = new HashSet<>();
        for (Stone s : stones) {
            result.add(s.getX() + "," + s.getY());
        }
        return result;
    }

    @Override
    public String toString() {
        return "Group(color=" + color + ", stones=" + stones + ")";
    }
}

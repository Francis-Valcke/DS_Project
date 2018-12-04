package classes;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    private String username;
    private boolean ready;
    private int score;

    public PlayerInfo(String username, boolean ready, int score) {
        this.username = username;
        this.ready = ready;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public boolean isReady() {
        return ready;
    }

    public int getScore() {
        return score;
    }
}

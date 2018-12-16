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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setScore(int score) {
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

package applicationserver;

import classes.PlayerInfo;
import interfaces.ClientInterface;

import java.util.Objects;

public class Player {
    private int score;
    private ClientInterface gameclient;
    private String username;
    private boolean ready;

    public Player(ClientInterface gameclient, String username) {
        this.gameclient = gameclient;
        this.username = username;
        score = 0;
        ready = false;
    }

    public void addPoint() {
        score += 1;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ClientInterface getGameclient() {
        return gameclient;
    }

    public void setGameclient(ClientInterface gameclient) {
        this.gameclient = gameclient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public PlayerInfo convertToPlayerInfo() {
        return new PlayerInfo(username, ready, score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}

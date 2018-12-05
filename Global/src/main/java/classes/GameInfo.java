package classes;

import java.io.Serializable;

public class GameInfo implements Serializable {
    String name;
    boolean started;
    int id;
    int width;
    int height;
    int maxPlayers;
    int numberOfPlayersJoined;
    int theme_id;

    public GameInfo(String name, int id, int width, int height, int maxPlayers, int numberOfPlayersJoined, boolean started, int theme_id) {
        this.name = name;
        this.id = id;
        this.width = width;
        this.height = height;
        this.maxPlayers = maxPlayers;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.theme_id = theme_id;
    }

    public String getName() {
        return name;
    }

    public boolean isStarted() {
        return started;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getNumberOfPlayersJoined() {
        return numberOfPlayersJoined;
    }

    public int getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(int theme_id) {
        this.theme_id = theme_id;
    }
}

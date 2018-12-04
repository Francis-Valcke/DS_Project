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

    public GameInfo(String name, int id, int width, int height, int maxPlayers, int numberOfPlayersJoined, boolean started) {
        this.name = name;
        this.id = id;
        this.width = width;
        this.height = height;
        this.maxPlayers = maxPlayers;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
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
}

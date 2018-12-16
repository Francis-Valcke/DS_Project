package classes;

import java.io.Serializable;

public class GameInfo implements Serializable {

    String hostName;
    String name;
    boolean started;
    String id;
    int width;
    int height;
    int maxPlayers;
    int numberOfPlayersJoined;
    int theme_id;

    public GameInfo() {
    }

    public GameInfo(String hostName, String name, String id, int width, int height, int maxPlayers, int numberOfPlayersJoined, boolean started, int theme_id) {
        this.hostName = hostName;
        this.name = name;
        this.id = id;
        this.width = width;
        this.height = height;
        this.maxPlayers = maxPlayers;
        this.numberOfPlayersJoined = numberOfPlayersJoined;
        this.theme_id = theme_id;
    }

    public String getHostName() {
        return hostName;
    }

    public String getName() {
        return name;
    }

    public boolean isStarted() {
        return started;
    }

    public String getId() {
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

}

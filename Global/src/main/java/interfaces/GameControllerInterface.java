package interfaces;

import classes.Coordinate;
import classes.PlayerInfo;

import java.util.List;

public interface GameControllerInterface {

    void showTile(Coordinate c, int value);

    void hideTile(Coordinate c, int delay);

    void updateInfoLabel(String s);

    void startGame();

    void updatePlayerList(List<PlayerInfo> playerInfoList);

}

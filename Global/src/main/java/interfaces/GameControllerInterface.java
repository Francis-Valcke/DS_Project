package interfaces;

import classes.Coordinate;
import classes.PlayerInfo;

import java.util.List;


public interface GameControllerInterface {

    /**
     * Geeft aan dat de client een tile moet tonen
     *
     * @param c     coordinaat van de tile
     * @param value id van foto die moet getoond worden
     */
    void showTile(Coordinate c, int value);

    /**
     * Geeft aan dat de client een tile moet verbergen
     * @param c coordinaat van de tile
     * @param delay tijd dat de client moet wachten om te verbergen
     */
    void hideTile(Coordinate c, int delay);

    /**
     * Update het info label bij de client
     * @param s String die het label moet tonen
     */
    void updateInfoLabel(String s);

    /**
     * Start de game bij de client
     */
    void startGame();

    /**
     * Update lijst van spelers (username en score)
     * @param playerInfoList Lijst met info
     */
    void updatePlayerList(List<PlayerInfo> playerInfoList);

}

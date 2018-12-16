package interfaces;

import classes.Coordinate;
import classes.GameInfo;
import classes.PlayerInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;


public interface GameInterface extends Remote {

    /**
     * Geeft aan dat een bepaalde Client klaar is om de game te starten
     *
     * @param client
     * @throws RemoteException
     */
    void readyUp(ClientInterface client) throws RemoteException;

    /**
     * Laat toe om het spel te verlaten
     * @param client
     * @throws RemoteException
     */
    void leaveGame(ClientInterface client) throws RemoteException;

    /**
     * @return hoogte van het speelveld
     * @throws RemoteException
     */
    int getHeight() throws RemoteException;

    /**
     * @return breedte van speelveld
     * @throws RemoteException
     */
    int getWidth() throws RemoteException;

    int getValueOf(Coordinate coordinate) throws RemoteException;

    /**
     * Convert een game naar gameinfo object
     * @return
     * @throws RemoteException
     */
    GameInfo getGameInfo() throws RemoteException;

    /**
     * @return Lijst met alle info van alle spelers
     * @throws RemoteException
     */
    List<PlayerInfo> getPlayerlist() throws RemoteException;

    /**
     * Methode die alle omgedraaide velden teruggeeft met hun waarde. Wordt gebruikt voor spectators die
     * midgame joinen
     * @return
     * @throws RemoteException
     */
    HashMap<Coordinate, Integer> getFlippedFields() throws RemoteException;

    /**
     * @return id van thema van deze game
     * @throws RemoteException
     */
    int getThemeId() throws RemoteException;
}

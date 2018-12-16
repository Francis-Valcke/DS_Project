package interfaces;

import classes.Coordinate;
import classes.PlayerInfo;
import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import exceptions.LeftGameException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Deze interface implementeert alle methodes die de application server moet kunnen oproepen bij de client
 * tijdens het spelen van een game.
 */
public interface ClientInterface extends Remote {

    /**
     * Vraagt aan de Client om een move te doen en wacht ook
     *
     * @return Coordinate van de om te draaien kaart
     * @throws RemoteException
     * @throws LeftGameException wordt gegooid als de speler beslist om te game te verlaten tijdens zijn beurt
     */
    Coordinate requestMove() throws RemoteException, LeftGameException;

    /**
     * Update lijst van spelers (username en score)
     * @param playerInfoList Lijst met info
     */
    void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException;

    /**
     * Geeft aan dat de client een tile moet verbergen
     * @param c coordinaat van de tile
     * @param delay tijd dat de client moet wachten om te verbergen
     */
    void hideTile(Coordinate c, int delay) throws RemoteException;

    /**
     * Geeft aan dat de client een tile moet tonen
     * @param c coordinaat van de tile
     * @param value id van foto die moet getoond worden
     */
    void showTile(Coordinate c, int value) throws RemoteException;

    boolean isSameClient(ClientInterface c) throws RemoteException;

    /**
     * Start de game bij de client
     */
    void setGameStarted() throws RemoteException;

    /**
     * Update het info label bij de client
     * @param s String die het label moet tonen
     */
    void updateInfoLabel(String s) throws RemoteException;

    /**
     * Geeft aan dat de client met een andere application server moet verbinden
     * @param server nieuwe server
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid indien de credentials niet kloppen
     * @throws AlreadyPresentException wordt gegooid indien de gebruiker als op deze server aanwezig is
     */
    void transferTo(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    /**
     * Geeft aan dat de client met een andere application server moet verbinden
     * @param serverName hostname van nieuwe server
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid indien de credentials niet kloppen
     * @throws AlreadyPresentException wordt gegooid indien de gebruiker als op deze server aanwezig is
     */
    void transferTo(String serverName) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void disconnect(boolean invalidate) throws RemoteException;

    void connect() throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void connect(String serverName) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void connect(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    /**
     * Vraag username op van client
     * @return username
     * @throws RemoteException
     */
    String getUsername() throws RemoteException;

    /**
     * Vraag token op van client
     * @return token
     * @throws RemoteException
     */
    String getToken() throws RemoteException;

    void setGameController(GameControllerInterface gameController) throws RemoteException;

    GameControllerInterface getGameController() throws RemoteException;

    AppLoginInterface getAppLogin() throws RemoteException;

    void setAppLogin(AppLoginInterface appLogin) throws RemoteException;
}

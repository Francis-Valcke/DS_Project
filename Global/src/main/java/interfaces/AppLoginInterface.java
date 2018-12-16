package interfaces;


import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Deze interface implementeert alle methodes die Client kan uitvoeren om in te loggen
 */
public interface AppLoginInterface extends Remote {


    List<ClientInterface> getConnectedClients() throws RemoteException;

    void init(ServerDispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) throws RemoteException;

    /**
     * Logt een client inloggen
     *
     * @param client
     * @return Geeft de lobby terug
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid als het paswoord en/of username niet kloppen
     * @throws AlreadyPresentException     wordt gegooid als een speler als is ingelogd
     */
    LobbyInterface clientLogin(ClientInterface client) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    /**
     * logt een client uit
     *
     * @param client     uit te loggen client
     * @param invalidate boolean om token te invalideren
     * @throws RemoteException
     */
    void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException;

    /**
     * Methode om te checken als de server nog online is
     *
     * @throws RemoteException
     */
    void ping() throws RemoteException;

}


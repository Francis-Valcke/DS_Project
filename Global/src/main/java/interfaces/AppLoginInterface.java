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

    /**
     * Logt een client in
     *
     * @param username
     * @param token
     * @return Geeft de lobby terug
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid als het paswoord en/of username niet kloppen
     * @throws AlreadyPresentException     wordt gegooid als een speler als is ingelogd
     */
    LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException;

    List<ClientInterface> getConnectedClients() throws RemoteException;

}

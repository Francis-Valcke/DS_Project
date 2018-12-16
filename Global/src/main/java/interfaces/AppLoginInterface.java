package interfaces;


import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AppLoginInterface extends Remote {

    List<ClientInterface> getConnectedClients() throws RemoteException;

    void init(ServerDispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) throws RemoteException;

    LobbyInterface clientLogin(ClientInterface client) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException;


}

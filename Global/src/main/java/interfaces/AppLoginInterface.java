package interfaces;


import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppLoginInterface extends Remote {

    void init(ServerDispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) throws RemoteException;

    LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    void setDb(DatabaseInterface db) throws RemoteException;

    void setDispatch(ServerDispatcherInterface dispatcherInterface) throws RemoteException;

    void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException;

    void setLobby(LobbyInterface lobbyInterface) throws RemoteException;
}

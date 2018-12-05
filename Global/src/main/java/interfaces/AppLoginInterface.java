package interfaces;


import exceptions.InvalidCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppLoginInterface extends Remote {

    LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException;

    void setDb(DatabaseInterface db) throws RemoteException;

    void setDispatch(DispatcherInterface dispatcherInterface) throws RemoteException;

    void setLobby(LobbyInterface lobbyInterface) throws RemoteException;
}

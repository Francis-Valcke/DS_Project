package interfaces;


import exceptions.InvalidCredentialsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppLoginInterface extends Remote {
    public LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException;
}

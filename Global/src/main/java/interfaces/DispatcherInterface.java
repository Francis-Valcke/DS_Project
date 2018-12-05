package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DispatcherInterface extends Remote {

    public void registerDatabaseServer(String id, int port) throws RemoteException;

    public DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException;

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public boolean isTokenValid(String username, String token) throws RemoteException;

    public ApplicationServerInterface getApplicationServer() throws RemoteException;

    public void broadCastLobby(LobbyInterface lobby) throws RemoteException;




}

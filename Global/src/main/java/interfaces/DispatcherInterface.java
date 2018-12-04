package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInterface extends Remote {

    public void registerDatabaseServer(String id, int port) throws RemoteException;

    public DatabaseInterface registerApplicationServer(String id, int port) throws RemoteException;

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public boolean isTokenValid(String username, String token) throws RemoteException;

    public AppLoginInterface getApplicationServer() throws RemoteException;
}

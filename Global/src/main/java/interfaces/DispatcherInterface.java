package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface DispatcherInterface extends Remote {

    public void registerDatabaseServer(DatabaseInterface dbi) throws RemoteException;

    public DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException;

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public boolean isTokenValid(String username, String token) throws RemoteException;

    public ApplicationServerInterface getApplicationServer() throws RemoteException;

    public void broadCastLobby(LobbyInterface lobby) throws RemoteException;

    ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException;

    ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException;

    Set<LobbyInterface> requestAllLobbies() throws RemoteException;
}

package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DispatcherInterface extends Remote {

    public void registerDatabaseServer(DatabaseInterface dbi) throws RemoteException;

    public DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException;

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException;

    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public boolean isTokenValid(String username, String token) throws RemoteException;

    public ApplicationServerInterface getApplicationServer() throws RemoteException;

    //Load balance the mobile users over the available servers
    VirtualClientServerInterface getVirtualClientServer() throws RemoteException;

    ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException;

    ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException;

    boolean isConnected(String username) throws RemoteException;

    void addUser(ApplicationServerInterface appServer, String username) throws RemoteException;

    void removeUser(ApplicationServerInterface appServer, String username) throws RemoteException;
}

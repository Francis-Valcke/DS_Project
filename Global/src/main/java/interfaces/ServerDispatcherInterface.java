package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerDispatcherInterface extends Remote {

    void registerDatabaseServer(DatabaseInterface dbi) throws RemoteException;

    DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException;

    void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException;

    boolean isTokenValid(String username, String token) throws RemoteException;

    boolean isConnected(String username) throws RemoteException;

    void addUser(ApplicationServerInterface appServer, String username) throws RemoteException;

    void removeUser(ApplicationServerInterface appServer, String username) throws RemoteException;

    ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException;

}


package dispatcher;

import interfaces.ApplicationServerInterface;
import interfaces.DatabaseInterface;
import interfaces.ServerDispatcherInterface;
import interfaces.VirtualClientServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerDispatcher extends UnicastRemoteObject implements ServerDispatcherInterface {
    Dispatcher dispatch = Dispatcher.getInstance();
    private static ServerDispatcher instance;

    static {
        try {
            instance = new ServerDispatcher();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ServerDispatcher() throws RemoteException {

    }

    public static ServerDispatcher getInstance() {
        return instance;
    }

    public void registerDatabaseServer(DatabaseInterface dbi) throws RemoteException {
        dispatch.registerDatabaseServer(dbi);
    }

    public DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException {
        return dispatch.registerApplicationServer(server);
    }

    public void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException {
        dispatch.registerVirtualClientServer(server);
    }

    public boolean isConnected(String username) throws RemoteException {
        return dispatch.isConnected(username);
    }

    public void addUser(ApplicationServerInterface appServer, String username) throws RemoteException {
        dispatch.addUser(appServer, username);
    }

    public void removeUser(ApplicationServerInterface appServer, String username) throws RemoteException {
        dispatch.removeUser(appServer, username);
    }

    public ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException {
        return dispatch.getApplicationServerByFreeSlots(slots);
    }

    public boolean isTokenValid(String username, String token) throws RemoteException {
        return dispatch.isTokenValid(username, token);
    }

    public ApplicationServerInterface getApplicationServer() throws RemoteException {
        return dispatch.getApplicationServer();
    }

    public ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException {
        return dispatch.getApplicationServerByName(name);
    }


}

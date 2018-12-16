package applicationserver;

import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class AppLogin extends UnicastRemoteObject implements AppLoginInterface {

    private static AppLogin instance;

    static {
        try {
            instance = new AppLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ApplicationServerInterface applicationServer;
    private LobbyInterface lobby;
    private DatabaseInterface db;
    private ServerDispatcherInterface dispatch;

    private List<ClientInterface> connectedClients = new LinkedList<>();


    private AppLogin() throws RemoteException {
    }

    public static AppLogin getInstance() {
        return instance;
    }

    @Override
    public List<ClientInterface> getConnectedClients() {
        return connectedClients;
    }

    @Override
    public void init(ServerDispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) {
        this.lobby = lobby;
        this.db = db;
        this.dispatch = dispatch;
        this.applicationServer = appServer;
    }

    @Override
    public LobbyInterface clientLogin(ClientInterface client) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        if (db.isTokenValid(client.getUsername(), client.getToken())) {
            if (!dispatch.isConnected(client.getUsername())) {
                dispatch.addUser(applicationServer, client.getUsername());
                connectedClients.add(client);
                return lobby;
            }
            else throw new AlreadyPresentException("The user is already logged in");
        }
        throw new InvalidCredentialsException("Wrong credentials.");
    }

    @Override
    public void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException {
        if (invalidate) db.inValidateToken(client.getUsername());
        dispatch.removeUser(applicationServer, client.getUsername());
        connectedClients.remove(client);
    }

}

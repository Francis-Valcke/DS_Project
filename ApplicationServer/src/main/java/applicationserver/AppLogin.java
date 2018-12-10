package applicationserver;

import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
    private DispatcherInterface dispatch;

    private AppLogin() throws RemoteException {
    }

    public static AppLogin getInstance() {
        return instance;
    }

    @Override
    public void init(DispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) {
        this.lobby = lobby;
        this.db = db;
        this.dispatch = dispatch;
        this.applicationServer = appServer;
    }

    @Override
    public LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        if (db.isTokenValid(username, token)) {
            if(!dispatch.isConnected(username))
                return lobby;
            else throw new AlreadyPresentException("The user is already logged in");
        }
        throw new InvalidCredentialsException("Wrong credentials.");
    }

    @Override
    public void clientLogout(ClientInterface client, boolean invalidate) throws RemoteException {
        if (invalidate) db.inValidateToken(client.getUsername());
        dispatch.removeUser(applicationServer, client.getUsername());
    }

    public LobbyInterface getLobby() {
        return lobby;
    }

    @Override
    public void setLobby(LobbyInterface lobbyInterface) {
        this.lobby = lobbyInterface;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public DatabaseInterface getDb() {
        return db;
    }

    public void setDb(DatabaseInterface db) {
        this.db = db;
    }

    public DispatcherInterface getDispatch() {
        return dispatch;
    }

    public void setDispatch(DispatcherInterface dispatch) {
        this.dispatch = dispatch;
    }
}

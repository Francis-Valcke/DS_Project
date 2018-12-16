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
    private ServerDispatcherInterface dispatch;

    private AppLogin() throws RemoteException {
    }

    public static AppLogin getInstance() {
        return instance;
    }

    @Override
    public void init(ServerDispatcherInterface dispatch, ApplicationServerInterface appServer, DatabaseInterface db, LobbyInterface lobby) {
        this.lobby = lobby;
        this.db = db;
        this.dispatch = dispatch;
        this.applicationServer = appServer;
    }

    @Override
    public LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        if (db.isTokenValid(username, token)) {
            if (!dispatch.isConnected(username)) {
                dispatch.addUser(applicationServer, username);
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
    }

}

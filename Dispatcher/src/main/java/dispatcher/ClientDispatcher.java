package dispatcher;


import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.ApplicationServerInterface;
import interfaces.ClientDispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementeert alle dispatcher functies waar clients aankunnen
 */
public class ClientDispatcher extends UnicastRemoteObject implements ClientDispatcherInterface {
    Dispatcher dispatch = Dispatcher.getInstance();
    private static ClientDispatcher instance;

    static {
        try {
            instance = new ClientDispatcher();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ClientDispatcher() throws RemoteException {

    }

    public static ClientDispatcher getInstance() {
        return instance;
    }

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        return dispatch.requestNewToken(username, password);
    }

    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        dispatch.registerNewUser(username, password);
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

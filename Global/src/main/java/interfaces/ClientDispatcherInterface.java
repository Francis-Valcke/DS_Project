package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientDispatcherInterface extends Remote {

    String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    boolean isTokenValid(String username, String token) throws RemoteException;

    ApplicationServerInterface getApplicationServer() throws RemoteException;

    //Load balance the mobile users over the available servers
    VirtualClientServerInterface getVirtualClientServer() throws RemoteException;

    ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException;

}
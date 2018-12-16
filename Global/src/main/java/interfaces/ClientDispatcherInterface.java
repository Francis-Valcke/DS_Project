package interfaces;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Deze interface implementeert alle methodes die een Client kan uitvoeren op de dispatcher
 */
public interface ClientDispatcherInterface extends Remote {

    /**
     * Vraag een nieuwe token aan die 24u geldig is
     *
     * @param username
     * @param password
     * @return token
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid indien het paswoord en/of username niet klopt
     */
    String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    /**
     * Registreer een nieuwe gebruiker
     * @param username
     * @param password
     * @throws RemoteException
     * @throws UserAlreadyExistsException wordt gegooid indien er al een gebruiker bestaat met deze username
     */
    void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    /**
     * Checkt als een token klopt en minder dan 24u oud is
     * @param username
     * @param token
     * @return true als de token valid is
     * @throws RemoteException
     */
    boolean isTokenValid(String username, String token) throws RemoteException;

    /**
     * Request een applicationserver om mee te verbinden
     * @return een application server waarop plaats vrij is
     * @throws RemoteException
     */
    ApplicationServerInterface getApplicationServer() throws RemoteException;

    /**
     * Request een specifieke application server (wordt gebruikt bij transfer)
     * @param name hostname van de server
     * @return application server
     * @throws RemoteException
     */
    ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException;

}
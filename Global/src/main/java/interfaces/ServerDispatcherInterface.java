package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Deze interface bevat alle methodes die application en database servers kunnen uitvoeren op de dispatcher
 */
public interface ServerDispatcherInterface extends Remote {

    /**
     * Registreer een nieuwe database server
     *
     * @param dbi databaseinterface van database
     * @throws RemoteException
     */
    void registerDatabaseServer(DatabaseInterface dbi) throws RemoteException;

    /**
     * Registreer een nieuwe application server
     * @param server applicationserverinterface
     * @return de database die de applicationserver moet gebruiken
     * @throws RemoteException
     */
    DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException;

    void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException;

    /**
     * Check als een token van een gebruiker overeenkomt en nog valid is
     * @param username
     * @param token
     * @return true als de token valid is
     * @throws RemoteException
     */
    boolean isTokenValid(String username, String token) throws RemoteException;

    /**
     * Checkt als een gebruiker al is verbonden
     * @param username
     * @return true als een gebruiker verbonden is
     * @throws RemoteException
     */
    boolean isConnected(String username) throws RemoteException;

    void addUser(ApplicationServerInterface appServer, String username) throws RemoteException;

    void removeUser(ApplicationServerInterface appServer, String username) throws RemoteException;

    /**
     * Verkrijg de applicatie server met het meeste vrije sloten
     * @param slots
     * @return
     * @throws RemoteException
     */
    ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException;

    /**
     * Markeer het paar als ontoegankelijk als voorbereiding op af sluiten.
     *
     * @param server
     * @return
     * @throws RemoteException
     */
    boolean markApplicationServerPairUnavailable(ApplicationServerInterface server) throws RemoteException;

    ApplicationServerInterface getApplicationServer() throws RemoteException;
}


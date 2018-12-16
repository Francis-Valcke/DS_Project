package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Deze interface staat in voor alles wat te maken heeft met de application servers die remote gebruikt worden.
 */
public interface ApplicationServerInterface extends Remote {

    /**
     * Geeft de naam terug van de server. Deze wordt gebruikt ter identificatie van de lobbies en games.
     *
     * @return naam
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Geeft het IP terug van de application server.
     *
     * @return IP
     * @throws RemoteException
     */
    String getIp() throws RemoteException;

    /**
     * Geeft de poort terug van de application server.
     *
     * @return poort
     * @throws RemoteException
     */
    int getPort() throws RemoteException;

    /**
     * Geeft de AppLogin interface terug die gebruikt wordt door de clients om in te loggen.
     *
     * @return AppLogin ter gebruik voor de clients bij login
     * @throws RemoteException
     */
    AppLoginInterface getAppLogin() throws RemoteException;

    /**
     * Geeft de Lobby terug van de application server. Deze lobby bevat de live games van de app server.
     *
     * @return LobbyInterface ter gebruik voor de clients na login
     * @throws RemoteException
     */
    LobbyInterface getLobby() throws RemoteException;

    /**
     * Geeft de backup application server terug van de huidige server.
     *
     * @return Backup application server
     * @throws RemoteException
     */
    ApplicationServerInterface getBackupServer() throws RemoteException;

    /**
     * Zet de backupserver van de huidige server.
     *
     * @param backupServer
     * @throws RemoteException
     */
    void setBackupServer(ApplicationServerInterface backupServer) throws RemoteException;

    /**
     * Deze methode wordt gebruikt bij het bepalen of er nog genoeg plaats is in de applicatie server voor
     * een bepaalde nieuwe game.
     *
     * @param slots aantal slots die de nieuwe game in neemt
     * @return Kan er bij of niet
     * @throws RemoteException
     */
    boolean canFit(int slots) throws RemoteException;

    /**
     * Test als de application server reageert
     *
     * @throws RemoteException
     */
    void ping() throws RemoteException;

}

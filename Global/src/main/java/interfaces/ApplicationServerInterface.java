package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface ApplicationServerInterface extends Remote {

    void startLogin() throws RemoteException;

    void registerWithDispatcher() throws RemoteException;

    String getName() throws RemoteException;

    String getIp() throws RemoteException;

    int getPort() throws RemoteException;

    int getRestPort() throws RemoteException;

    int getDispatcherPort() throws RemoteException;

    String getDispatcherIp() throws RemoteException;

    DatabaseInterface getDb() throws RemoteException;

    void setDb(DatabaseInterface db) throws RemoteException;

    AppLoginInterface getAppLogin() throws RemoteException;

    void setAppLogin(AppLoginInterface appLogin) throws RemoteException;

    LobbyInterface getLobby() throws RemoteException;

    void setLobby(LobbyInterface lobby) throws RemoteException;

    ApplicationServerInterface getBackupServer() throws RemoteException;

    void setBackupServer(ApplicationServerInterface backupServer) throws RemoteException;

    boolean isFull() throws RemoteException;

    List<ClientInterface> getConnectedClients() throws RemoteException;

    void addConnectedClient(ClientInterface client) throws RemoteException;

    void updateLobby(LobbyInterface lobby) throws RemoteException;

    Set<LobbyInterface> getAllLobbies() throws RemoteException;

    int getFreeSlots() throws RemoteException;

    boolean canFit(int slots) throws RemoteException;

    void transferClient(ClientInterface client) throws RemoteException;

    void disconnect(ClientInterface client) throws RemoteException;

    void setAllLobbies(Set<LobbyInterface> lobbies) throws RemoteException;
}

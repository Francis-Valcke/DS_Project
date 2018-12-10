package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

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

    int getFreeSlots() throws RemoteException;

    boolean canFit(int slots) throws RemoteException;

}

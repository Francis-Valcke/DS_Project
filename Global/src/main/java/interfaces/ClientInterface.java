package interfaces;

import classes.Coordinate;
import classes.PlayerInfo;
import exceptions.LeftGameException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {

    public Coordinate requestMove() throws RemoteException, LeftGameException;

    public String getUsername() throws RemoteException;

    public String getToken() throws RemoteException;

    public void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException;

    public void hideTile(Coordinate c, int delay) throws RemoteException;

    public void showTile(Coordinate c, int value) throws RemoteException;

    public boolean isSameClient(ClientInterface c) throws RemoteException;

    public void setGameStarted() throws RemoteException;

    public void updateInfoLabel(String s) throws RemoteException;

    public void showLobbies(List<LobbyInterface> lobbies) throws RemoteException;

    void refreshLobbies() throws RemoteException;

    ApplicationServerInterface getApplicationServer() throws RemoteException;

    void setApplicationServer(ApplicationServerInterface appServer) throws RemoteException;

    ApplicationServerInterface getBackupApplicationServer() throws RemoteException;

    void setBackupApplicationServer(ApplicationServerInterface appServer) throws RemoteException;

    void disconnect() throws RemoteException;

    void transferTo(String serverName) throws RemoteException;

    void connect(ApplicationServerInterface serverInterface) throws RemoteException;
}

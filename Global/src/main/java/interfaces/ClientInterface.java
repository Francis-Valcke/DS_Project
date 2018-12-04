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

}

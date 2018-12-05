package interfaces;

import classes.Coordinate;
import classes.GameInfo;
import classes.PlayerInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;


public interface GameInterface extends Remote {

    public void readyUp(ClientInterface client) throws RemoteException;

    public void leaveGame(ClientInterface client) throws RemoteException;

    public int getHeight() throws RemoteException;

    public int getWidth() throws RemoteException;

    public boolean isStarted() throws RemoteException;

    public GameInfo getGameInfo() throws RemoteException;

    public List<PlayerInfo> getPlayerlist() throws RemoteException;

    public HashMap<Coordinate, Integer> getFlippedFields() throws RemoteException;

    public int getThemeId() throws RemoteException;
}

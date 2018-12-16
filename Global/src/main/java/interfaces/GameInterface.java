package interfaces;

import classes.Coordinate;
import classes.GameInfo;
import classes.PlayerInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;


public interface GameInterface extends Remote {

    void readyUp(ClientInterface client) throws RemoteException;

    void leaveGame(ClientInterface client) throws RemoteException;

    int getHeight() throws RemoteException;

    int getWidth() throws RemoteException;

    int getValueOf(Coordinate coordinate) throws RemoteException;

    GameInfo getGameInfo() throws RemoteException;

    List<PlayerInfo> getPlayerlist() throws RemoteException;

    HashMap<Coordinate, Integer> getFlippedFields() throws RemoteException;

    int getThemeId() throws RemoteException;
}

package interfaces;

import classes.Coordinate;
import classes.PlayerInfo;
import exceptions.AlreadyPresentException;
import exceptions.InvalidCredentialsException;
import exceptions.LeftGameException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {

    Coordinate requestMove() throws RemoteException, LeftGameException;

    void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException;

    void hideTile(Coordinate c, int delay) throws RemoteException;

    void showTile(Coordinate c, int value) throws RemoteException;

    boolean isSameClient(ClientInterface c) throws RemoteException;

    void setGameStarted() throws RemoteException;

    void updateInfoLabel(String s) throws RemoteException;

    void transferTo(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException, AlreadyPresentException;

    String getUsername() throws RemoteException;

    String getToken() throws RemoteException;

}

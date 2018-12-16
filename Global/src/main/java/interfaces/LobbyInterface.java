package interfaces;

import classes.GameInfo;
import classes.ThemeInfo;
import exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyInterface extends Remote {

    GameInterface makeNewGame(String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id) throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException, ThemeNotLargeEnoughException;

    GameInterface makeNewGame(String id, String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id, boolean backup) throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException, ThemeNotLargeEnoughException;

    GameInterface joinGame(String gameId, ClientInterface newPlayer) throws GameFullException, GameNotFoundException, GameStartedException, RemoteException, InvalidCredentialsException, AlreadyPresentException;

    List<GameInfo> getAllLiveGames() throws RemoteException;

    GameInterface spectateGame(String gameId, ClientInterface client) throws InvalidCredentialsException, RemoteException, GameNotFoundException;

    void terminateGame(GameInterface game) throws RemoteException;

    List<byte[]> getPictures(int id) throws RemoteException;

    String getName() throws RemoteException;

    List<ThemeInfo> getThemes() throws RemoteException;

    byte[] getPicture(int themeId, int pictureIndex) throws RemoteException;
}

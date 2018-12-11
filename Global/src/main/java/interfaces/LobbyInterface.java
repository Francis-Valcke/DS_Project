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

    List<byte[]> getTheme(int id) throws RemoteException;

    GameInterface getGameById(String gameId) throws RemoteException, NoSuchGameExistsException;

    String getName() throws RemoteException;

    List<ThemeInfo> getThemes() throws RemoteException;
}

package interfaces;

import classes.GameInfo;
import exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface LobbyInterface extends Remote {

    GameInterface makeNewGame(String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id) throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException;

    GameInterface joinGame(int gameId, ClientInterface newPlayer) throws GameFullException, GameNotFoundException, GameStartedException, RemoteException, InvalidCredentialsException, AlreadyPresentException;

    ArrayList<GameInfo> getLiveGames() throws RemoteException;

    GameInterface spectateGame(int gameId, ClientInterface client) throws InvalidCredentialsException, RemoteException, GameNotFoundException;

    public List<byte[]> getTheme(int id) throws RemoteException;
}

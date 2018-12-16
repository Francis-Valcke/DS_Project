package interfaces;

import classes.GameInfo;
import classes.PreparedStatementWrapper;
import classes.ThemeInfo;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DatabaseInterface extends Remote {

    void createNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    String createToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    boolean isTokenValid(String username, String token) throws RemoteException;

    List<byte[]> getPictures(int id) throws RemoteException;

    void setMaster(DatabaseInterface master) throws RemoteException;

    void executeSQL(PreparedStatementWrapper pstmt) throws RemoteException;

    void inValidateToken(String username) throws RemoteException;

    List<GameInfo> getAllGames() throws RemoteException;

    List<ThemeInfo> getThemes() throws RemoteException;

    void addGame(GameInfo gi) throws RemoteException;

    void removeGame(GameInfo gi) throws RemoteException;

    ThemeInfo getTheme(int id) throws RemoteException;

    void addPeer(DatabaseInterface slave) throws RemoteException;

    void updateGameInfo(GameInfo gi) throws RemoteException;

    byte[] getPicture(int theme_id, int picture_index) throws RemoteException;

    void ping() throws RemoteException;


}


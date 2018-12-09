package interfaces;

import classes.GameInfo;
import classes.PreparedStatementWrapper;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DatabaseInterface extends Remote {

    public void createNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    public String createToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    public boolean checkCredentials(String username, String password) throws RemoteException;

    public boolean isTokenValid(String username, String token) throws RemoteException;

    public List<byte[]> getPictures(int id) throws RemoteException;

    public void insertPhoto(int id) throws RemoteException;

    public DatabaseInterface getMaster() throws RemoteException;

    public void setMaster(DatabaseInterface master) throws RemoteException;

    void addSlave(DatabaseInterface slave) throws RemoteException;

    void executeSQL(PreparedStatementWrapper pstmt) throws RemoteException;

    void inValidateToken(String username) throws RemoteException;

    List<GameInfo> getAllGames() throws RemoteException;

}

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

    /**
     * Voegt nieuwe gebruiker aan in de databank
     *
     * @param username
     * @param password
     * @throws RemoteException
     * @throws UserAlreadyExistsException wordt gegooid als username al bestaat
     */
    void createNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException;

    /**
     * Maakt een nieuwe token aan die 24u geldig is
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid als het paswoord en/of username niet kloppen
     */
    String createToken(String username, String password) throws RemoteException, InvalidCredentialsException;

    /**
     * Checkt als token valid is
     * @param username
     * @param token
     * @return true als de token valid is
     * @throws RemoteException
     */
    boolean isTokenValid(String username, String token) throws RemoteException;

    /**
     * Vraag alle foto's op van een bepaald thema
     * @param id
     * @return een lijst met bytearrays van alle foto's
     * @throws RemoteException
     */
    List<byte[]> getPictures(int id) throws RemoteException;

    /**
     * Deze methode dient om de master db in te stellen bij de slaves
     * @param master
     * @throws RemoteException
     */
    void setMaster(DatabaseInterface master) throws RemoteException;

    /**
     * Methode om SQL uit te voeren in de vorm van PreparedStatementWrapper
     * @param pstmt PreparedStatementWrapper die moet uitgevoerd worden
     * @throws RemoteException
     */
    void executeSQL(PreparedStatementWrapper pstmt) throws RemoteException;

    /**
     * Invalideert een token van een bepaalde user door de timestamp op 0 te zetten
     * @param username
     * @throws RemoteException
     */
    void inValidateToken(String username) throws RemoteException;

    /**
     * Vraagt een lijst op van alle games in de databank
     * @return een lijst met alle gameInfos
     * @throws RemoteException
     */
    List<GameInfo> getAllGames() throws RemoteException;

    /**
     * Vraag lijst op van alle thema's
     * @return
     * @throws RemoteException
     */
    List<ThemeInfo> getThemes() throws RemoteException;

    /**
     * Voeg game toe aan databank
     * @param gi
     * @throws RemoteException
     */
    void addGame(GameInfo gi) throws RemoteException;

    /**
     * Verwijder game uit databank
     * @param gi
     * @throws RemoteException
     */
    void removeGame(GameInfo gi) throws RemoteException;

    /**
     * Vraag specifiek thema op
     * @param id
     * @return
     * @throws RemoteException
     */
    ThemeInfo getTheme(int id) throws RemoteException;

    /**
     * Voeg een databank toe aan de lijst van peers
     * @param slave
     * @throws RemoteException
     */
    void addPeer(DatabaseInterface slave) throws RemoteException;

    /**
     * Methode op gameinfo in de databank up te daten, bijvoorbeeld als nieuwe speler gejoined is.
     * @param gi
     * @throws RemoteException
     */
    void updateGameInfo(GameInfo gi) throws RemoteException;

    /**
     * Methode om specifieke foto van een thema te verkrijgen
     * @param theme_id
     * @param picture_index
     * @return
     * @throws RemoteException
     */
    byte[] getPicture(int theme_id, int picture_index) throws RemoteException;

    /**
     * Methode om te checken als de database server nog reageert
     * @throws RemoteException
     */
    void ping() throws RemoteException;


}


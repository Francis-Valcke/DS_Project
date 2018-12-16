package interfaces;

import classes.GameInfo;
import classes.ThemeInfo;
import exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LobbyInterface extends Remote {

    /**
     * Maak een nieuwe game aan
     *
     * @param name
     * @param x           breedte
     * @param y           hoogte
     * @param max_players
     * @param firstPlayer Client die de game aanmaakt
     * @param theme_id
     * @return GameInterface van de nieuwe game
     * @throws RemoteException
     * @throws InvalidSizeException         wordt gegooid indien de het aantal velden oneven is
     * @throws InvalidCredentialsException  wordt gegooid als username en/of passwoord niet klopt
     * @throws AlreadyPresentException      wordt gegooid indien client al in de game zit
     * @throws ThemeNotLargeEnoughException wordt gegooid als gekozen thema niet groot genoeg is voor dit spel
     */
    GameInterface makeNewGame(String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id) throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException, ThemeNotLargeEnoughException;

    /**
     * Gelijkaardige methode als hierboven, maar voor backup games
     */
    GameInterface makeNewGame(String id, String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id, boolean backup) throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException, ThemeNotLargeEnoughException;

    /**
     * Methode om een game te joinen
     * @param gameId id van de game
     * @param newPlayer Client die wil joinen
     * @return
     * @throws GameFullException wordt gegooid als het spel volzet is
     * @throws GameNotFoundException wordt gegooid als het spel niet gevonden wordt
     * @throws GameStartedException wordt gegooid als het spel al begonnen is
     * @throws RemoteException
     * @throws InvalidCredentialsException wordt gegooid als username en/of passwoord niet klopt
     * @throws AlreadyPresentException wordt gegooid als de speler al in de game zit
     */
    GameInterface joinGame(String gameId, ClientInterface newPlayer) throws GameFullException, GameNotFoundException, GameStartedException, RemoteException, InvalidCredentialsException, AlreadyPresentException;

    /**
     * @return Lijst van alle live games
     * @throws RemoteException
     */
    List<GameInfo> getAllLiveGames() throws RemoteException;

    /**
     * Methode om een game te spectaten
     * @param gameId id van de game
     * @param client Client die wil spectaten
     * @return
     * @throws InvalidCredentialsException wordt gegooid als username en/of passwoord niet klopt
     * @throws RemoteException
     * @throws GameNotFoundException wordt gegooid als het spel niet gevonden wordt
     */
    GameInterface spectateGame(String gameId, ClientInterface client) throws InvalidCredentialsException, RemoteException, GameNotFoundException;

    /**
     * Methode om een thema in te laden van de database
     * @param id
     * @return
     * @throws RemoteException
     */
    List<byte[]> getPictures(int id) throws RemoteException;

    /**
     * @return naam van de server
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * @return lijst van themas
     * @throws RemoteException
     */
    List<ThemeInfo> getThemes() throws RemoteException;

    /**
     * Methode om individuele foto van een thema te vragen
     * @param themeId
     * @param pictureIndex
     * @return
     * @throws RemoteException
     */
    byte[] getPicture(int themeId, int pictureIndex) throws RemoteException;

    void terminateGame(GameInterface game) throws RemoteException;
}

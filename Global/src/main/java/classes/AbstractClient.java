package classes;

import exceptions.*;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public abstract class AbstractClient extends UnicastRemoteObject implements ClientInterface {

    //Remote Objects
    protected GameInterface game;
    protected DispatcherInterface dispatch;

    protected AppLoginInterface appLogin;
    protected AppLoginInterface backupAppLogin;
    protected LobbyInterface lobby;
    protected LobbyInterface backupLobby;

    //Local objects
    protected GameControllerInterface gameController;
    protected String username;
    protected String token;
    protected Coordinate nextMove;
    protected boolean inGame = false;

    protected AbstractClient() throws RemoteException {
    }

    /*
     * Logic from this class
     * */

    public boolean isDifferentServer(String hostName) throws RemoteException {
        try {
            return !lobby.getName().equals(hostName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void makeGame(String name, int width, int height, int max_players, int theme_id) throws AlreadyPresentException, InvalidSizeException, RemoteException, InvalidCredentialsException, ThemeNotLargeEnoughException {
        game = lobby.makeNewGame(name, width, height, max_players, this, theme_id);
        inGame = true;
    }

    public void joinGame(GameInfo gameInfo) throws AlreadyPresentException, GameFullException, RemoteException, GameStartedException, InvalidCredentialsException, GameNotFoundException, NoSuchGameExistsException {
        if (isDifferentServer(gameInfo.getHostName())) {
            transferTo(gameInfo.getHostName());
        }
        game = lobby.joinGame(gameInfo.getId(), this);
        inGame = true;
    }

    public void spectateGame(GameInfo gameInfo) throws GameNotFoundException, RemoteException, InvalidCredentialsException, AlreadyPresentException {
        if (isDifferentServer(gameInfo.getHostName())) {
            transferTo(gameInfo.getHostName());
        }
        game = lobby.spectateGame(gameInfo.getId(), this);
        inGame = true;
    }

    public void readyUp() throws RemoteException, NotInGameException {
        if (game == null) throw new NotInGameException();
        game.readyUp(this);
    }

    public synchronized void leaveGame() throws RemoteException, NotInGameException {
        if (game == null) throw new NotInGameException();
        //Eventuele thread die vastzit in requestmove losmaken
        inGame = false;
        notifyAll();
        //ApplicationServer laten weten
        game.leaveGame(this);
        game = null;
        //Terug switchen naar de lobby
        gameController = null;
    }

    /*
     * Overrride from Interface
     * */

    @Override
    public synchronized Coordinate requestMove() throws RemoteException, LeftGameException {
        gameController.updateInfoLabel("Flip a card!");
        nextMove = null;
        while (nextMove == null) {
            try {
                wait();
                if (!inGame) {
                    throw new LeftGameException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        gameController.updateInfoLabel("");
        return nextMove;
    }

    @Override
    public void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException {
        try {
            gameController.updatePlayerList(playerInfoList);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void hideTile(Coordinate c, int delay) throws RemoteException {
        gameController.hideTile(c, delay);
    }

    @Override
    public void showTile(Coordinate c, int value) throws RemoteException {
        gameController.showTile(c, value);
    }

    @Override
    public boolean isSameClient(ClientInterface c) throws RemoteException {
        return username.equals(c.getUsername()) && token.equals(c.getToken());
    }

    @Override
    public void setGameStarted() throws RemoteException {
        gameController.startGame();
    }

    @Override
    public void updateInfoLabel(String s) throws RemoteException {
        gameController.updateInfoLabel(s);
    }

    @Override
    public void transferTo(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        disconnect(false);
        connect(server);
    }

    @Override
    public void transferTo(String serverName) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        disconnect(false);
        connect(serverName);
    }

    @Override
    public void disconnect(boolean invalidate) throws RemoteException {
        appLogin.clientLogout(this, invalidate);
        lobby = null;
        backupLobby = null;
        appLogin = null;
        backupAppLogin = null;
    }

    @Override
    //Let the dispatcher choose which server
    public void connect() throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        ApplicationServerInterface server = dispatch.getApplicationServer();
        appLogin = server.getAppLogin();
        backupAppLogin = server.getBackupServer().getAppLogin();
        lobby = appLogin.clientLogin(username, token);
        dispatch.addUser(server, username);
    }

    @Override
    //Ask the dispatcher for a specific server
    public void connect(String serverName) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        ApplicationServerInterface server = dispatch.getApplicationServerByName(serverName);
        appLogin = server.getAppLogin();
        backupAppLogin = server.getBackupServer().getAppLogin();

        lobby = appLogin.clientLogin(username, token);

        dispatch.addUser(server, username);
    }

    @Override
    public void connect(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException, AlreadyPresentException {
        appLogin = server.getAppLogin();
        backupAppLogin = server.getBackupServer().getAppLogin();

        lobby = appLogin.clientLogin(username, token);

        dispatch.addUser(server, username);
    }

    /*
     * Implemented Getters & Setters
     * */

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public String getToken() throws RemoteException {
        return token;
    }

    @Override
    public void setGameController(GameControllerInterface gameController) throws RemoteException {
        this.gameController = gameController;
    }

    @Override
    public GameControllerInterface getGameController() throws RemoteException {
        return gameController;
    }

    @Override
    public AppLoginInterface getAppLogin() {
        return appLogin;
    }

    @Override
    public void setAppLogin(AppLoginInterface appLogin) {
        this.appLogin = appLogin;
    }

    /*
     * normal Getters & Setters
     * */

    public GameInterface getGame() {
        return game;
    }

    public void setGame(GameInterface game) {
        this.game = game;
    }

    public DispatcherInterface getDispatch() {
        return dispatch;
    }

    public void setDispatch(DispatcherInterface dispatch) {
        this.dispatch = dispatch;
    }

    public LobbyInterface getLobby() {
        return lobby;
    }

    public void setLobby(LobbyInterface lobby) {
        this.lobby = lobby;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Coordinate getNextMove() {
        return nextMove;
    }

    public synchronized void setNextMove(Coordinate nextMove) {
        this.nextMove = nextMove;
        notifyAll();
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public LobbyInterface getBackupLobby() {
        return backupLobby;
    }

    public void setBackupLobby(LobbyInterface backupLobby) {
        this.backupLobby = backupLobby;
    }

    /*
     * Special methods
     * */

}

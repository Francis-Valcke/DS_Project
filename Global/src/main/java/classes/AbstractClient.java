package classes;

import exceptions.*;
import interfaces.*;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Objects;

public abstract class AbstractClient extends UnicastRemoteObject implements ClientInterface {

    //Remote Objects
    protected ApplicationServerInterface applicationServer;
    protected ApplicationServerInterface backupApplicationServer;
    protected GameInterface game;
    protected DispatcherInterface dispatch;
    protected AppLoginInterface app_login;
    protected LobbyInterface lobby;

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

    public boolean isDiffrentServer(String name) throws RemoteException {
        return !applicationServer.getName().equals(name);
    }

    public void makeGame(String name, int width, int height, int max_players, int theme_id) throws AlreadyPresentException, InvalidSizeException, RemoteException, InvalidCredentialsException {
        game = lobby.makeNewGame(name, width, height, max_players, this, theme_id);
        inGame = true;
    }

    public void joinGame(String gameId) throws AlreadyPresentException, GameFullException, RemoteException, GameStartedException, InvalidCredentialsException, GameNotFoundException {
        game = lobby.joinGame(gameId, this);
        inGame = true;
    }

    public void spectateGame(String gameId) throws GameNotFoundException, RemoteException, InvalidCredentialsException {
        game = lobby.spectateGame(gameId, this);
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
    public void disconnect() throws RemoteException {
        //Disconnect from the current server
        applicationServer.disconnect(this);
        applicationServer = null;
        backupApplicationServer = null;
        lobby = null;
    }

    @Override
    public void transferTo(String serverName) throws RemoteException {
        ApplicationServerInterface newAppServer = dispatch.getApplicationServerByName(serverName);
        newAppServer.transferClient(this);
    }

    @Override
    public void connect(ApplicationServerInterface server) throws RemoteException {
        applicationServer = server;
        backupApplicationServer = server.getBackupServer();
        server.addConnectedClient(this);
        lobby = server.getLobby();
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
    public ApplicationServerInterface getApplicationServer() throws RemoteException {
        return applicationServer;
    }

    @Override
    public void setApplicationServer(ApplicationServerInterface appServer) throws RemoteException {
        applicationServer = appServer;
    }

    @Override
    public ApplicationServerInterface getBackupApplicationServer() throws RemoteException {
        return backupApplicationServer;
    }

    @Override
    public void setBackupApplicationServer(ApplicationServerInterface appServer) throws RemoteException {
        backupApplicationServer = appServer;
    }

    @Override
    public void setGameController(GameControllerInterface gameController) throws RemoteException {
        this.gameController = gameController;
    }

    @Override
    public GameControllerInterface getGameController() throws RemoteException {
        return gameController;
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

    public AppLoginInterface getApp_login() {
        return app_login;
    }

    public void setApp_login(AppLoginInterface app_login) {
        this.app_login = app_login;
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

    /*
     * Special methods
     * */

}

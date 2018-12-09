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
    protected GameInterface game;
    protected DispatcherInterface dispatch;
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
        return !lobby.getApplicationServer().getName().equals(hostName);
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
    public void transferTo(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException {
        disconnect();
        connect(server);
    }

    @Override
    public void transferTo(String serverName) throws RemoteException, InvalidCredentialsException {
        disconnect();
        connect(serverName);
    }

    @Override
    public void disconnect() throws RemoteException {
        lobby.getApplicationServer().disconnect(this);
        backupLobby.getApplicationServer().disconnect(this);
    }

    @Override
    //Let the dispatcher choose which server
    public void connect() throws RemoteException, InvalidCredentialsException {
        ApplicationServerInterface server = dispatch.getApplicationServer();
        server.addConnectedClient(this);
        lobby = server.getAppLogin().clientLogin(username, token);
        backupLobby = server.getBackupServer().getAppLogin().clientLogin(username, token);
    }

    @Override
    //Ask the dispatcher for a specific server
    public void connect(String serverName) throws RemoteException, InvalidCredentialsException {
        ApplicationServerInterface server = dispatch.getApplicationServerByName(serverName);
        server.addConnectedClient(this);
        lobby = server.getAppLogin().clientLogin(username, token);
        backupLobby = server.getBackupServer().getAppLogin().clientLogin(username, token);
    }

    @Override
    public void connect(ApplicationServerInterface server) throws RemoteException, InvalidCredentialsException {
        server.addConnectedClient(this);
        lobby = server.getAppLogin().clientLogin(username, token);
        backupLobby = server.getBackupServer().getAppLogin().clientLogin(username, token);
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

package com.kuleuven.distributedsystems.applicationserver.rest;

import classes.Coordinate;
import classes.GameInfo;
import classes.PlayerInfo;
import com.kuleuven.distributedsystems.applicationserver.AppLogin;
import com.kuleuven.distributedsystems.applicationserver.Game;
import com.kuleuven.distributedsystems.applicationserver.Lobby;
import exceptions.*;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class VirtualClient extends UnicastRemoteObject implements ClientInterface {

    private GameInterface game;
    private DispatcherInterface dispatch;
    private AppLoginInterface app_login;
    private Lobby lobby;

    private VirtualGameController gameController;
    private String username;
    private String token;
    private Coordinate nextMove;
    private List<Coordinate> moves = new LinkedList<>();
    private boolean inGame = false;

    public VirtualClient(String username, String token) throws RemoteException {
        this.username = username;
        this.token = token;
        app_login = AppLogin.getInstance();
        lobby = Lobby.getInstance();
        gameController = new VirtualGameController();
    }

    public GameInfo makeGame(String name, int width, int height, int max_players) throws InvalidSizeException, RemoteException, InvalidCredentialsException, AlreadyPresentException {
        //TODO: themes implementeren nu wordt standaard theme 0 geladen
        game = lobby.makeNewGame(name, width, height, max_players, this, 0);
        inGame = true;
        gameController = new VirtualGameController(this, height, width, false);
        return game.getGameInfo();
    }

    public GameInfo joinGame(int gameId) throws GameFullException, RemoteException, GameStartedException, InvalidCredentialsException, GameNotFoundException, NoSuchGameExistsException, AlreadyPresentException {
        game = lobby.getGame(gameId);
        gameController = new VirtualGameController(this, game.getHeight(), game.getWidth(), false);
        lobby.joinGame(gameId, this);
        inGame = true;
        return game.getGameInfo();
    }

    public void spectateGame(int gameId) throws GameNotFoundException, RemoteException, InvalidCredentialsException, NoSuchGameExistsException {
        game = lobby.getGame(gameId);
        gameController = new VirtualGameController(this, game.getHeight(), game.getWidth(), true);
        lobby.spectateGame(gameId, this);
        inGame = true;
        HashMap<Coordinate, Integer> flippedFields = game.getFlippedFields();
        for (Coordinate c : flippedFields.keySet()) {
            gameController.showTile(c, flippedFields.get(c));
        }
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

    public synchronized Object recordMove(Coordinate c) throws RemoteException, InvalidMoveException, NotYourTurnException {
        if (!gameController.isYourTurn()) throw new NotYourTurnException();
        if (!isMoveValid(c)) throw new InvalidMoveException();
        nextMove = c;
        moves.add(c);
        notifyAll();
        return ((Game) game).getBoard().get(c).getValue();
    }

    private boolean isMoveValid(Coordinate c) throws RemoteException {
        int x = game.getWidth();
        int y = game.getHeight();
        if (c.getX() < 0 || c.getX() >= x) return false;
        if (c.getY() < 0 || c.getY() >= y) return false;
        if (moves.contains(c)) return false;
        return true;
    }

    @Override
    public synchronized Coordinate requestMove() throws RemoteException, LeftGameException {
        /*
         * We moeten 2 moves binnen krijgen van de mobile client van wie het de beurt is.
         * */
        gameController.yourTurn(true);
        nextMove = null;
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
        gameController.yourTurn(false);
        gameController.updateInfoLabel("");

        if (moves.size() % 2 == 0)
            moves.clear();

        return nextMove;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public String getToken() throws RemoteException {
        return token;
    }

    @Override
    public boolean isSameClient(ClientInterface c) throws RemoteException {
        if (username.equals(c.getUsername()) && token.equals(c.getToken())) return true;
        return false;
    }

    /*
     * Actions that need to go into the action queue
     * */

    @Override
    public void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException {
        gameController.updatePlayerList(playerInfoList);
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
    public void setGameStarted() throws RemoteException {
        gameController.startGame();
    }

    @Override
    public void updateInfoLabel(String s) throws RemoteException {
        gameController.updateInfoLabel(s);
    }

    /*
     * Getters & Setters
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
        this.lobby = (Lobby) lobby;
    }

    public VirtualGameController getGameController() {
        return gameController;
    }

    public void setGameController(VirtualGameController gameController) {
        this.gameController = gameController;
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

    public void setNextMove(Coordinate nextMove) {
        this.nextMove = nextMove;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }
}

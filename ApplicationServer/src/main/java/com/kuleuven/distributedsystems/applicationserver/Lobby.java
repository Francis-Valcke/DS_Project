package com.kuleuven.distributedsystems.applicationserver;

import classes.GameInfo;
import exceptions.*;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lobby extends UnicastRemoteObject implements LobbyInterface {

    private static Lobby instance;
    private static int idCounter = 0;

    static {
        try {
            instance = new Lobby();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, Game> live_games = new HashMap<>();
    private DatabaseInterface db;
    private DispatcherInterface dispatch;

    private Lobby() throws RemoteException {
    }

    private static synchronized int createID() {
        return idCounter++;
    }

    public static Lobby getInstance() {
        return instance;
    }

    public Lobby init(DatabaseInterface db, DispatcherInterface dispatch) {
        this.db = db;
        this.dispatch = dispatch;
        return this;
    }

    public GameInterface makeNewGame(String name, int x, int y, int max_players, ClientInterface firstPlayer, int theme_id)
            throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException {
        //validate username and token in gameclientinterface
        if (!isValidPlayer(firstPlayer)) {
            throw new InvalidCredentialsException();
        }

        int id = createID();
        Game new_game = new Game(name, x, y, max_players, id, this, theme_id);
        new_game.addPlayer(firstPlayer);
        live_games.put(id, new_game);
        System.out.println("INFO: new game initialised [id:" + id + "]");
        return new_game;
    }

    //TODO: add spectator

    public synchronized GameInterface joinGame(int gameId, ClientInterface client)
            throws GameFullException, GameStartedException, RemoteException, InvalidCredentialsException, GameNotFoundException, AlreadyPresentException {
        //validate username and token in gameclientinterface
        if (!isValidPlayer(client)) {
            throw new InvalidCredentialsException();
        }

        Game game = live_games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException();
        }
        if (game.isStarted()) {
            throw new GameStartedException();
        }
        if (game.getMax_players() > game.getPlayerQueue().size()) {
            game.addPlayer(client);
            return game;
        } else throw new GameFullException();
    }

    public GameInterface spectateGame(int gameId, ClientInterface client) throws InvalidCredentialsException, RemoteException, GameNotFoundException {
        if (!isValidPlayer(client)) {
            throw new InvalidCredentialsException();
        }

        Game game = live_games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException();
        }
        game.addSpectator(client);
        return game;
    }

    public ArrayList<GameInfo> getLiveGames() throws RemoteException {
        ArrayList<GameInfo> liveGames = new ArrayList<>();
        for (Game g : live_games.values()) {
            liveGames.add(g.getGameInfo());
        }
        return liveGames;
    }

    public void terminateGame(Game game) {
        live_games.remove(game.getId());
        System.out.println("INFO: game [id:" + game.getId() + "] was finished");
    }

    public List<byte[]> getTheme(int id) throws RemoteException {
        return db.getTheme(id);
    }

    public boolean isValidPlayer(ClientInterface client) {
        try {
            return dispatch.isTokenValid(client.getUsername(), client.getToken());
        } catch (RemoteException re) {
            re.printStackTrace();
        }
        return false;

    }

    public Game getGame(int gameId) throws NoSuchGameExistsException {
        if (!live_games.containsKey(gameId)) throw new NoSuchGameExistsException();
        return live_games.get(gameId);
    }
}

package com.kuleuven.distributedsystems.applicationserver;

import classes.GameInfo;
import exceptions.*;
import interfaces.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

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

    private Map<String, Game> liveGames = new HashMap<>();
    private Map<String, Game> backupLiveGames = new HashMap<>();

    private ApplicationServerInterface applicationServer;
    private DatabaseInterface db;
    private DispatcherInterface dispatch;

    private Lobby() throws RemoteException {
    }

    private String createID() throws RemoteException{
        return applicationServer.getName() + idCounter++;
    }

    public static Lobby getInstance() {
        return instance;
    }

    public Lobby init(ApplicationServerInterface applicationServer, DatabaseInterface db, DispatcherInterface dispatch) {
        this.applicationServer = applicationServer;
        this.db = db;
        this.dispatch = dispatch;
        return this;
    }

    public GameInterface makeNewGame(String name, int x, int y, int max_players, ClientInterface client, int theme_id)
            throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException {

        return makeNewGame(createID(), name, x, y, max_players, client, theme_id, false);
    }

    public GameInterface makeNewGame(String id, String name, int x, int y, int max_players, ClientInterface client, int theme_id, boolean backup)
            throws RemoteException, InvalidSizeException, InvalidCredentialsException, AlreadyPresentException {

        if (!isValidPlayer(client)) {
            throw new InvalidCredentialsException();
        }

        GameInterface newGameInterface = null;
        Game newGame = null;
        /*
        * Bij het aanmaken van een new game moet eerst gechecked worden of de gevraagde game bij de server kan.
        * Zo niet wordt een nieuwe app server opgestart
        * */
        if (!applicationServer.canFit(max_players) && !backup){
            //Vraag een appserver op die wel plaats heeft voor de game.
            //De dispatcher start als nodig een nieuwe appserver op.
            ApplicationServerInterface newAppServer = dispatch.getApplicationServerByFreeSlots(max_players);
            newAppServer.transferClient(client);
            newGameInterface = newAppServer.getLobby().makeNewGame(name, x, y, max_players, client, theme_id);
        } else {
            //validate username and token in gameclientinterface


            newGame = new Game(name, x, y, max_players, id , client, this, theme_id, backup);
            newGame.addPlayer(client);

            if (!backup) {
                liveGames.put(id, newGame);
                System.out.println("INFO: new game initialised [id:" + id + "]");
                ((ApplicationServer) applicationServer).reduceFreeSlots(max_players);
                dispatch.broadCastLobby(this);
            } else {
                backupLiveGames.put(id, newGame);
            }
        }

        System.out.println();
        System.out.println("Current games:");
        System.out.println("\tLive Games:");
        for (Map.Entry<String, Game> entry : liveGames.entrySet()) {
            System.out.println("\t\t" + entry.getValue());
        }
        System.out.println("\tBackup Games:");
        for (Map.Entry<String, Game> entry : backupLiveGames.entrySet()) {
            System.out.println("\t\t" + entry.getValue());
        }
        System.out.println();

        if (newGameInterface!=null) return newGameInterface;
        else return newGame;
    }

    //TODO: add spectator

    public synchronized GameInterface joinGame(String gameId, ClientInterface client)
            throws GameFullException, GameStartedException, RemoteException, InvalidCredentialsException, GameNotFoundException, AlreadyPresentException {
        //validate username and token in gameclientinterface
        if (!isValidPlayer(client)) {
            throw new InvalidCredentialsException();
        }

        Game game = liveGames.get(gameId);
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

    public GameInterface spectateGame(String gameId, ClientInterface client) throws InvalidCredentialsException, RemoteException, GameNotFoundException {
        if (!isValidPlayer(client)) {
            throw new InvalidCredentialsException();
        }

        Game game = liveGames.get(gameId);
        if (game == null) {
            throw new GameNotFoundException();
        }
        game.addSpectator(client);
        return game;
    }

    public ArrayList<GameInfo> getLiveGames() throws RemoteException {
        ArrayList<GameInfo> liveGames = new ArrayList<>();
        for (Game g : this.liveGames.values()) {
            liveGames.add(g.getGameInfo());
        }
        return liveGames;
    }

    public void terminateGame(Game game) throws RemoteException {
        liveGames.remove(game.getId());
        dispatch.broadCastLobby(this);
        ((ApplicationServer)applicationServer).addFreeSlots(game.getMax_players());
        System.out.println("INFO: game [id:" + game.getId() + "] was finished");
    }

    public List<byte[]> getTheme(int id) throws RemoteException {
        return db.getTheme(id);
    }

    @Override
    public ApplicationServerInterface getApplicationServer() throws RemoteException {
        return applicationServer;
    }

    public boolean isValidPlayer(ClientInterface client) {
        try {
            return dispatch.isTokenValid(client.getUsername(), client.getToken());
        } catch (RemoteException re) {
            re.printStackTrace();
        }
        return false;

    }

    public Game getGame(String gameId) throws NoSuchGameExistsException {
        if (!liveGames.containsKey(gameId)) throw new NoSuchGameExistsException();
        return liveGames.get(gameId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lobby)) return false;
        if (!super.equals(o)) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(applicationServer, lobby.applicationServer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), applicationServer);
    }
}

package logic;

import classes.Coordinate;
import classes.GameInfo;
import classes.PlayerInfo;
import exceptions.*;
import interfaces.*;
import ui.AlertBox;
import ui.Tile;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface {
    //Singleton
    private static Client instance;
    static {
        try {
            instance = new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Remote Objects
    private ApplicationServerInterface applicationServer;
    private GameInterface game;
    private DispatcherInterface dispatch;
    private AppLoginInterface app_login;
    private LobbyInterface lobby;

    //Local objects
    private String username;
    private String token;
    private Coordinate nextMove;
    private GameController gameController;
    private boolean inGame = false;


    private Client() throws RemoteException{}

    public synchronized Coordinate requestMove() throws RemoteException, LeftGameException {
        //System.out.println("Move requested");
        Tile.setYourTurn(true);
        nextMove = null;
        gameController.updateInfoLabel("Flip a card!");
        //Zolang nextMove niet gegeven is moet de thread wachten;
        while(nextMove == null){
            try {
                wait();
                if(!inGame){
                    throw new LeftGameException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("move ontvangen");
        Tile.setYourTurn(false);
        gameController.updateInfoLabel("");
        return nextMove;
    }


    public void makeGame(String name, int width, int height, int max_players, int theme_id) throws AlreadyPresentException {
        try {
            game = lobby.makeNewGame(name, width, height, max_players, this, theme_id);

            inGame = true;
            gameController = new GameController(height, width, false, loadTheme(theme_id));
            SceneController.getInstance().createGameScene(gameController);

        } catch (InvalidSizeException e) {
            AlertBox.display("Cannot make game","Number of fields must be even");

        } catch (InvalidCredentialsException e) {
            AlertBox.display("Cannot make game","Wrong credentials");
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public void joinGame(GameInfo selected) throws AlreadyPresentException {
        try {
            game = lobby.joinGame(selected.getId(), this);
            inGame = true;
            gameController = new GameController(game.getHeight(), game.getWidth(), false, loadTheme(game.getThemeId()));
            SceneController.getInstance().createGameScene(gameController);

        } catch (GameFullException | GameNotFoundException | GameStartedException | InvalidCredentialsException e) {
            AlertBox.display("Cannot Join", e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void spectateGame(int gameId) {
        try {
            game = lobby.spectateGame(gameId, this);
            gameController = new GameController(game.getHeight(), game.getWidth(), true, loadTheme(game.getThemeId()));
            SceneController.getInstance().createGameScene(gameController);
            HashMap<Coordinate, Integer> flippedFields = game.getFlippedFields();
            for(Coordinate c: flippedFields.keySet()){
                gameController.showTile(c, flippedFields.get(c));
            }

        } catch (InvalidCredentialsException | GameNotFoundException e) {
            AlertBox.display("Cannot Join", e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Theme loadTheme(int theme_id){
        System.out.println(System.getProperty("user.dir"));
        File themeDirectory = new File("Client/src/main/resources/themes/"+theme_id);
        if(themeDirectory.isDirectory()){
            return new Theme(theme_id);
        }
        else{
            try {
                List<byte[]> images = lobby.getTheme(theme_id);
                Theme.saveNewTheme(theme_id, images);
                return new Theme(theme_id);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void readyUp(){
        try{
            game.readyUp(this);
            //System.out.println("ik ben klaar");
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
    }

    public synchronized void leaveGame(){
        try {
            //Eventuele thread die vastzit in requestmove losmaken
            inGame = false;
            notifyAll();
            //ApplicationServer laten weten
            game.leaveGame(this);
            //Terug switchen naar de lobby
            gameController = null;
            SceneController.getInstance().showLobbyScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTile(Coordinate c, int value) throws RemoteException{
        gameController.showTile(c, value);
    }
    public void hideTile(Coordinate c, int delay) throws RemoteException{
        gameController.hideTile(c, delay);
    }


    public void updatePlayerInfo(List<PlayerInfo> playerInfoList) throws RemoteException{
        try {
            gameController.updatePlayerList(playerInfoList);
        }catch (NullPointerException ne){
            //Het kan zijn dat de gamecontroller nog niet is aangemaakt op het moment van de 1ste update
        }
    }

    public void updateInfoLabel(String s) throws RemoteException{
        gameController.updateInfoLabel(s);
    }

    public void setGameStarted() throws RemoteException{
        gameController.startGame();
    }

    public synchronized void setNextMove(Coordinate nextMove) {
        this.nextMove = nextMove;
        notifyAll();
    }

    public GameInterface getGame() {
        return game;
    }

    public ApplicationServerInterface getApplicationServer() {
        return applicationServer;
    }

    public void setApplicationServer(ApplicationServerInterface applicationServer) {
        this.applicationServer = applicationServer;
    }

    public void setGame(GameInterface game) {
        this.game = game;
    }

    public String getUsername() throws RemoteException{
        return username;
    }

    public String getToken() throws RemoteException{
        return token;
    }

    public boolean isSameClient(ClientInterface c) throws RemoteException{
        if(username.equals(c.getUsername()) && token.equals(c.getToken())) return true;
        return false;
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

    public static Client getInstance() {
        return instance;
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

    public GameController getGameController() {
        return gameController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}

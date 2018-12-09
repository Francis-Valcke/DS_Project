package logic;

import classes.AbstractClient;
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

public class Client extends AbstractClient {
    //Singleton
    private static Client instance;

    static {
        try {
            instance = new Client();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Local objects
    private LobbyController lobbyController;


    private Client() throws RemoteException {
    }

    public static Client getInstance() {
        return instance;
    }

    @Override
    public synchronized Coordinate requestMove() throws RemoteException, LeftGameException {
        //System.out.println("Move requested");
        Tile.setYourTurn(true);
        nextMove = super.requestMove();
        Tile.setYourTurn(false);
        return nextMove;
    }

    @Override
    public void makeGame(String name, int width, int height, int maxPlayers, int themeId) throws AlreadyPresentException {
        try {
            super.makeGame(name, width, height, maxPlayers, themeId);
            gameController = new GameController(height, width, false, loadTheme(themeId));
            SceneController.getInstance().createGameScene(gameController);

        } catch (InvalidSizeException e) {
            AlertBox.display("Cannot make game", "Number of fields must be even");

        } catch (InvalidCredentialsException e) {
            AlertBox.display("Cannot make game", "Wrong credentials");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinGame(String gameId) throws AlreadyPresentException {
        try {
            super.joinGame(gameId);
            gameController = new GameController(game.getHeight(), game.getWidth(), false, loadTheme(game.getThemeId()));
            SceneController.getInstance().createGameScene(gameController);

        } catch (GameFullException | GameNotFoundException | GameStartedException | InvalidCredentialsException e) {
            AlertBox.display("Cannot Join", e.getMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void spectateGame(String gameId) {
        try {
            game = lobby.spectateGame(gameId, this);
            gameController = new GameController(game.getHeight(), game.getWidth(), true, loadTheme(game.getThemeId()));
            SceneController.getInstance().createGameScene(gameController);
            HashMap<Coordinate, Integer> flippedFields = game.getFlippedFields();
            for (Coordinate c : flippedFields.keySet()) {
                gameController.showTile(c, flippedFields.get(c));
            }

        } catch (InvalidCredentialsException | GameNotFoundException e) {
            AlertBox.display("Cannot Join", e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void leaveGame() {
        try {
            super.leaveGame();
            SceneController.getInstance().showLobbyScene();
        } catch (IOException | NotInGameException e) {
            e.printStackTrace();
        }
    }

    public Theme loadTheme(int theme_id) {
        System.out.println(System.getProperty("user.dir"));
        File themeDirectory = new File("Client/src/main/resources/themes/" + theme_id);
        if (themeDirectory.isDirectory()) {
            return new Theme(theme_id);
        } else {
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


    public LobbyController getLobbyController() {
        return lobbyController;
    }

    public void setLobbyController(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
    }
}

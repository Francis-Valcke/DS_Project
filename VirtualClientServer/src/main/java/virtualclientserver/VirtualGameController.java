package virtualclientserver;

import classes.Coordinate;
import classes.PlayerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import interfaces.GameControllerInterface;
import virtualclientserver.actions.Action;
import virtualclientserver.actions.*;

import java.util.ArrayList;
import java.util.List;

/*
 * This method will virtually keep the game state for the mobile clients
 * */
public class VirtualGameController implements GameControllerInterface {

    private ArrayList<Action> actions;
    private VirtualClient client;

    private boolean gameStarted = false;
    private boolean spectatorMode;
    private boolean yourTurn;
    private int height, width;

    public VirtualGameController(VirtualClient client, int height, int width, boolean spectating) {
        this.client = client;
        this.height = height;
        this.width = width;
        this.spectatorMode = spectating;
        actions = new ArrayList<>();
        actions.add(new InitialiseAction(width, height, spectating));
    }

    public VirtualGameController() {
        actions = new ArrayList<>();
    }

    public void showTile(Coordinate c, int integer) {
        actions.add(new ShowTileAction(c, integer));
    }

    public void hideTile(Coordinate c, int delay) {
        actions.add(new HideTileAction(c, delay));
    }

    public void updatePlayerList(List<PlayerInfo> playerInfoList) {
        actions.add(new UpdatePlayerInfoAction(playerInfoList));
    }

    public void updateInfoLabel(String s) {
        actions.add(new UpdateInfoLabelAction(s));
    }

    public void startGame() {
        actions.add(new GameStartAction());
    }

    public void yourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
        actions.add(new YourTurnAction(yourTurn));
    }

    public List<Action> getActions(int from) {
        return actions.subList(from, actions.size());
    }

    /*
     * Getters & Setters
     * */

    public boolean isYourTurn() {
        return yourTurn;
    }

}

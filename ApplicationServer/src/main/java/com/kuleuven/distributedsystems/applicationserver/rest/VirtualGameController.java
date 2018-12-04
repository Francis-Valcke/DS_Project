package com.kuleuven.distributedsystems.applicationserver.rest;

import com.kuleuven.distributedsystems.applicationserver.rest.actions.*;
import classes.Coordinate;
import classes.PlayerInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/*
 * This method will virtually keep the game state for the mobile clients
 * */
public class VirtualGameController {

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

    public void init(VirtualClient client, int height, int width, boolean spectating) {
        this.client = client;
        this.height = height;
        this.width = width;
        this.spectatorMode = spectating;
        actions.add(new InitialiseAction(width, height, spectating));
    }

    public void showTile(Coordinate c, Integer integer) {
        actions.add(new ShowTileAction(c, integer));
    }

    public void hideTile(Coordinate c, Integer delay) {
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

    public String getActionsJson(int from) throws JsonProcessingException {
        List<Action> toSend = actions.subList(from, actions.size());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(toSend);
        return json;
    }

    public List<Action> getActions(int from) throws JsonProcessingException {
        return actions.subList(from, actions.size());
    }

    /*
     * Getters & Setters
     * */

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public VirtualClient getClient() {
        return client;
    }

    public void setClient(VirtualClient client) {
        this.client = client;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isSpectatorMode() {
        return spectatorMode;
    }

    public void setSpectatorMode(boolean spectatorMode) {
        this.spectatorMode = spectatorMode;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

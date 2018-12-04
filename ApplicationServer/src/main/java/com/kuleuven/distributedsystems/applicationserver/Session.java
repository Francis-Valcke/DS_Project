package com.kuleuven.distributedsystems.applicationserver;

import interfaces.ClientInterface;
import interfaces.GameInterface;

public class Session {

    private String token;
    private ClientInterface client;
    private GameInterface game;

    public Session(String token, ClientInterface client, GameInterface game) {
        this.token = token;
        this.client = client;
        this.game = game;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ClientInterface getClient() {
        return client;
    }

    public void setClient(ClientInterface client) {
        this.client = client;
    }

    public GameInterface getGame() {
        return game;
    }

    public void setGame(GameInterface game) {
        this.game = game;
    }
}

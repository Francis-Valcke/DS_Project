package com.kuleuven.distributedsystems.applicationserver;

import exceptions.InvalidCredentialsException;
import interfaces.AppLoginInterface;
import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;
import interfaces.LobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AppLogin extends UnicastRemoteObject implements AppLoginInterface {

    private static AppLogin instance;

    static {
        try {
            instance = new AppLogin();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private LobbyInterface lobby;
    private DatabaseInterface db;
    private DispatcherInterface dispatch;

    private AppLogin() throws RemoteException {
    }

    public AppLogin init(Lobby lobby, DatabaseInterface db, DispatcherInterface dispatch) {
        this.lobby = lobby;
        this.db = db;
        this.dispatch = dispatch;
        return this;
    }

    public LobbyInterface clientLogin(String username, String token) throws RemoteException, InvalidCredentialsException {
        if(db.isTokenValid(username, token)){
            return lobby;
        }
        throw new InvalidCredentialsException("Wrong credentials.");
    }

    public LobbyInterface getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public DatabaseInterface getDb() {
        return db;
    }

    public void setDb(DatabaseInterface db) {
        this.db = db;
    }

    public DispatcherInterface getDispatch() {
        return dispatch;
    }

    public void setDispatch(DispatcherInterface dispatch) {
        this.dispatch = dispatch;
    }

    @Override
    public void setLobby(LobbyInterface lobbyInterface) {
        this.lobby = lobbyInterface;
    }

    public static AppLogin getInstance() {
        return instance;
    }
}

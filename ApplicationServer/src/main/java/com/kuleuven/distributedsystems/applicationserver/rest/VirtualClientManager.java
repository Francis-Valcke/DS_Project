package com.kuleuven.distributedsystems.applicationserver.rest;


import exceptions.AlreadyPresentException;
import exceptions.UserNotLoggedInException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/*
 * Singleton class to handle virtual android clients
 * */
public class VirtualClientManager {
    //Singleton
    private static VirtualClientManager instance = new VirtualClientManager();

    private Map<String, VirtualClient> clients = new HashMap<>();

    private VirtualClientManager() {
    }

    public static VirtualClientManager getInstance() {
        return instance;
    }

    public VirtualClient removeClient(String token) throws UserNotLoggedInException {
        VirtualClient client = getClient(token);
        clients.remove(token);
        return client;
    }

    public VirtualClient newClient(String username, String token) throws AlreadyPresentException, RemoteException {
        if (isLoggedIn(token)) throw new AlreadyPresentException();
        VirtualClient client = new VirtualClient(username, token);
        clients.put(token, client);
        return client;
    }

    public VirtualClient getClient(String token) throws UserNotLoggedInException {
        if (!isLoggedIn(token)) {
            throw new UserNotLoggedInException();
        }
        return clients.get(token);
    }

    public boolean isLoggedIn(String token) {
        return clients.containsKey(token);
    }
}

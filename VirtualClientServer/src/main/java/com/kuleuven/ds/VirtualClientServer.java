package com.kuleuven.ds;

import exceptions.AlreadyPresentException;
import exceptions.UserNotLoggedInException;
import interfaces.DispatcherInterface;
import interfaces.VirtualClientServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class VirtualClientServer extends UnicastRemoteObject implements VirtualClientServerInterface {

    /*
     * Instance
     * */
    private static VirtualClientServer instance;

    static {
        try {
            instance = new VirtualClientServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static VirtualClientServer getInstance() {
        return instance;
    }

    /*
     * Attributes
     * */

    //Remote objects
    DispatcherInterface dispatcher;

    //Local objects
    private String name;
    private String address;
    private String port;
    private Map<String, VirtualClient> clients = new HashMap<>();

    /*
     * Constructors
     * */
    protected VirtualClientServer() throws RemoteException {
    }


    /*
     * Logic
     * */

    public void init(String serverName, String address, String port) {
        this.name = serverName;
        this.address = address;
        this.port = port;
        this.clients = new HashMap<>();

        try {
            //First get the dispatcher reference
            Registry registry = LocateRegistry.getRegistry(Main.DISPATCH_IP, Main.DISPATCH_PORT);
            dispatcher = (DispatcherInterface) registry.lookup("dispatcher_service");
            //Then register this server with the dispatcher
            dispatcher.registerVirtualClientServer(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Successfully registered with dispatcher.");
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

    /*
     * Getters & Setters
     * */

    public DispatcherInterface getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(DispatcherInterface dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

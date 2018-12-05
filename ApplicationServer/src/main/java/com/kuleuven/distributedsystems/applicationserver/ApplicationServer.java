package com.kuleuven.distributedsystems.applicationserver;

import interfaces.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ApplicationServer extends UnicastRemoteObject implements ApplicationServerInterface {

    private static final int SERVER_CAPACITY = 2;
    private static ApplicationServer instance;

    static {
        try {
            instance = new ApplicationServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private String ip;
    private int port;
    private int restPort;
    private int dispatcherPort;
    private String dispatcherIp;
    private DatabaseInterface db;
    private AppLoginInterface appLogin;
    private LobbyInterface lobby;
    private ApplicationServerInterface backupServer;

    private List<ClientInterface> connectedClients;

    private ApplicationServer() throws RemoteException {
    }

    public static ApplicationServer getInstance() {
        return instance;
    }

    public void init(String name, String ip, int port, int restPort, String dispatcherIp, int dispatcherPort) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.restPort = restPort;
        this.dispatcherPort = dispatcherPort;
        this.dispatcherIp = dispatcherIp;
        connectedClients = new ArrayList<>();

        startLogin();
        System.out.println("INFO: up and running on port: " + port);
        registerWithDispatcher();
        System.out.println("INFO: connection made to database and dispatch");
    }

    @Override
    public void startLogin() {
        try {
            appLogin = AppLogin.getInstance();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("login_service", appLogin);
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    @Override
    public void registerWithDispatcher() {
        try {
            Registry registry = LocateRegistry.getRegistry(dispatcherIp, dispatcherPort);
            DispatcherInterface dispatcherImpl = (DispatcherInterface) registry.lookup("dispatcher_service");
            //We krijgen een link naar de databank terug die deze server zal gebruiken
            db = dispatcherImpl.registerApplicationServer((ApplicationServerInterface) this);

            //We maken de login en lobby klaar voor gebruik.
            appLogin.setDispatch(dispatcherImpl);
            appLogin.setDb(db);
            lobby = Lobby.getInstance().init(db, dispatcherImpl);
            appLogin.setLobby(lobby);

        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isFull() {
        return connectedClients.size() >= SERVER_CAPACITY;
    }

    public void addConnectedClient(ClientInterface client) {
        connectedClients.add(client);
    }

    /*
     * Getters & Setters
     * */

    @Override
    public List<ClientInterface> getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(List<ClientInterface> connectedClients) {
        this.connectedClients = connectedClients;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int getPort() {
        return port;
    }


    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getRestPort() {
        return restPort;
    }

    public void setRestPort(int restPort) {
        this.restPort = restPort;
    }

    @Override
    public int getDispatcherPort() {
        return dispatcherPort;
    }

    public void setDispatcherPort(int dispatcherPort) {
        this.dispatcherPort = dispatcherPort;
    }

    @Override
    public String getDispatcherIp() {
        return dispatcherIp;
    }

    public void setDispatcherIp(String dispatcherIp) {
        this.dispatcherIp = dispatcherIp;
    }

    @Override
    public DatabaseInterface getDb() {
        return db;
    }

    @Override
    public void setDb(DatabaseInterface db) {
        this.db = db;
    }

    @Override
    public AppLoginInterface getAppLogin() {
        return appLogin;
    }

    @Override
    public void setAppLogin(AppLoginInterface appLogin) {
        this.appLogin = appLogin;
    }

    @Override
    public LobbyInterface getLobby() {
        return lobby;
    }

    @Override
    public void setLobby(LobbyInterface lobby) {
        this.lobby = lobby;
    }

    @Override
    public ApplicationServerInterface getBackupServer() {
        return backupServer;
    }

    @Override
    public void setBackupServer(ApplicationServerInterface backupServer) {
        this.backupServer = backupServer;
    }
}

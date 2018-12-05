package com.kuleuven.distributedsystems.dispatcher;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import interfaces.*;

import java.rmi.RemoteException;
import java.util.List;

@JsonSerialize(using = ApplicationServerSerializer.class)
public class ApplicationServer implements ApplicationServerInterface {
    private String name;
    private String ip;
    private int port;
    private int restPort;
    private AppLoginInterface appLogin;
    private ApplicationServerInterface backupServer;

    public ApplicationServer(String name, String ip, int port, int restPort, AppLoginInterface appLogin) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.restPort = restPort;
        this.appLogin = appLogin;
    }

    public ApplicationServer(ApplicationServerInterface server) throws RemoteException {
        this.name = server.getName();
        this.ip = server.getIp();
        this.port = server.getPort();
        this.restPort = server.getRestPort();
        this.appLogin = server.getAppLogin();
    }

    @Override
    public void startLogin() throws RemoteException {

    }

    @Override
    public void registerWithDispatcher() throws RemoteException {

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
    public int getDispatcherPort() throws RemoteException {
        return 0;
    }

    @Override
    public String getDispatcherIp() throws RemoteException {
        return null;
    }

    @Override
    public DatabaseInterface getDb() throws RemoteException {
        return null;
    }

    @Override
    public void setDb(DatabaseInterface db) throws RemoteException {

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
    public LobbyInterface getLobby() throws RemoteException {
        return null;
    }

    @Override
    public void setLobby(LobbyInterface lobby) throws RemoteException {

    }

    @Override
    public ApplicationServerInterface getBackupServer() {
        return backupServer;
    }

    @Override
    public void setBackupServer(ApplicationServerInterface backupServer) {
        this.backupServer = backupServer;
    }

    @Override
    public boolean isFull() throws RemoteException {
        return false;
    }

    @Override
    public List<ClientInterface> getConnectedClients() throws RemoteException {
        return null;
    }

    @Override
    public void addConnectedClient(ClientInterface client) {

    }
}

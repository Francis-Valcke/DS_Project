package com.kuleuven.distributedsystems.applicationserver;

import interfaces.AppLoginInterface;
import interfaces.ApplicationServerInterface;
import interfaces.LobbyInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ApplicationServer extends UnicastRemoteObject implements ApplicationServerInterface {

    private static ApplicationServerInterface instance;

    static {
        try {
            instance = new ApplicationServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private String name;
    private String ip;
    private int rmiPort;
    private int restPort;
    private AppLoginInterface app_login;
    private LobbyInterface lobby;
    private ApplicationServerInterface backupServer;

    private ApplicationServer() throws RemoteException {
    }

    public void init(String name, String ip, int rmiPort, int restPort){
        this.name = name;
        this.ip = ip;
        this.rmiPort = rmiPort;
        this.restPort = restPort;
    }

    @Override
    public String getIp() {
        return null;
    }

    @Override
    public void setIp(String ip) {

    }

    @Override
    public int getRmiPort() {
        return 0;
    }

    @Override
    public void setRmiPort(int rmiPort) {

    }

    @Override
    public AppLoginInterface getApp_login() {
        return null;
    }

    @Override
    public void setApp_login(AppLoginInterface app_login) {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getRestPort() {
        return 0;
    }

    @Override
    public void setBackupServer(ApplicationServerInterface server) {

    }

    @Override
    public ApplicationServerInterface getBackupServer() {
        return null;
    }
}

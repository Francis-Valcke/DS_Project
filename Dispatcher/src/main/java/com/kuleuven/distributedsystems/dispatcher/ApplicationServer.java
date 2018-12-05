package com.kuleuven.distributedsystems.dispatcher;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import interfaces.AppLoginInterface;
import interfaces.ApplicationServerInterface;

@JsonSerialize(using = ApplicationServerSerializer.class)
public class ApplicationServer implements ApplicationServerInterface {
    private String name;
    private String ip;
    private int rmiPort;
    private int restPort;
    private AppLoginInterface app_login;
    private ApplicationServerInterface backupServer;

    public ApplicationServer(String name, String ip, int rmiPort, AppLoginInterface app_login) {
        this.name = name;
        this.ip = ip;
        this.rmiPort = rmiPort;
        this.restPort = rmiPort + 1;
        this.app_login = app_login;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public AppLoginInterface getApp_login() {
        return app_login;
    }

    public void setApp_login(AppLoginInterface app_login) {
        this.app_login = app_login;
    }

    public String getName() {
        return name;
    }

    public int getRestPort() {
        return restPort;
    }

    public void setBackupServer(ApplicationServerInterface server) {
        backupServer = server;
    }

    public ApplicationServerInterface getBackupServer() {
        return backupServer;
    }
}

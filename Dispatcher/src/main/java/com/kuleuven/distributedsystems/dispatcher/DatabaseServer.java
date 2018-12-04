package com.kuleuven.distributedsystems.dispatcher;

import interfaces.DatabaseInterface;

public class DatabaseServer {
    private String id;
    private String ip;
    private int port;
    private DatabaseInterface databaseImp;

    public DatabaseServer(String id, String ip, int port, DatabaseInterface databaseImp) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.databaseImp = databaseImp;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public DatabaseInterface getDatabaseImp() {
        return databaseImp;
    }

    public void setDatabaseImp(DatabaseInterface databaseImp) {
        this.databaseImp = databaseImp;
    }
}

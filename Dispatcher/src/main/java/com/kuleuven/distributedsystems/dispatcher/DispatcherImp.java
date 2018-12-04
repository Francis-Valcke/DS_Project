package com.kuleuven.distributedsystems.dispatcher;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.AppLoginInterface;
import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class DispatcherImp extends UnicastRemoteObject implements DispatcherInterface {

    private static DispatcherImp instance;

    static {
        try {
            instance = new DispatcherImp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private List<DatabaseServer> databaseServers = new LinkedList<>();
    private List<ApplicationServer> applicationServers = new LinkedList<>();

    public DispatcherImp() throws RemoteException{
    }

    public static DispatcherImp getInstance() {
        return instance;
    }

    public void registerDatabaseServer(String id, int port) throws RemoteException{
        try{
            //Verbinden met de RMI van de database
            Registry registry = LocateRegistry.getRegistry(RemoteServer.getClientHost(), port);
            DatabaseInterface databaseImp = (DatabaseInterface) registry.lookup("database_service");
            DatabaseServer newServer = new DatabaseServer(id, RemoteServer.getClientHost(), port, databaseImp);
            //Nieuwe database toevoegen aan pool
            databaseServers.add(newServer);
            System.out.println("INFO: new database server registered: "+id+" ["+newServer.getIp()+":"+newServer.getPort()+"]");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public DatabaseInterface registerApplicationServer(String id, int port) throws RemoteException{
        try{
            Registry registry = LocateRegistry.getRegistry(RemoteServer.getClientHost(), port);
            AppLoginInterface app_login = (AppLoginInterface) registry.lookup("login_service");
            ApplicationServer newServer = new ApplicationServer(id, RemoteServer.getClientHost(), port, app_login);
            applicationServers.add(newServer);
            System.out.println("INFO: New application server registered: " + id + " [" + newServer.getIp() + ":" + newServer.getRmiPort() + "]");
            return databaseServers.get(0).getDatabaseImp();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        databaseServers.get(0).getDatabaseImp().createNewUser(username, password);
    }


    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        return databaseServers.get(0).getDatabaseImp().createToken(username, password);
    }


    public boolean isTokenValid(String username, String token) throws RemoteException {
        return databaseServers.get(0).getDatabaseImp().isTokenValid(username, token);
    }

    public AppLoginInterface getApplicationServer() throws RemoteException{
        System.out.println("INFO: new client connected");
        return applicationServers.get(0).getApp_login();
    }


    public List<DatabaseServer> getDatabaseServers() {
        return databaseServers;
    }

    public void setDatabaseServers(List<DatabaseServer> databaseServers) {
        this.databaseServers = databaseServers;
    }

    public List<ApplicationServer> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(List<ApplicationServer> applicationServers) {
        this.applicationServers = applicationServers;
    }
}

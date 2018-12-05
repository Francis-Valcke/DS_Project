package com.kuleuven.distributedsystems.dispatcher;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.ApplicationServerInterface;
import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;
import interfaces.LobbyInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Dispatcher extends UnicastRemoteObject implements DispatcherInterface {

    private static Dispatcher instance;

    static {
        try {
            instance = new Dispatcher();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private List<DatabaseServer> databaseServers = new LinkedList<>();
    private List<ApplicationServerInterface> applicationServers = new LinkedList<>();
    private List<ApplicationServerInterface> unPairedServers = new LinkedList<>();

    public Dispatcher() throws RemoteException {
    }

    public static Dispatcher getInstance() {
        return instance;
    }

    public void registerDatabaseServer(String name, int port) throws RemoteException {
        try {
            //Verbinden met de RMI van de database
            Registry registry = LocateRegistry.getRegistry(RemoteServer.getClientHost(), port);
            DatabaseInterface databaseImp = (DatabaseInterface) registry.lookup("database_service");
            DatabaseServer newServer = new DatabaseServer(name, RemoteServer.getClientHost(), port, databaseImp);
            //Nieuwe database toevoegen aan pool
            databaseServers.add(newServer);
            System.out.println("INFO: new database server registered: " + name + " [" + newServer.getIp() + ":" + newServer.getPort() + "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized DatabaseInterface registerApplicationServer(ApplicationServerInterface server) throws RemoteException {
        try {

            applicationServers.add(server);
            System.out.println("INFO: New application server registered: " + server.getName() + " [" + server.getIp() + ":" + server.getPort() + "]");
            //Voeg de appserver toe aan de unpaired server lijst
            unPairedServers.add(server);
            //Als er 2 servers aanwezig zijn in de unpaired list kunnen we ze paren aan elkaar
            if (unPairedServers.size() == 2) {
                ApplicationServerInterface a = unPairedServers.remove(0);
                ApplicationServerInterface b = unPairedServers.remove(0);
                a.setBackupServer(b);
                System.out.println("Server " + b.getName() + " is a backup server of server " + a.getName());
                b.setBackupServer(a);
                System.out.println("Server " + a.getName() + " is a backup server of server " + b.getName());
            }

            //Notify waiting users that a new server is available
            notifyAll();

            return databaseServers.get(0).getDatabaseImp();
        } catch (Exception e) {
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

    public synchronized ApplicationServerInterface getApplicationServer() throws RemoteException {
        System.out.println("INFO: new client connected");

        Optional<ApplicationServerInterface> oServer = applicationServers.stream()
                .filter(s -> {
                    boolean result = true;
                    try {
                        result = !s.isFull();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return result;
                }).min(new Comparator<ApplicationServerInterface>() {
                    @Override
                    public int compare(ApplicationServerInterface o1, ApplicationServerInterface o2) {
                        int result = 0;

                        try {
                            result = o1.getConnectedClients().size() - o2.getConnectedClients().size();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        return result;
                    }
                });

        if (oServer.isPresent()) return oServer.get();
        else {
            System.out.println("Starting new servers");

            //Een nieuw paar servers opstarten.
            Main.startApplicationServers(2);
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getApplicationServer();
        }
    }

    @Override
    public void broadCastLobby(LobbyInterface lobby) throws RemoteException {
        for (ApplicationServerInterface applicationServer : applicationServers) {
            applicationServer.showLobby(lobby);
        }
    }

    public List<DatabaseServer> getDatabaseServers() {
        return databaseServers;
    }

    public void setDatabaseServers(List<DatabaseServer> databaseServers) {
        this.databaseServers = databaseServers;
    }

    public List<ApplicationServerInterface> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(List<ApplicationServerInterface> applicationServers) {
        this.applicationServers = applicationServers;
    }
}

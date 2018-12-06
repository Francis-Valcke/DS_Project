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
import java.util.*;

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

        //Return een appServer met minst connected players (poging tot load balancing)
        Optional<ApplicationServerInterface> oServer = applicationServers.stream()
                .min(new Comparator<ApplicationServerInterface>() {
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

        return oServer.get();
    }

    public synchronized ApplicationServerInterface getApplicationServerByFreeSlots(int slots) throws RemoteException {
        Optional<ApplicationServerInterface> oServer = applicationServers.stream()
                .filter(s -> {
            boolean result = false;
            try {
                result = s.canFit(slots);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return result;
        }).findFirst();

        if (oServer.isPresent()) return oServer.get();
        else {
            //In dit geval is er geen enkele app server gevonden die de game aan kan dus wordt een server paar opgestart
            System.out.println("Starting new servers");

            Main.startApplicationServers(2);
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getApplicationServerByFreeSlots(slots);
        }
    }

    @Override
    public synchronized void broadCastLobby(LobbyInterface lobby) throws RemoteException {
        for (ApplicationServerInterface applicationServer : applicationServers) {
            applicationServer.updateLobby(lobby);
        }
    }

    public ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException {
        ApplicationServerInterface appServer = null;
        for (ApplicationServerInterface applicationServer : applicationServers) {
            if (applicationServer.getName().equals(name)){
                appServer = applicationServer;
            }
        }
        return appServer;
    }

    //This method only gets called when a lobby is registered.
    public Set<LobbyInterface> requestAllLobbies() throws RemoteException {
        Set<LobbyInterface> lobbies = new HashSet<>();
        for (ApplicationServerInterface applicationServer : applicationServers) {
            lobbies.add(applicationServer.getLobby());
        }
        return lobbies;
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

package com.kuleuven.distributedsystems.dispatcher;

import com.google.common.collect.HashMultimap;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.*;

import java.rmi.RemoteException;
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

    private HashMultimap<ApplicationServerInterface, String> connectedUsers = HashMultimap.create();
    private DatabaseInterface masterDB;
    private List<DatabaseInterface> databaseServers = new LinkedList<>();
    private List<ApplicationServerInterface> applicationServers = new LinkedList<>();
    private List<ApplicationServerInterface> unPairedServers = new LinkedList<>();
    private List<VirtualClientServerInterface> virtualClientServers = new LinkedList<>();

    public Dispatcher() throws RemoteException {
    }

    public static Dispatcher getInstance() {
        return instance;
    }

    @Override
    public synchronized void registerDatabaseServer(DatabaseInterface db) throws RemoteException {
        try {
            //1ste DB als masterDB instellen bij de rest
            if (databaseServers.size() < 1) masterDB = db;
            else {
                db.setMaster(masterDB);
                masterDB.addSlave(db);
            }
            //Nieuwe database toevoegen aan pool
            databaseServers.add(db);

            System.out.println("INFO: new database server registered");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
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

            return databaseServers.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException {
        System.out.println("INFO: New application server registered: " + server.getName() + " [" + server.getAddress() + ":" + server.getPort() + "]");
        virtualClientServers.add(server);
    }


    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        masterDB.createNewUser(username, password);
    }

    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        return masterDB.createToken(username, password);
    }

    public boolean isTokenValid(String username, String token) throws RemoteException {
        return masterDB.isTokenValid(username, token);
    }

    public synchronized ApplicationServerInterface getApplicationServer() throws RemoteException {
        System.out.println("INFO: new client connected");

        //Return een appServer met minst connected players (poging tot load balancing)
        Optional<ApplicationServerInterface> oServer = applicationServers.stream()
                .min(Comparator.comparingInt(o -> connectedUsers.get(o).size()));

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
            int serverCount = applicationServers.size();
            Main.startApplicationServers(2);
            //Wachten tot ze alletwee zijn opgestart
            while (applicationServers.size() != serverCount + 2) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return getApplicationServerByFreeSlots(slots);
        }
    }

    //Load balance the mobile users over the available servers
    @Override
    public VirtualClientServerInterface getVirtualClientServer(){
        Optional<VirtualClientServerInterface> server = virtualClientServers.stream()
                .min(Comparator.comparingInt(o -> {
                    try {
                        return o.getConnectedClients().size();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }));

        return server.get();
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
            if (applicationServer.getName().equals(name)) {
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

    public List<ApplicationServerInterface> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(List<ApplicationServerInterface> applicationServers) {
        this.applicationServers = applicationServers;
    }

    public List<DatabaseInterface> getDatabaseServers() {
        return databaseServers;
    }

    public DatabaseInterface getMasterDB() {
        return masterDB;
    }



    @Override
    public boolean isConnected(String username) {
        return connectedUsers.values().stream().anyMatch(s -> s.equals(username));
    }

    @Override
    public void addUser(ApplicationServerInterface appServer, String username) {
        connectedUsers.put(appServer, username);
    }

    @Override
    public void removeUser(ApplicationServerInterface appServer, String username) {
        connectedUsers.remove(appServer, username);
    }
}

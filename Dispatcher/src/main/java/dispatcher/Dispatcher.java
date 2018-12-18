package dispatcher;

import com.google.common.collect.HashMultimap;
import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.ApplicationServerInterface;
import interfaces.DatabaseInterface;
import interfaces.VirtualClientServerInterface;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Dispatcher {

    private static Dispatcher instance = new Dispatcher();

    private HashMultimap<ApplicationServerInterface, String> connectedUsers = HashMultimap.create();
    private HashMultimap<DatabaseInterface, ApplicationServerInterface> databaseApplicationMap = HashMultimap.create();
    private DatabaseInterface masterDB;
    private List<DatabaseInterface> databaseServers = new LinkedList<>();
    private List<ApplicationServerInterface> applicationServers = new LinkedList<>();
    private List<ApplicationServerInterface> unPairedServers = new LinkedList<>();
    private List<VirtualClientServerInterface> virtualClientServers = new LinkedList<>();

    private Dispatcher() {
    }

    public static Dispatcher getInstance() {
        return instance;
    }

    //TODO: appservers deregistreren

    public synchronized void registerDatabaseServer(DatabaseInterface db) throws RemoteException {
            //1ste DB als masterDB instellen bij de rest
            if (databaseServers.size() < 1) masterDB = db;
            else {
                db.setMaster(masterDB);
            }
            for (DatabaseInterface dbi : databaseServers) {
                db.addPeer(dbi);
                dbi.addPeer(db);
            }
            //Nieuwe database toevoegen aan pool
            databaseServers.add(db);

            System.out.println("INFO: new database server registered");
    }

    public synchronized DatabaseInterface registerApplicationServer(ApplicationServerInterface appServer) throws RemoteException {
        applicationServers.add(appServer);
        System.out.println("INFO: New application server registered: " + appServer.getName() + " [" + appServer.getIp() + ":" + appServer.getPort() + "]");
        //Voeg de appserver toe aan de unpaired server lijst
        unPairedServers.add(appServer);
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

        DatabaseInterface db = getDatabaseWithLowestLoad();
        databaseApplicationMap.put(db, appServer);

        return db;
    }

    /*public synchronized void deregisterApplicationServer(String name) throws RemoteException{
        for(ApplicationServerInterface appServer : applicationServers){
            if(appServer.getName() == name){
                applicationServers.remove(appServer);
                break;
            }
        }
        applicationServers.remove();
    }*/

    public DatabaseInterface getDatabaseWithLowestLoad() {
        int minConnectedAppServers = Integer.MAX_VALUE;
        DatabaseInterface dbWithLowestLoad = null;
        for (DatabaseInterface db : databaseServers) {
            try {
                //Checken als DB beschikbaar is
                db.ping();
                if (databaseApplicationMap.get(db).size() < minConnectedAppServers) {
                    minConnectedAppServers = databaseApplicationMap.get(db).size();
                    dbWithLowestLoad = db;
                }
            } catch (RemoteException e) {
                System.out.println("Lost connection to a DB");
            }

        }
        return dbWithLowestLoad;
    }

    public synchronized void registerVirtualClientServer(VirtualClientServerInterface server) throws RemoteException {
        System.out.println("INFO: New application server registered: " + server.getName() + " [" + server.getAddress() + ":" + server.getPort() + "]");
        virtualClientServers.add(server);
    }

    public void checkShouldShutDown(ApplicationServerInterface server) throws RemoteException {
        ApplicationServerInterface backup = server.getBackupServer();
        if (server.getLobby().getAllLiveGames().isEmpty() && backup.getLobby().getAllLiveGames().isEmpty()) {
            markApplicationServerPairUnavailable(server);
            server.shutDown();
            backup.shutDown();
        }
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

    public synchronized ApplicationServerInterface getApplicationServer() {
        System.out.println("INFO: new client connected");

        //Return een appServer met minst connected players (poging tot load balancing)
        Optional<ApplicationServerInterface> oServer = applicationServers.stream()
                .min(Comparator.comparingInt(o -> connectedUsers.get(o).size()));

        return oServer.get();
    }

    public synchronized ApplicationServerInterface getApplicationServerByFreeSlots(int slots) {
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
    public VirtualClientServerInterface getVirtualClientServer() {
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

    public ApplicationServerInterface getApplicationServerByName(String name) throws RemoteException {
        ApplicationServerInterface appServer = null;
        for (ApplicationServerInterface applicationServer : applicationServers) {
            if (applicationServer.getName().equals(name)) {
                appServer = applicationServer;
            }
        }
        return appServer;
    }

    public boolean isConnected(String username) {
        return connectedUsers.values().stream().anyMatch(s -> s.equals(username));
    }

    public void addUser(ApplicationServerInterface appServer, String username) {
        connectedUsers.put(appServer, username);
    }

    public void removeUser(ApplicationServerInterface appServer, String username) {
        connectedUsers.remove(appServer, username);
    }

    public synchronized boolean markApplicationServerPairUnavailable(ApplicationServerInterface server) throws RemoteException {

        if (applicationServers.size() > 2) {
            applicationServers.remove(server);
            applicationServers.remove(server.getBackupServer());
            return true;
        } else {
            return false;
        }
    }
}

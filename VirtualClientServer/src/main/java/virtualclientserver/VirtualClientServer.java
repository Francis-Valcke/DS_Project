package virtualclientserver;

import exceptions.AlreadyPresentException;
import exceptions.UserNotLoggedInException;
import interfaces.ClientInterface;
import interfaces.ServerDispatcherInterface;
import interfaces.VirtualClientServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class VirtualClientServer extends UnicastRemoteObject implements VirtualClientServerInterface {

    /*
     * Instance
     * */
    private static VirtualClientServer instance;

    static {
        try {
            instance = new VirtualClientServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static VirtualClientServer getInstance() {
        return instance;
    }

    /*
     * Attributes
     * */

    //Remote objects
    ServerDispatcherInterface dispatcher;

    //Local objects
    private String name;
    private String address;
    private String port;
    private Map<String, ClientInterface> connectedClients = new HashMap<>();

    /*
     * Constructors
     * */
    protected VirtualClientServer() throws RemoteException {
    }


    /*
     * Logic
     * */

    public void init(String serverName, String address, String port) {
        this.name = serverName;
        this.address = address;
        this.port = port;
        this.connectedClients = new HashMap<String, ClientInterface>();

        try {
            //First get the dispatcher reference
            Registry registry = LocateRegistry.getRegistry(Main.DISPATCH_IP, Main.DISPATCH_PORT);
            dispatcher = (ServerDispatcherInterface) registry.lookup("dispatcher_service");
            //Then register this server with the dispatcher
            dispatcher.registerVirtualClientServer(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Successfully registered with dispatcher.");
    }

    public ClientInterface removeClient(String token) throws UserNotLoggedInException {
        ClientInterface client = getClient(token);
        connectedClients.remove(token);
        return client;
    }

    public void addClient(VirtualClient client) throws RemoteException {
        connectedClients.put(client.getToken(), client);
    }

    public VirtualClient newClient(String username, String token) throws AlreadyPresentException, RemoteException {
        if (isLoggedIn(token)) throw new AlreadyPresentException();
        VirtualClient client = new VirtualClient(username, token);
        connectedClients.put(token, client);
        return client;
    }

    public ClientInterface getClient(String token) throws UserNotLoggedInException {
        if (!isLoggedIn(token)) {
            throw new UserNotLoggedInException();
        }
        return connectedClients.get(token);
    }

    public boolean isLoggedIn(String token) {
        return connectedClients.containsKey(token);
    }

    /*
     * Getters & Setters
     * */

    public ServerDispatcherInterface getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(ServerDispatcherInterface dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public Map<String, ClientInterface> getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(Map<String, ClientInterface> connectedClients) {
        this.connectedClients = connectedClients;
    }
}


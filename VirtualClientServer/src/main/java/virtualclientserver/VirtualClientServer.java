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

import static constants.DispatcherConstants.DISPATCHER_IP;
import static constants.DispatcherConstants.DISPATCHER_SERVER_PORT;
import static constants.ServiceConstants.SERVER_DISPATCHER_SERVICE;

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
            Registry registry = LocateRegistry.getRegistry(DISPATCHER_IP, DISPATCHER_SERVER_PORT);
            dispatcher = (ServerDispatcherInterface) registry.lookup(SERVER_DISPATCHER_SERVICE);
            //Then register this server with the dispatcher
            dispatcher.registerVirtualClientServer(this);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        System.out.println("INFO: Successfully registered with dispatcher.");
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public Map<String, ClientInterface> getConnectedClients() {
        return connectedClients;
    }

}


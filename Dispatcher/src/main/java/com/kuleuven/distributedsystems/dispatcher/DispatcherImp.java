package com.kuleuven.distributedsystems.dispatcher;

import exceptions.InvalidCredentialsException;
import exceptions.UserAlreadyExistsException;
import interfaces.ApplicationServerInterface;
import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DispatcherImp extends UnicastRemoteObject implements DispatcherInterface {

    private static DispatcherImp instance;

    static {
        try {
            instance = new DispatcherImp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private List<DatabaseInterface> databaseServers = new LinkedList<>();
    private List<ApplicationServerInterface> applicationServers = new LinkedList<>();
    private List<ApplicationServerInterface> unPairedServers = new LinkedList<>();

    public DispatcherImp() throws RemoteException {
    }

    public static DispatcherImp getInstance() {
        return instance;
    }

    public void registerDatabaseServer(DatabaseInterface db) throws RemoteException {
        try {
            //Nieuwe database toevoegen aan pool
            databaseServers.add(db);
            //Database master maken als het de eerste is
            if(databaseServers.size() == 1) db.setMaster(true);
            System.out.println("INFO: new database server registered");

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

            return databaseServers.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void registerNewUser(String username, String password) throws RemoteException, UserAlreadyExistsException {
        databaseServers.get(0).createNewUser(username, password);
    }


    public String requestNewToken(String username, String password) throws RemoteException, InvalidCredentialsException {
        return databaseServers.get(0).createToken(username, password);
    }


    public boolean isTokenValid(String username, String token) throws RemoteException {
        return databaseServers.get(0).isTokenValid(username, token);
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

    public List<ApplicationServerInterface> getApplicationServers() {
        return applicationServers;
    }

    public void setApplicationServers(List<ApplicationServerInterface> applicationServers) {
        this.applicationServers = applicationServers;
    }
}

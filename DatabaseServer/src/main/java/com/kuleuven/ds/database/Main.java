package com.kuleuven.ds.database;

import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {

    private static final String DISPATCH_IP = "localhost";
    private static final int DISPATCH_PORT = 1000;
    private static String dbFilePath;
    private static int serverPort;

    private static DispatcherInterface dispatcherImp;
    private static DatabaseInterface databaseImp;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            dbFilePath = args[0];
            serverPort = Integer.parseInt(args[1]);
        } else {
            dbFilePath = "db_alpha";
            serverPort = 1100;
        }
        databaseImp = startRMI(dbFilePath, serverPort);
        dispatcherImp = registerDispatcher(dbFilePath, serverPort);
    }

    public static DispatcherInterface registerDispatcher(String serverName, int port){

        //Registreren bij dispatcher
        try{
            Registry registry = LocateRegistry.getRegistry(DISPATCH_IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup("dispatcher_service");
            dispatcherImp.registerDatabaseServer(serverName, port);
            System.out.println("INFO: "+ serverName+" up and running on port: "+port);
            return dispatcherImp;
        }
        catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
        return null;
    }
    public static DatabaseInterface startRMI(String server_name, int port){
        try{
            databaseImp = new DatabaseImp(server_name);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("database_service", databaseImp);
            return databaseImp;
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
        return null;
    }

}

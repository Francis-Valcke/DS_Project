package database;

import interfaces.ServerDispatcherInterface;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static constants.DispatcherConstants.DISPATCHER_IP;
import static constants.DispatcherConstants.DISPATCHER_SERVER_PORT;
import static constants.ServiceConstants.SERVER_DISPATCHER_SERVICE;

@SpringBootApplication
public class Main {

    private static String dbFilePath;
    private static int serverPort;

    private static ServerDispatcherInterface dispatcherImp;
    private static DatabaseImp databaseImp;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException{
        if (args.length != 0) {
            dbFilePath = args[0];
            serverPort = Integer.parseInt(args[1]);
        } else {
            dbFilePath = "db_alpha";
            serverPort = 1100;
        }

        databaseImp = new DatabaseImp(dbFilePath);
        databaseImp.clearGames();
        dispatcherImp = registerDispatcher();
        System.out.println("INFO: " + dbFilePath + " up and running on port: " + serverPort);

        /*for(int i = 1; i< 152; i++){
            try {
                database.insertPhoto(i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }*/
    }

    public static ServerDispatcherInterface registerDispatcher() {

        //Registreren bij dispatcher
        try {
            Registry registry = LocateRegistry.getRegistry(DISPATCHER_IP, DISPATCHER_SERVER_PORT);
            ServerDispatcherInterface dispatcherImp = (ServerDispatcherInterface) registry.lookup(SERVER_DISPATCHER_SERVICE);
            dispatcherImp.registerDatabaseServer(databaseImp);
            return dispatcherImp;
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}

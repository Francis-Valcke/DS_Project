package database;

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

    public static DispatcherInterface registerDispatcher() {

        //Registreren bij dispatcher
        try {
            Registry registry = LocateRegistry.getRegistry(DISPATCH_IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImp = (DispatcherInterface) registry.lookup("dispatcher_service");
            dispatcherImp.registerDatabaseServer(databaseImp);
            return dispatcherImp;
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}

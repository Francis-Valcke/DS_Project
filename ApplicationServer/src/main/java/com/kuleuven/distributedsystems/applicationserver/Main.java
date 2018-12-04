package com.kuleuven.distributedsystems.applicationserver;

import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {

    public static final String DISPATCH_IP = "localhost";
    public static final int DISPATCH_PORT = 1000;

    public static int port;
    public static String server_name;
    public static Lobby lobby;
    public static AppLogin appLogin;
    public static DatabaseInterface db;


    public static void main(String[] args) {
        if (args.length != 0) {
            server_name = args[0];
            port = Integer.parseInt(args[1]);
            System.setProperty("server.port", args[2]);
        }
        else {
            server_name = "Application server Alpha";
            port = 1200;
            System.setProperty("server.port", "1201");
        }


        SpringApplication.run(Main.class, args);

        startLogin();
        System.out.println("INFO: up and running on port: "+port);
        registerWithDispatcher();
        System.out.println("INFO: connection made to database and dispatch");

    }

    public static void startLogin(){
        try{
            appLogin = AppLogin.getInstance();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("login_service", appLogin);
        } catch(RemoteException re){
            re.printStackTrace();
        }
    }

    public static void registerWithDispatcher(){
        try{
            Registry registry = LocateRegistry.getRegistry(DISPATCH_IP, DISPATCH_PORT);
            DispatcherInterface dispatcherImpl = (DispatcherInterface) registry.lookup("dispatcher_service");
            //We krijgen een link naar de databank terug die deze server zal gebruiken
            db = dispatcherImpl.registerApplicationServer(server_name, port);

            //We maken de login en lobby klaar voor gebruik.
            appLogin.setDispatch(dispatcherImpl);
            appLogin.setDb(db);
            lobby = Lobby.getInstance().init(db, dispatcherImpl);
            appLogin.setLobby(lobby);

        } catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

}

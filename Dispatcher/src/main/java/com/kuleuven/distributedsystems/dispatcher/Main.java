package com.kuleuven.distributedsystems.dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        if (args.length != 0) {
            System.setProperty("server.port", args[2]);
            DispatcherImp dispatcherImp = startDispatcher(Integer.parseInt(args[1]));
        } else {
            System.setProperty("server.port", "1001");
            DispatcherImp dispatcherImp = startDispatcher(Integer.parseInt("1000"));
        }
        SpringApplication.run(Main.class, args);
    }

    public static DispatcherImp startDispatcher(int port){
        try{
            DispatcherImp dispatcherImp = DispatcherImp.getInstance();
            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("dispatcher_service", dispatcherImp);
            System.out.println("INFO: up and running on port: "+port);
            return dispatcherImp;
        }
        catch(RemoteException re){
            re.printStackTrace();
        }
        return null;
    }
}

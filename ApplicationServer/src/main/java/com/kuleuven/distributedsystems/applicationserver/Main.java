package com.kuleuven.distributedsystems.applicationserver;

import interfaces.DatabaseInterface;
import interfaces.DispatcherInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class Main {

    public static final String DISPATCH_IP = "localhost";
    public static final int DISPATCH_PORT = 1000;

    public static String ip;
    public static int restPort;
    public static int port;
    public static String server_name;


    public static void main(String[] args) {
        if (args.length != 0) {
            server_name = args[0];
            port = Integer.parseInt(args[1]);
            restPort = Integer.parseInt(args[2]);
        }
        else {
            server_name = "Application server Alpha";
            port = 1200;
            restPort = 1600;
        }
        System.setProperty("server.port", String.valueOf(restPort));
        SpringApplication.run(Main.class, args);


        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ApplicationServer server = ApplicationServer.getInstance();
        server.init(server_name, ip, port, restPort, DISPATCH_IP, DISPATCH_PORT);

    }


}

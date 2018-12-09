package com.kuleuven.ds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@SpringBootApplication
public class Main {

    public static final String DISPATCH_IP = "localhost";
    public static final int DISPATCH_PORT = 1000;

    private static String serverName;
    private static String address;
    private static String port;

    public static void main(String[] args) {

        serverName = args[0];
        port = args[1];
        try {
            address = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.setProperty("server.port", String.valueOf(port));
        SpringApplication.run(Main.class, args);

        VirtualClientServer.getInstance().init(serverName, address, port);
    }

}

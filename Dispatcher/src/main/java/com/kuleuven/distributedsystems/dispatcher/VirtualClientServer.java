package com.kuleuven.distributedsystems.dispatcher;

public class VirtualClientServer {

    private String name;
    private String address;
    private String port;

    public VirtualClientServer() {
    }

    public VirtualClientServer(String name, String address, String port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

package dispatcher;

import java.io.Serializable;

public class VirtualClientServer implements Serializable {

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

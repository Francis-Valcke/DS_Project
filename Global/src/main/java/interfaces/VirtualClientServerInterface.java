package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface VirtualClientServerInterface extends Remote {

    String getName() throws RemoteException;

    String getAddress() throws RemoteException;

    String getPort() throws RemoteException;

    Map<String, ClientInterface> getConnectedClients() throws RemoteException;
}

package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualClientServerInterface extends Remote {


    String getName() throws RemoteException;

    String getAddress() throws RemoteException;

    String getPort() throws RemoteException;
}

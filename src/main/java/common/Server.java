package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author yuchen
 */
public interface Server extends Remote{
    public static final String SERVER_NAME_IN_REGISTRY = "Server";
    
    long login(Client remoteNode, Credentials credentials) throws RemoteException;
    
    void logout(long id) throws RemoteException;
    
    String[] list() throws RemoteException;
    
    FileDTO open(String filename) throws RemoteException;
    
    void upload(long id, String filename, int size, String permission) throws RemoteException;
    
    /*
    void delete(String file) throws RemoteException;
    */
}

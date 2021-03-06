package server.startup;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.controller.Controller;
import server.integration.CatalogDBException;



/**
 * Starts the chat servant and binds it in the RMI registry.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     * @throws server.integration.CatalogDBException
     */
    public static void main(String[] args) throws CatalogDBException {
        try {
            System.out.println("In main");
            new Main().startRegistry();
            System.out.println("after registry");
            Naming.rebind(Controller.SERVER_NAME_IN_REGISTRY, new Controller());
            System.out.println("File server is running.");
        } catch (MalformedURLException | RemoteException ex) {
            System.out.println("Own server.");
        }
    }
    
    private void startRegistry() throws RemoteException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryIsRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
    }
}

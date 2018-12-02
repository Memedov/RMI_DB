package client.startup;

import java.rmi.RemoteException;
import client.view.NonBlockingInterpreter;

/**
 * Starts the catalog client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
            new NonBlockingInterpreter().start();
        } catch (RemoteException ex) {
            System.out.println("Could not start client.");
        }
    }
}
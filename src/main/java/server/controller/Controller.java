package server.controller;

import common.Client;
import common.Credentials;
import common.FileDTO;
import common.Server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.integration.CatalogDBException;
import server.model.UserManager;
import server.model.Catalog;
import server.model.File;

/**
 *
 * @author Ramiz
 */
public class Controller  extends UnicastRemoteObject implements Server {
    private final String dbms = "derby";
    private final String datasource = "CatalogDB";
    private final Random idGenerator = new Random();
    private final UserManager userMgr = new UserManager();
    private final Catalog cat;


    public Controller() throws RemoteException, CatalogDBException {
        System.out.println("contr constructor");
        this.cat = new Catalog(dbms, datasource);
        System.out.println("after creating catalog");
    }
    
    @Override
    public long login(Client remoteNode, Credentials credentials) throws RemoteException {        
        long uid = userMgr.createUser(remoteNode, credentials);
        return uid;
    }

    @Override
    public void logout(long id) throws RemoteException {
        userMgr.removeUser(id);
    }
    
    @Override
    public String[] list() throws RemoteException{
        return cat.getFiles();
    }
    
    @Override
    public void upload(long id, String filename, int size, String permission) throws RemoteException{
        FileDTO file = cat.makeFile(filename, size, userMgr.getUser(id).username, permission);
        try {
            cat.addFile((File)file);
        } catch (CatalogDBException ex) {
            System.err.println("File could not be uploaded.");
        }
    }
    
    @Override
    public FileDTO open(String file) throws RemoteException{
        return cat.getFile(file);
    }
    
}


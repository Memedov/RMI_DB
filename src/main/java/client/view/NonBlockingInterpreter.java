package client.view;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import common.Client;
import common.Server;
import common.Credentials;
import common.FileDTO;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final Client myRemoteObj;
    private Server server;
    private long myIdAtServer;
    private boolean receivingCmds = false;

    public NonBlockingInterpreter() throws RemoteException {
        myRemoteObj = new ConsoleOutput();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     */
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case LOGOUT:
                        receivingCmds = false;
                        server.logout(myIdAtServer);
                        boolean forceUnexport = false;
                        UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
                        outMgr.println("Logged out!");
                        break;
                    case LOGIN:
                        lookupServer(cmdLine.getParameter(0));
                        myIdAtServer
                                = server.login(myRemoteObj,
                                               new Credentials(cmdLine.getParameter(1),
                                                               cmdLine.getParameter(2)));
                        outMgr.println("Logged in.");
                        break;
                    case LIST:
                        String[] files = server.list();
                        if(files.length > 0){
                            for(int i = 0; i < files.length; i++){
                                outMgr.println(files[i]);
                            }
                        }else{
                            outMgr.println("Catalog empty.");
                        }
                        break;
                    case OPEN:
                        FileDTO file = server.open(cmdLine.getParameter(0));
                        if(file != null){
                            outMgr.println(file.getFileName() + " size: " + file.getFileSize() + " Author: " + file.getFileOwner() + 
                                    " Privilege: " + file.getFilePermission());
                        }else{
                            outMgr.println("No such file.");
                        }
                        break;
                    case UPLOAD:
                        if(cmdLine.getParameter(2) != null){
                            if(cmdLine.getParameter(2).equals("RO")){
                                server.upload(myIdAtServer, cmdLine.getParameter(0), Integer.parseInt(cmdLine.getParameter(1)), cmdLine.getParameter(2));
                            }else if(cmdLine.getParameter(2).equals("RW")){
                                server.upload(myIdAtServer, cmdLine.getParameter(0), Integer.parseInt(cmdLine.getParameter(1)), cmdLine.getParameter(2));
                            }else{
                                outMgr.println("File privilege not delcared correctly, please use 'RO' for read only or 'RW' for read write permission.");
                            }
                        }else{
                            outMgr.println("One or more fields are not set. Please fill in filename, its size and the file privileges.");
                        }
                        break;
                    /*case DELETE:
                        //TODO
                        break;*/
                    default:
                        System.out.println("Non-valid command!");
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
            }
        }
    }

    private void lookupServer(String host) throws NotBoundException, MalformedURLException,
                                                  RemoteException {
        server = (Server) Naming.lookup(
                "//" + host + "/" + Server.SERVER_NAME_IN_REGISTRY);
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput extends UnicastRemoteObject implements Client {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void recvMsg(String msg) {
             outMgr.println((String) msg);
        }
    }
}

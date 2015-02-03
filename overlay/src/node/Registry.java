package node;

import transport.TCPServerThread;
import util.CommandLineParser;
import util.RegistryCommandsParser;
import wireformats.Event;
import wireformats.OverlayNodeSendsRegistration;
import wireformats.Protocol;
import wireformats.RegistryReportsRegistrationStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by ydubale on 1/20/15.
 */
public class Registry implements Node, Runnable {

    private ServerSocket ss;
    private int portNum;
    private Hashtable<Integer, MessagingNode> messNode;

    public Registry(int port){
        this.portNum = port;
        messNode = new Hashtable<>();
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(portNum, 10);
            Socket tempSock;
            while((tempSock = ss.accept()) != null){
                (new Thread(new TCPServerThread(tempSock, this))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Foreground commands ------------------- //
    public synchronized void list_messaging_nodes(){
        int count = 1;
        for(Integer i : messNode.keySet()){
            System.out.println(count + ": " + messNode.get(i));
            count++;
        }
    }

    public Hashtable<Integer, MessagingNode> getNodes(){
        return messNode;
    }

    @Override
    public synchronized Event onEvent(Event event) {
        if(event.getType() == Protocol.OVERLAY_NODE_SENDS_REGISTRATION){
            OverlayNodeSendsRegistration sendsReg = (OverlayNodeSendsRegistration) event;
            Random rand = new Random();

            //todo: Check no duplicate ids
            int assignedID = rand.nextInt(100);

            MessagingNode mNode = new MessagingNode(sendsReg.getIpAddr(), sendsReg.getPort(), assignedID);
            messNode.put(mNode.getID(), mNode);

            String infoStr = "Registration request successful. The number of messaging nodes currently constituting the overlay is ("+ messNode.size() +")";

            RegistryReportsRegistrationStatus regRRS = new RegistryReportsRegistrationStatus(assignedID, infoStr.toString());

            return regRRS;

        }
        return null;
    }

    public static void main(String args[]){
        CommandLineParser clp = new CommandLineParser();

        clp.validateRegisCLA(args);

        Registry registry = new Registry(clp.port_num);

        Thread registryThread = new Thread(registry);

        registryThread.start();

        RegistryCommandsParser rcp = new RegistryCommandsParser(registry);
        Scanner scan = new Scanner(System.in);
        String input;

        System.out.print("Command: ");
        while((input = scan.nextLine()) != null){
            rcp.parseArgument(input);
            System.out.print("Command: ");
        }

    }
}

package node;

import transport.TCPConnection;
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
import java.util.*;

/**
 * Created by ydubale on 1/20/15.
 */
public class Registry implements Node, Runnable {

    private static final int MAX_NODE_ID = 128;

    private ServerSocket ss;
    private int portNum;
    private Hashtable<Integer, MessagingNode> messNode;
    private Hashtable<Socket, TCPConnection> connections;
    private int routingTableSize = 3;
    private List<Integer> unUsedKeys;

    public Registry(int port){
        this.portNum = port;
        messNode = new Hashtable<>();
        connections = new Hashtable<>();

        unUsedKeys = new ArrayList<>();

        for(int i = 0; i < MAX_NODE_ID; i++){
            unUsedKeys.add(i);  //Populate array with IDs (0 - 127)
        }
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(portNum, 10);
            Socket tempSock;
            while((tempSock = ss.accept()) != null){
                connections.put(tempSock, new TCPConnection(tempSock));
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

    private int[] getRoutingTableForIndex(int index, Integer[] sortedIDs){

        return null;
    }

    public synchronized void setup_overlay(int size_table){
        routingTableSize = size_table;
        Object[] idsSorted = messNode.keySet().toArray();
        Arrays.sort(idsSorted);

    }

    public synchronized void list_routing_tables(){

    }

    public synchronized void start(){

    }

    // Helpers
    private synchronized int getRandomID(){
        Random rand = new Random();

        if(unUsedKeys.size() == 0){
            return -1;
        }

        int randomIndex = rand.nextInt(unUsedKeys.size());
        int assignedID = unUsedKeys.get(randomIndex);
        unUsedKeys.remove(randomIndex);

        return assignedID;
    }


    public Hashtable<Integer, MessagingNode> getNodes(){
        return messNode;
    }

    @Override
    public synchronized Event onEvent(Event event) {

        if(event.getType() == Protocol.OVERLAY_NODE_SENDS_REGISTRATION){
            OverlayNodeSendsRegistration sendsReg = (OverlayNodeSendsRegistration) event;

            int randomID = getRandomID();
            if(randomID == -1){
                System.out.println("No more available IDs to assign.");
                return null;
            }

            System.out.println("Using ID " + randomID);

            MessagingNode mNode = new MessagingNode(sendsReg.getIpAddr(), sendsReg.getPort(), randomID);
            synchronized (messNode){
                messNode.put(mNode.getID(), mNode);
            }

            String infoStr = "Registration request successful. The number of messaging nodes currently constituting the overlay is ("+ messNode.size() +")";

            RegistryReportsRegistrationStatus regRRS = new RegistryReportsRegistrationStatus(randomID, infoStr.toString());

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

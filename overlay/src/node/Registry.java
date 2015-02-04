package node;

import routing.RoutingEntry;
import routing.RoutingTable;
import transport.TCPConnection;
import transport.TCPServerThread;
import util.CommandLineParser;
import util.RegistryCommandsParser;
import wireformats.*;

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
    private int routingTableSize = 3;
    private List<Integer> unUsedKeys;

    public Registry(int port){
        this.portNum = port;
        messNode = new Hashtable<>();

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

                int randomID = getRandomID();
                if(randomID == -1){
                    System.out.println("No more available IDs to assign. No longer accepting connections.");
                    break;
                }

                messNode.put(randomID, new MessagingNode(randomID, tempSock));
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

    private RoutingTable getRoutingTableForIndex(int index, Object[] sortedIDs){

        RoutingTable rTable = new RoutingTable();

        int power = 1;
        boolean first = true;
        for(int i = 0; i < routingTableSize; i++){
            int entryIndex = (index + power) % sortedIDs.length;
            RoutingEntry rEntry = new RoutingEntry(messNode.get(sortedIDs[entryIndex]));
            rTable.addEntry(rEntry);
            power = (power << 1); // Increase by powers of 2
            //todo: safe check for power
        }

        return rTable;
    }

    public synchronized void setup_overlay(int size_table){
        routingTableSize = size_table;
        Object[] idsSorted = messNode.keySet().toArray();
        Arrays.sort(idsSorted);

        for(int i= 0; i < idsSorted.length; i++){
            RoutingTable rTable = getRoutingTableForIndex(i, idsSorted);
            MessagingNode tempMNode = messNode.get(idsSorted[i]);
            tempMNode.setRoutingTable(rTable);

            TCPConnection tempTCPC = tempMNode.getTCPC();

            RegistrySendsNodeManifest rsnm = new RegistrySendsNodeManifest(rTable, messNode.keySet());

            tempTCPC.sendData(rsnm.getBytes());
        }
    }

    public synchronized void list_routing_tables(){
        for(Integer id : messNode.keySet()){
            messNode.get(id).printRoutingTable();
        }
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

    private int getMessagingNodeID(String ip, int port){
        for(Integer i : messNode.keySet()){
            MessagingNode mNode = messNode.get(i);
            if(mNode.getSocket().getPort() == port &&
                    mNode.getSocket().getInetAddress().getHostAddress().equals(ip)){
                return i;
            }
        }
        return -1;
    }


    public Hashtable<Integer, MessagingNode> getNodes(){
        return messNode;
    }

    @Override
    public synchronized Event onEvent(Event event) {

        if(event.getType() == Protocol.OVERLAY_NODE_SENDS_REGISTRATION){
            OverlayNodeSendsRegistration sendsReg = (OverlayNodeSendsRegistration) event;

            int assignedID = getMessagingNodeID(sendsReg.getIpAddr(), sendsReg.getPort());

            if(assignedID == -1){
                System.out.println("NEVER COMMUNICATED WITH THIS NODE BEFORE");
            }

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

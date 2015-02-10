package node;

import routing.RoutingEntry;
import routing.RoutingTable;
import transport.TCPConnection;
import transport.TCPConnectionsCache;
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
public class Registry implements Node{

    private static final int MAX_NODE_ID = 128;

    private ServerSocket ss;
    private int serverPort;
    private Hashtable<Integer, MessagingNode> messNode;
    private int routingTableSize = 3;
    private List<Integer> unUsedKeys;
    private TCPConnectionsCache tcpCC;
    private int numCompletedNodes;
    private int numTrafficReceived = 0;

    public Registry(int port) throws IOException {
        this.serverPort = port;
        messNode = new Hashtable<>();
        tcpCC = new TCPConnectionsCache();

        Thread serverThread = new Thread(new TCPServerThread(this, new ServerSocket(serverPort)));
        serverThread.start();

        unUsedKeys = new ArrayList<>();
        for(int i = 0; i < MAX_NODE_ID; i++){
            unUsedKeys.add(i);  //Populate array with IDs (0 - 127)
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

            int nodeID = (Integer) sortedIDs[entryIndex];

            RoutingEntry rEntry = new RoutingEntry(nodeID, tcpCC.getTCPConnection(nodeID));
            rTable.addEntry(rEntry);
            power = (power << 1); // Increase by powers of 2
            //todo: safe check for power
        }

        return rTable;
    }

    public synchronized void setup_overlay(int size_table){
        routingTableSize = size_table;
        //todo: fix object array
        Object[] idsSorted = messNode.keySet().toArray();
        Arrays.sort(idsSorted);

        for(int i= 0; i < idsSorted.length; i++){

            int currID = (Integer) idsSorted[i];

            RoutingTable rTable = getRoutingTableForIndex(i, idsSorted);

            MessagingNode tempMNode = messNode.get(currID);

            tempMNode.setRoutingTable(rTable);

            RegistrySendsNodeManifest rsnm = new RegistrySendsNodeManifest(rTable, new ArrayList<>(messNode.keySet()));

            tcpCC.sendEvent(currID, rsnm);

        }
    }

    private void sendToAllNodes(Event event){
        for(Integer id : messNode.keySet()){
            tcpCC.sendEvent(id, event);
        }
    }

    public synchronized void list_routing_tables(){
        for(Integer id : messNode.keySet()){
            messNode.get(id).printRoutingTable();
        }
    }

    public synchronized void start(int numMessagesToSend){
        RegistryRequestsTaskInitiate rrti = new RegistryRequestsTaskInitiate(numMessagesToSend);
        sendToAllNodes(rrti);
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

    private void printTrafficSummaryForAll(){
        System.out.println("\nNode\tPackets Sent\tPackets Recv\t" +
                "Packets Relayed\tSum Values Sent\t\tSum Values Received");

        int sumPacketSent = 0;
        int sumPacketRecv = 0;
        int sumPacketRelay = 0;
        long sumValuesSent = 0;
        long sumValuesRecv = 0;

        for(Integer nodeID : messNode.keySet()){
            MessagingNode mNode = messNode.get(nodeID);

            sumPacketSent += mNode.getSendTracker();
            sumPacketRecv += mNode.getReceiveTracker();
            sumPacketRelay += mNode.getRelayTracker();
            sumValuesSent += mNode.getSendSummation();
            sumValuesRecv += mNode.getReceiveSummation();

            System.out.println(nodeID + "\t" + mNode.getTrafficSummary());
        }

        System.out.println("Sum\t" + sumPacketSent + "\t\t" + sumPacketRecv + "\t\t" + sumPacketRelay +
                "\t\t" + sumValuesSent + "\t\t" + sumValuesRecv);
    }


    public Hashtable<Integer, MessagingNode> getNodes(){
        return messNode;
    }

    @Override
    public synchronized void onEvent(Event event) {

        if(event == null){
            System.out.println("EVENT IS NULL YO!");
        }
        else if(event.getType() == Protocol.OVERLAY_NODE_SENDS_REGISTRATION){
            OverlayNodeSendsRegistration sendsReg = (OverlayNodeSendsRegistration) event;

            int assignedID = getRandomID();

            messNode.put(assignedID, new MessagingNode(assignedID));

            try {
                tcpCC.addNewConn(assignedID, new TCPConnection(new Socket(sendsReg.getIpAddr(), sendsReg.getPort()), this));
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Problem in onEvent of Registry, can't accept connection probably.");
            }

            if(assignedID == -1){
                System.out.println("NEVER COMMUNICATED WITH THIS NODE BEFORE. Id is -1.");
            }

            String infoStr = "Registration request successful. The number of messaging nodes" +
                    " currently constituting the overlay is ("+ messNode.size() +")";

            RegistryReportsRegistrationStatus regRRS = new RegistryReportsRegistrationStatus(assignedID, infoStr.toString());

            tcpCC.sendEvent(assignedID, regRRS);

        }
        else if(event.getType() == Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS){
            NodeReportsOverlaySetupStatus nross = (NodeReportsOverlaySetupStatus) event;

            if(nross.isSuccessful()){
                System.out.println("Registry now ready to initiate tasks");
            }
        }
        else if(event.getType() == Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED){
            OverlayNodeReportsTaskFinished onrtf = (OverlayNodeReportsTaskFinished) event;

            numCompletedNodes++;
            System.out.println(numCompletedNodes + " Finished");

            int numNodesInOverlay = messNode.size();

            if(numCompletedNodes == numNodesInOverlay){
                RegistryRequestsTrafficSummary rrts = new RegistryRequestsTrafficSummary();
                try {
                    System.out.println("Count to 20 seconds!");
                    Thread.sleep(20000);
                    System.out.println("Now sending task finished to all nodes!");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendToAllNodes(rrts);
            }
        }
        else if(event.getType() == Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY){
            OverlayNodeReportsTrafficSummary onrts = (OverlayNodeReportsTrafficSummary) event;

            MessagingNode currMNode = messNode.get(onrts.getNodeID());

            currMNode.setSendTracker(onrts.getTotalSent());
            currMNode.setRelayTracker(onrts.getTotalRelayed());
            currMNode.setSendSummation(onrts.getSumSent());
            currMNode.setReceiveTracker(onrts.getTotalReceived());
            currMNode.setReceiveSummation(onrts.getSumReceived());

            numTrafficReceived++;

            int numNodesInOverlay = messNode.size();
            if(numTrafficReceived == numNodesInOverlay){
                printTrafficSummaryForAll();
            }
        }
    }

    public static void main(String args[]){
        CommandLineParser clp = new CommandLineParser();

        clp.validateRegisCLA(args);

        Registry registry = null;
        try {
            registry = new Registry(clp.port_num);
        } catch (IOException e) {
            System.out.println("Unable to start registry!");
            e.printStackTrace();
        }


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

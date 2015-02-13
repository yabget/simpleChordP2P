package node;

import routing.RoutingTable;
import transport.TCPConnection;
import transport.TCPConnectionsCache;
import transport.TCPServerThread;
import util.CommandLineParser;
import util.RegistryCommandsParser;
import util.StatisticsCollectorAndDisplay;
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

    private int routingTableSize = 3; //Default routing table size
    private int numCompletedNodes = 0; // Number of nodes that have completed sending messages
    private int numTrafficReceived = 0; //Number of nodes that have send back traffic summary

    private int serverPort;

    private Hashtable<Integer, MessagingNode> messNode; //Keeps track of nodes by ID
    private List<Integer> unUsedKeys; //List of available node IDs
    private TCPConnectionsCache tcpCC;

    public Registry(int port) throws IOException {
        intializeContainers();

        this.serverPort = port;

        // Start server on given port
        Thread serverThread = new Thread(new TCPServerThread(this, new ServerSocket(serverPort)));
        serverThread.start();

        // Generate array of unused IDs up to max ID
        unUsedKeys = new ArrayList<>();
        for(int i = 0; i < MAX_NODE_ID; i++){
            unUsedKeys.add(i);  //Populate array with IDs (0 - 127)
        }
    }

    private void intializeContainers(){
        messNode = new Hashtable<>();
        tcpCC = new TCPConnectionsCache();
    }

    // ----------------- Foreground commands ------------------- //
    /**
     * Lists the messaging nodes currently in the registry
     */
    public synchronized void list_messaging_nodes(){
        int count = 1;
        for(Integer i : messNode.keySet()){
            System.out.println(count + ":\t" + messNode.get(i));
            count++;
        }
    }

    /**
     * Registry sends node manifest to each of the nodes that are registered
     * @param size_table - the size of the routing table at each meassaging node
     */
    public void setup_overlay(int size_table){
        routingTableSize = size_table;

        ArrayList<Integer> sortedIDs = new ArrayList<Integer>();
        for(Integer nodeID : messNode.keySet()){
            sortedIDs.add(nodeID);
        }
        Collections.sort(sortedIDs);

        for(int i= 0; i < sortedIDs.size(); i++){

            int currID = sortedIDs.get(i);

            RoutingTable rTable = RoutingTable.getRoutingTableForIndex(i, routingTableSize, sortedIDs, tcpCC);

            MessagingNode tempMNode = messNode.get(currID);

            tempMNode.setRoutingTable(rTable);

            RegistrySendsNodeManifest rsnm = new RegistrySendsNodeManifest(rTable, new ArrayList<>(messNode.keySet()));

            tcpCC.sendEvent(currID, rsnm);
        }
    }

    /**
     * Lists the routing table assigned to each messaging node
     */
    public void list_routing_tables(){
        for(Integer id : messNode.keySet()){
            messNode.get(id).printRoutingTable();
        }
    }

    /**
     * Sends a task initiate message to all the registered nodes
     * @param numMessagesToSend - the number of messages nodes should send between each other
     */
    public void start(int numMessagesToSend){
        RegistryRequestsTaskInitiate rrti = new RegistryRequestsTaskInitiate(numMessagesToSend);
        sendToAllNodes(rrti);
    }

    // ----------------- Helper methods ------------------- //
    /**
     * Uses the unUsedKeys list to find a random key, the key is removed from the list of unUsedKeys
     * @return a random ID
     */
    private synchronized int getRandomID(){
        Random rand = new Random();

        //If all keys have been used
        if(unUsedKeys.size() == 0){
            return -1;
        }

        int randomIndex = rand.nextInt(unUsedKeys.size());
        int assignedID = unUsedKeys.get(randomIndex);
        unUsedKeys.remove(randomIndex); // Remove the random key from the list

        return assignedID;
    }

    /**
     * Sends an Event to all of the registered nodes
     * @param event - The event to send
     */
    private void sendToAllNodes(Event event){
        for(Integer id : messNode.keySet()){
            tcpCC.sendEvent(id, event);
        }
    }

    // ----------------- onEvent actions ------------------- //

    /**
     * When a messaging node sends it's registration request,
     * - Assign the node a random ID
     * - Put the node in a hashtable of <ID, Node>
     * - Add the TCPConnection into the cache
     * @param onsr - OverlayNodeSendsRegistration event
     */
    private void handleOverlayNodeSendsRegistration(OverlayNodeSendsRegistration onsr){
        int assignedID = getRandomID();

        if(assignedID == -1){
            System.out.println("NEVER COMMUNICATED WITH THIS NODE BEFORE. Id is -1. Not adding node.");
            return;
        }

        try {
            tcpCC.addNewConn(assignedID, new TCPConnection(new Socket(onsr.getIpAddr(), onsr.getPort()), this));
            messNode.put(assignedID, new MessagingNode(assignedID, onsr.getIpAddr(), onsr.getPort()));
        } catch (IOException e) {
            System.out.println("Problem in onEvent of Registry, cannot make connection with node. Not adding node.");
            return;
        }

        String infoStr = "Registration request successful. The number of messaging nodes " +
                "currently constituting the overlay is ("+ messNode.size() +")";

        RegistryReportsRegistrationStatus regRRS = new RegistryReportsRegistrationStatus(assignedID, infoStr.toString());

        tcpCC.sendEvent(assignedID, regRRS);
    }

    /**
     * When a messaging node reports successful setup of their routing tables,
     * If everything is successful for the messaging nodes, the registry is ready to initiate tasks
     * @param nross - NodeReportsOverlaySetupStatus event
     */
    private void handleNodeReportsOverlaySetupStatus(NodeReportsOverlaySetupStatus nross){
        if(nross.isSuccessful()){
            System.out.println("Registry now ready to initiate tasks with " + nross.getSuccessStatus());
            return;
        }
        System.out.println("Messaging nodes had trouble setting up overlay.");
    }

    /**
     * When a messaging node sends x number of messages, it reports to the registry,
     * Once all messaging nodes have reported to the registry ...
     * The registry then requests the traffic summary from all nodes after waiting for 20 seconds
     * to account for packets that might still be in transit
     * @param onrtf - OverlayNodeReportsTaskFinished event
     */
    private synchronized void handleOverlayNodeReportsTaskFinished(OverlayNodeReportsTaskFinished onrtf){
        numCompletedNodes++;
        System.out.println(numCompletedNodes + " Finished");

        int numNodesInOverlay = messNode.size();

        if(numCompletedNodes == numNodesInOverlay){
            RegistryRequestsTrafficSummary rrts = new RegistryRequestsTrafficSummary();
            try {
                System.out.println("Counting to 20 seconds!");
                for(int i=0; i< 20; i++){
                    System.out.println("Second " + (i+1));
                    Thread.sleep(1000);
                }
                System.out.println("Now asking for traffic summary from all nodes.");

            } catch (InterruptedException e) {
                System.out.println("Oh No! Somehow I have a problem sleeping for 20 seconds.");
            }
            sendToAllNodes(rrts);
        }
    }

    /**
     * As each node reports it's summary, registry stores those values in the corresponding entry
     * Once all the messaging nodes have reported, messaging node prints the traffic summary
     * @param onrts - OverlayNodeReportsTrafficSummary event
     */
    private synchronized void handleOverlayNodeReportsTrafficSummary(OverlayNodeReportsTrafficSummary onrts){
        MessagingNode currMNode = messNode.get(onrts.getNodeID());

        currMNode.setSendTracker(onrts.getTotalSent());
        currMNode.setRelayTracker(onrts.getTotalRelayed());
        currMNode.setSendSummation(onrts.getSumSent());
        currMNode.setReceiveTracker(onrts.getTotalReceived());
        currMNode.setReceiveSummation(onrts.getSumReceived());

        numTrafficReceived++;

        int numNodesInOverlay = messNode.size();
        if(numTrafficReceived == numNodesInOverlay){
            StatisticsCollectorAndDisplay.printTrafficSummaryForAll(messNode);
        }
    }

    /**
     * When a messaging node sends a deregistration request, Registry repsonds with message,
     * then the node terminates
     * - Node is removed form TCPConnectionCache
     * - Node is removed from Hashtable of <ID, MessagingNodes>
     * - The node ID is added back into the unUsedKeys
     * @param onsdereg
     */
    private void handleOverlayNodeSendsDeregistration(OverlayNodeSendsDeregistration onsdereg){
        System.out.println("Deregistering " + onsdereg);

        RegistryReportsDeregistrationStatus rrdergs = new RegistryReportsDeregistrationStatus();

        tcpCC.sendEvent(onsdereg.getNodeID(), rrdergs);

        if(tcpCC.removeConn(onsdereg.getNodeID())){
            System.out.println("Deregistration successful!");
            synchronized (messNode){
                messNode.remove(onsdereg.getNodeID());
            }
            unUsedKeys.add(onsdereg.getNodeID());
        }
        else{
            System.out.println("Deregistration UNSUCCESFUL!");
        }
    }

    /**
     * When a messaging node sends an event to the registry,
     * depending on the type of event, the registry responds accordingly
     * @param event - The event sent by the messaging node
     */
    @Override
    public void onEvent(Event event) {

        if(event == null){
            System.out.println("EVENT IS NULL! Something went wrong here.");
            return;
        }

        byte eventType = event.getType();

        switch (eventType){
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                OverlayNodeSendsRegistration onsr = (OverlayNodeSendsRegistration) event;
                handleOverlayNodeSendsRegistration(onsr);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                NodeReportsOverlaySetupStatus nross = (NodeReportsOverlaySetupStatus) event;
                handleNodeReportsOverlaySetupStatus(nross);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                OverlayNodeReportsTaskFinished onrtf = (OverlayNodeReportsTaskFinished) event;
                handleOverlayNodeReportsTaskFinished(onrtf);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                OverlayNodeReportsTrafficSummary onrts = (OverlayNodeReportsTrafficSummary) event;
                handleOverlayNodeReportsTrafficSummary(onrts);
                break;
            case Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION:
                OverlayNodeSendsDeregistration onsdereg = (OverlayNodeSendsDeregistration) event;
                handleOverlayNodeSendsDeregistration(onsdereg);
                break;
        }

    }

    public static void main(String args[]){
        CommandLineParser clp = new CommandLineParser();

        clp.validateRegisCLA(args); //Parse arguments for registry

        Registry registry = null;
        try {
            registry = new Registry(clp.port_num);
        } catch (IOException e) {
            System.out.println("Unable to start registry! Program is exiting.");
            return;
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
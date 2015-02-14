package node;

import routing.RoutingEntry;
import routing.RoutingTable;
import transport.TCPConnection;
import transport.TCPConnectionsCache;
import transport.TCPServerThread;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node {

    // Messaging node properties
    private int ID;
    private String ipAddress;
    private int port;

    private RoutingTable routingTable;
    private ArrayList<Integer> allOtherMNodes;
    private TCPConnectionsCache tcpCC;

    private TCPConnection registryConnection = null;
    private OverlayNodeReportsTrafficSummary onodeRepTraffSum;

    public int sendTracker;
    public int relayTracker;
    public int receiveTracker;

    public long sendSummation;
    public long receiveSummation;

    private Queue<OverlayNodeSendsData> sendQueue;

    /**
     * Constructor that is used to start the messaging node independently
     * @param registry_ip - IP address of the registry to connect to
     * @param registry_port - ServerPort of registry
     * @throws IOException
     */
    public MessagingNode(String registry_ip, int registry_port) throws IOException {
        initializeContainers();
        resetCounters();

        // Start server thread
        ServerSocket serverSocket = new ServerSocket(0);
        Thread serverThread = new Thread(new TCPServerThread(this, serverSocket));
        serverThread.start();

        // Connect with registry
        registryConnection = new TCPConnection(new Socket(registry_ip, registry_port), this);
        registryConnection.startReceiveThread();

        // Store IP and port information
        ipAddress = InetAddress.getLocalHost().getHostAddress();
        port = serverSocket.getLocalPort();

        // Send registration to Registry
        OverlayNodeSendsRegistration sendReg = new OverlayNodeSendsRegistration(ipAddress, port);
        registryConnection.sendData(sendReg.getBytes());


        // Initialize sending queue between messaging nodes
        sendQueue = new LinkedList<>();

        Thread sendQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    OverlayNodeSendsData relayMsg;
                    synchronized (sendQueue){
                        relayMsg = sendQueue.poll();
                    }
                    if(relayMsg != null){
                        int bestNodeToSend = routingTable.determineBestNode(relayMsg.getDestinationID());
                        tcpCC.sendEvent(bestNodeToSend, relayMsg);
                    }
                }
            }
        });
        sendQueueThread.start();
    }

    /**
     * Constructor used by registry to keep track of nodes
     * @param nodeID - The assigned messaging nodeID
     * @param ipAddress - The ipAddress of the messaging node / TCPConnection
     * @param port - The port for the messaging node
     */
    public MessagingNode(int nodeID, String ipAddress, int port){
        initializeContainers();
        resetCounters();
        this.ID = nodeID;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    private void initializeContainers(){
        routingTable = new RoutingTable();
        allOtherMNodes = new ArrayList<>();
        tcpCC = new TCPConnectionsCache();
    }

    // ----------------- Foreground commands ------------------- //
    public void print_counters_and_diagnostics(){
        System.out.println("SendT\trecvT\trelyT\t\tSSum\t\tRSum");
        System.out.println(getTrafficSummary());
    }

    public void sendDeregistration(){
        OverlayNodeSendsDeregistration sendDereg = new OverlayNodeSendsDeregistration(ipAddress, port, ID);
        registryConnection.sendData(sendDereg.getBytes());
    }

    // ----------------- Getters and setters ------------------- //
    /**
     * Assign this messaging node it's routing table
     * @param rt - The routing table to be assigned to this node
     */
    public void setRoutingTable(RoutingTable rt){
        routingTable = rt;
    }

    /**
     * Sets the list of other nodes but first removes this node from the list
     * @param allOtherMNodes - All other nodes in the overlay
     */
    public void setAllOtherMNodes(ArrayList<Integer> allOtherMNodes){
        allOtherMNodes.remove(new Integer(ID)); //Remove self from the list of nodes to send to
        System.out.println("\nAll other nodes: " + allOtherMNodes);
        this.allOtherMNodes = allOtherMNodes;
    }

    /**
     * Used for easier printing of traffic summary
     * @param onodeRepTraffSum - OverlayNodeReportsTrafficSummary event
     */
    public void setOverlayNodeRepTraffSum(OverlayNodeReportsTrafficSummary onodeRepTraffSum) {
        this.onodeRepTraffSum = onodeRepTraffSum;
    }

    public OverlayNodeReportsTrafficSummary getOnodeRepTraffSum() {
        return onodeRepTraffSum;
    }

    /**
     * Returns the stats for
     * @return
     */
    public String getTrafficSummary(){
        return onodeRepTraffSum.getTotalSent() + "\t\t" +
                onodeRepTraffSum.getTotalReceived() + "\t\t" +
                onodeRepTraffSum.getTotalRelayed() + "\t\t" +
                onodeRepTraffSum.getSumSent() + "\t\t" +
                onodeRepTraffSum.getSumReceived();
    }

    private void setID(int id){
        this.ID = id;
    }

    // ----------------- Helper methods ------------------- //
    /**
     * Prints the routing table for the messaging node (Invoked by registry)
     */
    public void printRoutingTable(){
        System.out.println("Printing routing table for: " + ID);
        for(RoutingEntry rEntry : routingTable.getEntries()){
            System.out.println(rEntry);
        }
        System.out.println();
    }

    /**
     * Before and after node starts sending messages to other nodes, counters are reset
     */
    public void resetCounters(){
        sendTracker = 0;
        relayTracker = 0;
        sendSummation = 0;
        receiveTracker = 0;
        receiveSummation = 0;
    }

    /**
     * Sends packets to nodes picked at random from the list of nodes
     * @param numPacketsToSend - The number of packets to send
     */
    private synchronized void startSendingToNodes(int numPacketsToSend){
        Random rand = new Random();
        int numNodes = allOtherMNodes.size();
        ArrayList<Integer> trace;
        System.out.println("Sending " + numPacketsToSend);

        for(int i=0; i < numPacketsToSend; i++){
            int payload = rand.nextInt(); // Get a random payload
            int destinationID = allOtherMNodes.get(rand.nextInt(numNodes)); //Pick destination

            sendSummation += payload; //Sum the payload

            trace = new ArrayList<>();
            trace.add(ID); //Add self to trace

            OverlayNodeSendsData onsd = new OverlayNodeSendsData(destinationID, ID, payload, trace);

            synchronized (sendQueue){
                sendQueue.add(onsd); //Add to queue to be sent
            }
            sendTracker++;
        }
        System.out.println("Sent all packets");

        OverlayNodeReportsTaskFinished onrtf = new OverlayNodeReportsTaskFinished(ipAddress, port, ID);
        registryConnection.sendData(onrtf.getBytes()); //Notify registry that task is completed
    }

    public String toString(){
        return ID + "\t" + ipAddress + "\t" + port;
    }

    // ----------------- onEvent actions ------------------- //
    /**
     * Messaging node receives and stores the ID it receives from the registry
     * @param rrrs - RegistryReportsRegistrationStatus event
     */
    private void handleRegRepRegistrationStatus(RegistryReportsRegistrationStatus rrrs){
        setID(rrrs.getAssignedID());
        System.out.println(rrrs.getInfoString());
        System.out.println("I am " + ID);
    }

    /**
     * Receives routing table information from registry, stores it in the routing table
     * @param rsnm - RegistrySendsNodeManifest event
     */
    private void handleRegistrySendsNodeManifest(RegistrySendsNodeManifest rsnm){
        setRoutingTable(rsnm.getRoutingTable()); //Stores the routing table
        setAllOtherMNodes(rsnm.getAllNodeIDs()); //Get list of all nodes

        for(RoutingEntry routingEntry : routingTable.getEntries()){
            routingEntry.addSelfToTCPConnectionCache(tcpCC); //Connect with nodes in routing table
        }

        String successString = "HEY! I am " + ID + "! I connected with everyone.";

        NodeReportsOverlaySetupStatus nross = new NodeReportsOverlaySetupStatus(ID, successString);

        printRoutingTable();

        registryConnection.sendData(nross.getBytes());
    }

    /**
     * When another messaging node sends data, either relay the packet or accept it
     * @param onsd - OverlayNodeSendsData event
     */
    private synchronized void handleOverlayNodeSendsData(OverlayNodeSendsData onsd){
        if(onsd.amInTrace(ID)){
            System.out.println("SOMETHING WENT WRONG, I GOT PACKET AGAIN (i.e. I'm already in trace.");
            return;
        }

        //If i'm not the destination
        if(!onsd.isDestination(ID)){
            onsd.addToTrace(ID); //Add myself to the trace
            synchronized (sendQueue){
                sendQueue.add(onsd); //Add message to queue
            }
            relayTracker++;
            return;
        }

        int payload = onsd.getPayload();
        receiveSummation += payload;
        receiveTracker++;
    }

    /**
     * When a messaging node or the registry sends an event to this node,
     * respond accordingly depending on what the event is
     * @param event - The event sent by the messaging node or registry
     */
    @Override
    public void onEvent(Event event) {
        if(event == null){
            System.out.println("Messaging node EVENT IS NULL!");
        }
        byte eventType = event.getType();

        switch (eventType){
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                RegistryReportsRegistrationStatus rrrs = (RegistryReportsRegistrationStatus) event;
                handleRegRepRegistrationStatus(rrrs);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                RegistrySendsNodeManifest rsnm = (RegistrySendsNodeManifest) event;
                handleRegistrySendsNodeManifest(rsnm);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                RegistryRequestsTaskInitiate rrti = (RegistryRequestsTaskInitiate) event;
                startSendingToNodes(rrti.getNumPacketsToSend());
                break;
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                OverlayNodeSendsData onsd = (OverlayNodeSendsData) event;
                handleOverlayNodeSendsData(onsd);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                OverlayNodeReportsTrafficSummary onrts = new OverlayNodeReportsTrafficSummary(
                        ID, sendTracker, relayTracker, sendSummation, receiveTracker, receiveSummation
                );
                registryConnection.sendData(onrts.getBytes());
                resetCounters();
                break;
            case Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS:
                System.out.println("Awesome! I am deregistered! Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unrecognized event was received.");
        }
    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        System.out.printf("Connecting to %s on port %s ...\n", clp.ip_addr, clp.port_num);

        try {
            MessagingNode mNode = new MessagingNode(clp.ip_addr, clp.port_num);

            //Start parsing commands
            MNodeCommandParser mNodeCP = new MNodeCommandParser(mNode);
            Scanner scan = new Scanner(System.in);

            String input;

            System.out.print("Command: ");
            while((input = scan.nextLine()) != null){
                mNodeCP.parseArgument(input);
                System.out.print("Command: ");
            }
        } catch (IOException e) {
            System.out.println("Unable to start messaging node");
            System.exit(-1);
        }
    }

}
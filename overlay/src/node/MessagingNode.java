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
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node {

    // Messaging node properties
    private int ID;
    private String myIP;
    private int myServerPort;

    private RoutingTable routingTable;
    private ArrayList<Integer> allOtherMNodes;
    private TCPConnectionsCache tcpCC;

    private TCPConnection registryConnection = null;

    private int sendTracker = 0;
    private int relayTracker = 0;
    private int receiveTracker = 0;

    private long sendSummation = 0;
    private long receiveSummation = 0;


    private Thread sendQueueThread = null;
    Queue<OverlayNodeSendsData> sendQ;

    public MessagingNode(String registry_ip, int registry_port) throws IOException {
        initializeContainers();

        // Start server thread
        ServerSocket serverSocket = new ServerSocket(0);
        Thread serverThread = new Thread(new TCPServerThread(this, serverSocket));
        serverThread.start();

        // Connect with registry
        registryConnection = new TCPConnection(new Socket(registry_ip, registry_port), this);
        registryConnection.startReceiveThread();

        myIP = InetAddress.getLocalHost().getHostAddress();
        myServerPort = serverSocket.getLocalPort();

        OverlayNodeSendsRegistration sendReg = new OverlayNodeSendsRegistration(myIP, myServerPort);

        registryConnection.sendData(sendReg.getBytes());

        sendQ = new ConcurrentLinkedQueue<>();

        sendQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(sendQ.size() > 0){
                        OverlayNodeSendsData onsd = sendQ.remove();
                        int bestNode = routingTable.determineBestNode(onsd.getDestinationID());

                        tcpCC.sendEvent(bestNode, onsd);
                    }
                }

            }
        });
        sendQueueThread.start();
    }

    public MessagingNode(int nodeID){
        initializeContainers();
        this.ID = nodeID;
    }

    private void initializeContainers(){
        routingTable = new RoutingTable();
        allOtherMNodes = new ArrayList<>();
        tcpCC = new TCPConnectionsCache();
    }

    public int getSendTracker() {
        return sendTracker;
    }

    public int getReceiveTracker() {
        return receiveTracker;
    }

    public int getRelayTracker() {
        return relayTracker;
    }

    public long getSendSummation() {
        return sendSummation;
    }

    public long getReceiveSummation() {
        return receiveSummation;
    }

    public void setSendTracker(int sendTracker) {
        this.sendTracker = sendTracker;
    }

    public void setReceiveTracker(int receiveTracker) {
        this.receiveTracker = receiveTracker;
    }

    public void setRelayTracker(int relayTracker) {
        this.relayTracker = relayTracker;
    }

    public void setSendSummation(long sendSummation) {
        this.sendSummation = sendSummation;
    }

    public void setReceiveSummation(long receiveSummation) {
        this.receiveSummation = receiveSummation;
    }

    public void setRoutingTable(RoutingTable rt){
        routingTable = rt;
    }

    public void setAllOtherMNodes(ArrayList<Integer> allOtherMNodes){
        allOtherMNodes.remove(new Integer(ID));
        System.out.println("All others: " + allOtherMNodes);
        this.allOtherMNodes = allOtherMNodes;
    }

    public void printRoutingTable(){
        System.out.println("Printing routing table for: " + ID);
        for(RoutingEntry rEntry : routingTable.getEntries()){
            System.out.println(rEntry);
        }
        System.out.println();
    }

    public String getTrafficSummary(){
        return sendTracker + "\t\t" + receiveTracker + "\t\t" + relayTracker + "\t\t" + sendSummation + "\t\t" + receiveSummation;
    }

    public void print_counters_and_diagnostics(){
        System.out.println("SendT\trecvT\trelyT\t\tSSum\t\tRSum");
        System.out.println(getTrafficSummary());
    }

    private void setID(int id){
        this.ID = id;
    }

    public String toString(){
        return ID + " ";
    }

    private void startSendingToNodes(int numPacketsToSend){
        Random rand = new Random();
        int numNodes = allOtherMNodes.size();
        ArrayList<Integer> trace;
        System.out.println("Sending " + numPacketsToSend);



        for(int i=0; i < numPacketsToSend; i++){
            int payload = rand.nextInt();

            int randNodeIDIndex = rand.nextInt(numNodes);

            int destinationID;

            destinationID = allOtherMNodes.get(randNodeIDIndex);

            sendSummation += payload;

            trace = new ArrayList<>();
            trace.add(ID);


            OverlayNodeSendsData onsd = new OverlayNodeSendsData(destinationID, ID, payload, trace);

            synchronized (sendQ){
                sendQ.add(onsd);
            }

            //System.out.println("[SEND " +  i + "]: " + onsd);

            sendTracker++;

        }

        System.out.println("Sent all packets");

        if(sendTracker != numPacketsToSend){
            System.out.println("DIDN'T SEND AS MANY PACKETS AS I SHOULD HAVE");
        }

        OverlayNodeReportsTaskFinished onrtf = new OverlayNodeReportsTaskFinished(myIP, myServerPort, ID);

        registryConnection.sendData(onrtf.getBytes());
    }

    @Override
    public synchronized void onEvent(Event event) {
        if(event == null){
            System.out.println("Messaging node EVENT IS NULL!");
        }
        byte eventType = event.getType();
        if(eventType == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS){
            RegistryReportsRegistrationStatus rrRS = (RegistryReportsRegistrationStatus) event;
            setID(rrRS.getAssignedID());
            System.out.println(rrRS.getInfoString());
            System.out.println("I am " + ID);

        }
        else if(eventType == Protocol.REGISTRY_SENDS_NODE_MANIFEST){
            RegistrySendsNodeManifest rsnm = (RegistrySendsNodeManifest) event;
            setRoutingTable(rsnm.getRoutingTable());
            setAllOtherMNodes(rsnm.getAllNodeIDs());

            for(RoutingEntry routingEntry : routingTable.getEntries()){
                //Connection to each entry
                routingEntry.addSelfToTCPConnectionCache(tcpCC);
            }

            String successString = "HEY! I am " + ID + "! I connected with everyone.";

            NodeReportsOverlaySetupStatus nross = new NodeReportsOverlaySetupStatus(ID, successString);

            printRoutingTable();

            registryConnection.sendData(nross.getBytes());
        }
        else if(eventType == Protocol.REGISTRY_REQUESTS_TASK_INITIATE){
            RegistryRequestsTaskInitiate rrti = (RegistryRequestsTaskInitiate) event;

            startSendingToNodes(rrti.getNumPacketsToSend());

        }
        else if(eventType == Protocol.OVERLAY_NODE_SENDS_DATA){

            OverlayNodeSendsData onsd = (OverlayNodeSendsData) event;

            if(onsd.amInTrace(ID)){
                System.out.println("SOMETHING WENT WRONG, I GOT PACKET AGAIN (i.e. I'm already in trace.");
                return;
            }

            //If i'm not the destination
            if(!onsd.isDestination(ID)){


                onsd.addToTrace(ID);
                int bestNode = routingTable.determineBestNode(onsd.getDestinationID());

                tcpCC.sendEvent(bestNode, onsd);
                //System.out.println("[RELAYED-]: " + onsd);
                relayTracker++;
                return;
            }

            int payload = onsd.getPayload();
            receiveSummation += payload;
            receiveTracker++;

            //System.out.println("[RECEIVED]: " + onsd);
        }
        else if(eventType == Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY){
            OverlayNodeReportsTrafficSummary onrts = new OverlayNodeReportsTrafficSummary(
                    ID, sendTracker, relayTracker, sendSummation, receiveTracker, receiveSummation
            );

            registryConnection.sendData(onrts.getBytes());

            sendTracker = 0;
            relayTracker = 0;
            sendSummation = 0;
            receiveTracker = 0;
            receiveSummation = 0;
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
            e.printStackTrace();
            System.exit(-1);
        }



    }

}
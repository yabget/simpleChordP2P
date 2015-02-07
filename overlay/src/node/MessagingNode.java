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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node {

    private int ID;
    private RoutingTable routingTable;
    private ArrayList<Integer> allOtherMNodes;
    private TCPConnection registryConnection = null;
    private TCPConnectionsCache tcpCC;
    private ServerSocket serverSocket;

    private int sendTracker = 0;
    private int receiveTracker = 0;

    private int relayTracker = 0;

    private long sendSummation = 0;
    private long receiveSummation = 0;

    public MessagingNode(String registry_ip, int registry_port) throws IOException {
        routingTable = new RoutingTable();
        allOtherMNodes = new ArrayList<>();
        tcpCC = new TCPConnectionsCache();

        serverSocket = new ServerSocket(0);
        startServer(0);

        Socket regSocket = new Socket(registry_ip, registry_port);
        registryConnection = new TCPConnection(regSocket, this);
        registryConnection.startReceiveThread();

        OverlayNodeSendsRegistration sendReg = new OverlayNodeSendsRegistration(
                regSocket.getLocalAddress().getHostAddress(), serverSocket.getLocalPort()
        );

        registryConnection.sendData(sendReg.getBytes());
    }

    public MessagingNode(int nodeID){
        this.ID = nodeID;
        routingTable = new RoutingTable();
        allOtherMNodes = new ArrayList<>();
        tcpCC = new TCPConnectionsCache();
    }

    public MessagingNode(){
        routingTable = new RoutingTable();
        allOtherMNodes = new ArrayList<>();
        tcpCC = new TCPConnectionsCache();
    }

    public void setRoutingTable(RoutingTable rt){
        routingTable = rt;
    }

    public void setAllOtherMNodes(ArrayList<Integer> allOtherMNodes){
        this.allOtherMNodes = allOtherMNodes;
    }

    public void printRoutingTable(){
        System.out.println("Printing routing table for: " + ID);
        for(RoutingEntry rEntry : routingTable.getEntries()){
            System.out.println(rEntry);
        }
        System.out.println();
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

        for(int i=0; i < numPacketsToSend; i++){

            int payload = rand.nextInt();

            int randNodeIDIndex = rand.nextInt(numNodes);

            int destinationID = allOtherMNodes.get(randNodeIDIndex);

            sendSummation += payload;

            OverlayNodeSendsData onsd = new OverlayNodeSendsData(destinationID, ID, payload, new ArrayList<Integer>());

            sendToBestNode(destinationID, onsd);
            sendTracker++;

        }
    }

    private void sendToBestNode(int destinationID, OverlayNodeSendsData onsd) {
        int bestNode = routingTable.determineBestNode(destinationID);

        tcpCC.sendEvent(bestNode, onsd);
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

            System.out.print(tcpCC);

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
                sendToBestNode(onsd.getDestinationID(), onsd);
                relayTracker++;
                return;
            }

            int payload = onsd.getPayload();

            receiveSummation += payload;
            receiveTracker++;
        }
    }

    @Override
    public void startServer(int portNumber) {
        Thread serverThread = new Thread(new TCPServerThread(this, serverSocket));
        serverThread.start();
    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        String registry_host = clp.ip_addr;
        int registry_port = clp.port_num;

        System.out.printf("Connecting to %s on port %s ...\n", registry_host, registry_port);

        try {
            MessagingNode mNode = new MessagingNode(registry_host, registry_port);
        } catch (IOException e) {
            System.out.println("Unable to start messaging node");
            e.printStackTrace();
        }

        //Start parsing commands
        MNodeCommandParser mNodeCP = new MNodeCommandParser();
        Scanner scan = new Scanner(System.in);
        String input;

        System.out.print("Command: ");
        while((input = scan.nextLine()) != null){
            mNodeCP.parseArgument(input);
            System.out.print("Command: ");
        }

    }

}
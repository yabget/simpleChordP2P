package node;

import routing.RoutingEntry;
import routing.RoutingTable;
import transport.TCPConnection;
import transport.TCPServerThread;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.Event;
import wireformats.Protocol;
import wireformats.RegistryReportsRegistrationStatus;
import wireformats.RegistrySendsNodeManifest;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node, Runnable {

    private int ID;
    private Socket socket;
    private TCPConnection tcpC;
    private RoutingTable routingTable;
    private Set<Integer> allOtherMNodes;

    public MessagingNode(int nodeID, Socket socket){
        this.ID = nodeID;
        this.socket = socket;
        this.tcpC = new TCPConnection(socket);
        routingTable = new RoutingTable();
        allOtherMNodes = new HashSet<>();
    }

    public void setRoutingTable(RoutingTable rt){
        routingTable = rt;
    }

    public void setAllOtherMNodes(Set<Integer> allOtherMNodes){
        this.allOtherMNodes = allOtherMNodes;
    }

    public void printRoutingTable(){
        System.out.println("Printing routing table for: " + ID);
        for(RoutingEntry rEntry : routingTable.getEntries()){
            System.out.println(rEntry);
        }
        System.out.println();
    }

    public TCPConnection getTCPC(){
        return tcpC;
    }

    @Override
    public void run() {
        //Start thread to communicate with registry
        new Thread(new TCPServerThread(socket, this)).start();
    }

    public int getID(){
        return ID;
    }

    public Socket getSocket(){
        return socket;
    }

    public boolean equals(Object other){
        if(other == null || !(other instanceof MessagingNode)){
            return false;
        }
        return getSocket().equals(((MessagingNode) other).getSocket());
    }

    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() +
                " " + socket.getPort() + " " + getID();
    }

    private void setID(int id){
        this.ID = id;
    }


    @Override
    public synchronized Event onEvent(Event event) {
        if(event == null){
            System.out.println("Messaging node EVENT IS NULL!");
            return null;
        }
        byte eventType = event.getType();
        if(eventType == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS){
            RegistryReportsRegistrationStatus rrRS = (RegistryReportsRegistrationStatus) event;
            setID(rrRS.getAssignedID());
            System.out.println(rrRS.getInfoString());
            return null;
        }
        else if(eventType == Protocol.REGISTRY_SENDS_NODE_MANIFEST){
            RegistrySendsNodeManifest rsnm = (RegistrySendsNodeManifest) event;
            setRoutingTable(rsnm.getRoutingTable());
            setAllOtherMNodes(rsnm.getAllNodeIDs());

            printRoutingTable();
            for(Integer nodeID : allOtherMNodes){
                System.out.print(" " + nodeID + " ");
            }
            System.out.println();

        }
        return null;
    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        String registry_host = clp.ip_addr;
        int registry_port = clp.port_num;

        System.out.printf("Connecting to %s on port %s ...\n", registry_host, registry_port);

        try {
            MessagingNode mNode = new MessagingNode(-1, new Socket(registry_host, registry_port));
            Thread mNodeThread = new Thread(mNode);
            mNodeThread.start();

            //Start parsing commands
            MNodeCommandParser mNodeCP = new MNodeCommandParser();
            Scanner scan = new Scanner(System.in);
            String input;

            while((input = scan.nextLine()) != null){

            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("ERROR! Could not open socket correctly.");

            e.printStackTrace();
        }
    }

}

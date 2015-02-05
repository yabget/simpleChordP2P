package node;

import routing.RoutingEntry;
import routing.RoutingTable;
import transport.TCPConnection;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.Event;
import wireformats.Protocol;
import wireformats.RegistryReportsRegistrationStatus;
import wireformats.RegistrySendsNodeManifest;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node {

    private int ID;
    private RoutingTable routingTable;
    private Set<Integer> allOtherMNodes;

    public MessagingNode(String registry_ip, int registry_port){

    }

    public MessagingNode(){
        routingTable = new RoutingTable();
        allOtherMNodes = new HashSet<>();
    }

    public MessagingNode(int nodeID){
        this.ID = nodeID;
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

    public int getID(){
        return ID;
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

    @Override
    public void startServer(int portNumber) {

    }

    @Override
    public void addConnection(TCPConnection tcpC) {

    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        String registry_host = clp.ip_addr;
        int registry_port = clp.port_num;

        System.out.printf("Connecting to %s on port %s ...\n", registry_host, registry_port);

        MessagingNode mNode = new MessagingNode(registry_host, registry_port);

        //Start parsing commands
        MNodeCommandParser mNodeCP = new MNodeCommandParser();
        Scanner scan = new Scanner(System.in);
        String input;

        while((input = scan.nextLine()) != null){

        }

    }

}
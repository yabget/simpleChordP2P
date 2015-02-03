package node;

import transport.TCPConnection;
import transport.TCPServerThread;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.Event;
import wireformats.Protocol;
import wireformats.RegistryReportsRegistrationStatus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node, Runnable {

    private int port;
    private String ip;
    private int ID;
    private Socket socket;
    private TCPConnection tcpC;
    private String registry_ip;
    private int registry_port;

    public MessagingNode(String myIP, int myPort, int myID){
        this.ip = myIP;
        this.port = myPort;
        this.ID = myID;
    }

    public MessagingNode(String registry_ip, int registry_port) {
        this.registry_ip = registry_ip;
        this.registry_port = registry_port;
    }

    @Override
    public void run() {
        try {
            //Start thread to communicate with registry
            socket = new Socket(registry_ip, registry_port);
            new Thread(new TCPServerThread(socket, this)).start();
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public int getID(){
        return ID;
    }

    @Override
    public String toString() {
        return this.ip + " " + this.port + " " + this.ID;
    }

    public boolean equals(Object object){
        if(object == null || !(object instanceof MessagingNode)){
            return false;
        }
        return false;
    }

    @Override
    public synchronized Event onEvent(Event event) {
        if(event == null){
            System.out.println("Messaging node EVENT IS NULL!");
            return null;
        }
        if(event.getType() == Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS){
            RegistryReportsRegistrationStatus rrRS = (RegistryReportsRegistrationStatus) event;
            this.ID = rrRS.getAssignedID();
            System.out.println(rrRS.getInfoString());
            return null;
        }
        return null;
    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        String registry_host = clp.ip_addr;
        int registry_port = clp.port_num;

        System.out.printf("Connecting to %s on port %s ...\n", registry_host, registry_port);

        MessagingNode mNode = new MessagingNode(registry_host, registry_port);

        Thread mNodeThread = new Thread(mNode);

        mNodeThread.start();

        try {
            InetAddress inetA = InetAddress.getLocalHost();
            MNodeCommandParser mNodeCP = new MNodeCommandParser(inetA.getHostName(), inetA.getHostAddress());

            Scanner scan = new Scanner(System.in);
            String input;

            while((input = scan.nextLine()) != null){

            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}

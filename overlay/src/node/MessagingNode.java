package node;

import transport.TCPConnection;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.Event;
import wireformats.EventFactory;
import wireformats.OverlayNodeSendsRegistration;
import wireformats.RegistryReportsRegistrationStatus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode implements Node {

    private int port;
    private String ip;
    private int ID;
    private Socket socket;
    private TCPConnection tcpC;

    public MessagingNode(String myIP, int myPort, int myID){
        this.ip = myIP;
        this.port = myPort;
        this.ID = myID;
    }

    public int getID(){
        return ID;
    }

    public MessagingNode(String registry_ip, int registry_port) {
        try{
            socket = new Socket(registry_ip, registry_port);
            tcpC = new TCPConnection(socket);
            EventFactory ef = EventFactory.getInstance();

            OverlayNodeSendsRegistration sendReg = new OverlayNodeSendsRegistration(socket.getLocalAddress().getHostAddress(), socket.getLocalPort());

            tcpC.sendData(sendReg.getBytes());

            byte[] recvData = tcpC.recieveData();

            RegistryReportsRegistrationStatus rrRS = new RegistryReportsRegistrationStatus(recvData);

            this.ID = rrRS.getAssignedID();

            System.out.println(rrRS.getInfoString());

        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public static void main(String args[]){

        CommandLineParser clp = new CommandLineParser();

        clp.validateMNodeCLA(args);

        String registry_host = clp.ip_addr;
        int registry_port = clp.port_num;

        System.out.printf("Connecting to %s on port %s ...\n", registry_host, registry_port);

        MessagingNode mNode = new MessagingNode(registry_host, registry_port);

        try {
            InetAddress inetA = InetAddress.getLocalHost();
            MNodeCommandParser mNodeCP = new MNodeCommandParser(inetA.getHostName(), inetA.getHostAddress());

            Scanner scan = new Scanner(System.in);
            String input;

            while((input = scan.nextLine()) != null){
                Event currEvent = mNodeCP.parseArgument(input);


            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.ip + " " + this.port + " " + this.ID;
    }

    @Override
    public Event onEvent(Event event) {
        return event;
    }
}

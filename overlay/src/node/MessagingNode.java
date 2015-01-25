package node;

import transport.TCPConnection;
import util.CommandLineParser;
import util.MNodeCommandParser;
import wireformats.OverlayNodeSendsRegistration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by ydubale on 1/22/15.
 */

public class MessagingNode {

    private DataInputStream dis;
    private DataOutputStream dos;
    private String myIp;
    private int myPort;
    private TCPConnection tcpC;
    private Socket servSock;

    public MessagingNode(String registry_ip, int registry_port) {
        tcpC = new TCPConnection();
        try {
            InetAddress inetA = InetAddress.getLocalHost();
            myIp = inetA.getHostAddress();

            servSock = new Socket(registry_ip, registry_port);

            myPort = servSock.getLocalPort();

            dis = new DataInputStream(servSock.getInputStream());
            dos = new DataOutputStream(servSock.getOutputStream());

        }
        catch (IOException e) {
            System.out.println("MessagingNode");
            e.printStackTrace();
        }
    }

    public void startCommunication(){
        //Registration
        OverlayNodeSendsRegistration onsr = new OverlayNodeSendsRegistration(myIp, myPort);
        tcpC.sendData(onsr.getBytes(), servSock);

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
                mNodeCP.parseArgument(input);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}

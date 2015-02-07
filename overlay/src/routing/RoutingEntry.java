package routing;

import node.MessagingNode;
import transport.TCPConnection;
import transport.TCPConnectionsCache;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class RoutingEntry {

    int nodeID;
    byte lengthIP;
    String ipAddr;
    int port;

    public RoutingEntry(int nodeID, String ipAddress, int port){
        this.nodeID = nodeID;
        this.ipAddr = ipAddress;
        this.port = port;
        this.lengthIP = (byte) ipAddr.length();
    }

    public RoutingEntry(int nodeID, TCPConnection tcpC){
        this.nodeID = nodeID;
        this.ipAddr = tcpC.getIP();
        this.port = tcpC.getPort();
        this.lengthIP = (byte) ipAddr.length();
    }

    public RoutingEntry(int nodeID, byte lengthIP, String ipAddress, int port){
        this.nodeID = nodeID;
        this.ipAddr = ipAddress;
        this.port = port;
        this.lengthIP = lengthIP;
    }

    public void addSelfToTCPConnectionCache(TCPConnectionsCache tcpConnectionsCache){
        try {
            tcpConnectionsCache.addNewConn(nodeID, new TCPConnection(new Socket(ipAddr, port), new MessagingNode(nodeID)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNodeID(){
        return nodeID;
    }

    public String toString(){
        return nodeID + "\t--- " + lengthIP + "\t" + ipAddr + "\t" + "\t" + port;
    }

    public byte[] getBytes(){
        byte[] rEntryBytes = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

        try {
            dos.writeInt(nodeID);

            dos.writeByte(lengthIP);

            byte[] ipBytes = ipAddr.getBytes();
            dos.write(ipBytes);

            dos.writeInt(port);

            dos.flush();
            rEntryBytes = baos.toByteArray();

            baos.close();
            dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rEntryBytes;
    }


}

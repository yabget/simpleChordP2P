package routing;

import node.MessagingNode;
import transport.TCPConnection;
import transport.TCPConnectionsCache;
import wireformats.ByteWriter;

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
            tcpConnectionsCache.addNewConn(
                    nodeID,
                    new TCPConnection(
                            new Socket(ipAddr, port), new MessagingNode(nodeID, ipAddr, port)
                    )
            );
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
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeInt(nodeID);

        byteWriter.writeByte(lengthIP);

        byteWriter.writeString(ipAddr);

        byteWriter.writeInt(port);

        byteWriter.close();

        return byteWriter.getBytes();
    }


}
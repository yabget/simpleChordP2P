package routing;

import node.MessagingNode;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class RoutingEntry {

    int nodeID;
    byte lengthIP;
    String ipAddr;
    int port;

    public RoutingEntry(MessagingNode messagingNode){
        this.nodeID = messagingNode.getID();
        this.ipAddr = messagingNode.getSocket().getInetAddress().getHostAddress();
        this.lengthIP = (byte) ipAddr.length();
        this.port = messagingNode.getSocket().getPort();
    }

    public String toString(){
        return nodeID + "\t--- " + lengthIP + "\t" + ipAddr + "\t" + "\t" + port;
    }

    public RoutingEntry(int nodeID, byte lengthIP, String ipAddr, int port){
        this.nodeID = nodeID;
        this.lengthIP = lengthIP;
        this.ipAddr = ipAddr;
        this.port = port;
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

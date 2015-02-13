package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsDeregistration implements Event{

    private byte type;
    private byte lengthIP;
    private String ipAddress;
    int portNumber;

    public int getNodeID() {
        return nodeID;
    }

    int nodeID;

    public OverlayNodeSendsDeregistration(String ipAddress, int portNumber, int nodeID){
        this.type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
        this.lengthIP = (byte) ipAddress.length();
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.nodeID = nodeID;
    }

    public OverlayNodeSendsDeregistration(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try {
            type = dis.readByte();

            lengthIP = dis.readByte();

            byte[] ipAddr = new byte[lengthIP];
            dis.readFully(ipAddr);

            ipAddress = new String(ipAddr);

            portNumber = dis.readInt();

            nodeID = dis.readInt();

            bais.close();
            dis.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

        try {
            dos.writeByte(type);

            dos.writeByte(lengthIP);

            dos.write(ipAddress.getBytes());

            dos.writeInt(portNumber);

            dos.writeInt(nodeID);

            dos.flush();
            toSend = baos.toByteArray();

            baos.close();
            dos.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return toSend;
    }

    @Override
    public byte getType() {
        return type;
    }

    public String toString(){
        return "Node: " + nodeID + " IP: " + ipAddress + " Port: " + portNumber;
    }

}
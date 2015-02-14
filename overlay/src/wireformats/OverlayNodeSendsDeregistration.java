package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsDeregistration implements Event{

    private byte type;
    private byte lengthIP;
    private String ipAddress;
    private int portNumber;
    private int nodeID;

    public OverlayNodeSendsDeregistration(String ipAddress, int portNumber, int nodeID){
        this.type = Protocol.OVERLAY_NODE_SENDS_DEREGISTRATION;
        this.lengthIP = (byte) ipAddress.length();
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.nodeID = nodeID;
    }

    public OverlayNodeSendsDeregistration(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        lengthIP = byteReader.readByte();

        ipAddress = byteReader.readString(lengthIP);

        portNumber = byteReader.readInt();

        nodeID = byteReader.readInt();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeByte(lengthIP);

        byteWriter.writeString(ipAddress);

        byteWriter.writeInt(portNumber);

        byteWriter.writeInt(nodeID);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public int getNodeID() {
        return nodeID;
    }

    @Override
    public byte getType() {
        return type;
    }

    public String toString(){
        return "Node: " + nodeID + " IP: " + ipAddress + " Port: " + portNumber;
    }

}
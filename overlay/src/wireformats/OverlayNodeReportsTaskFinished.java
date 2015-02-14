package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeReportsTaskFinished implements Event {

    private byte type;
    private byte ipLength;
    private String ipAddress;
    private int port;
    private int nodeID;

    public OverlayNodeReportsTaskFinished(String ipAddress, int port, int nodeID){
        this.type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
        this.ipAddress = ipAddress;
        this.ipLength = (byte) ipAddress.length();
        this.port = port;
        this.nodeID = nodeID;
    }

    public OverlayNodeReportsTaskFinished(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        ipLength = byteReader.readByte();

        ipAddress = byteReader.readString(ipLength);

        port = byteReader.readInt();

        nodeID = byteReader.readInt();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeByte(ipLength);

        byteWriter.writeString(ipAddress);

        byteWriter.writeInt(port);

        byteWriter.writeInt(nodeID);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    @Override
    public byte getType() {
        return type;
    }
}

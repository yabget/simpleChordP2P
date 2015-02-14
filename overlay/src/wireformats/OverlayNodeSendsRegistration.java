package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsRegistration implements Event {

    private byte type;
    private byte lengthIP;
    private String ipAddress;
    private int portNum;

    public OverlayNodeSendsRegistration(String ipAddress, int portNum) {
        this.type = Protocol.OVERLAY_NODE_SENDS_REGISTRATION;
        this.lengthIP = (byte) ipAddress.length();
        this.ipAddress = ipAddress;
        this.portNum = portNum;
    }

    public OverlayNodeSendsRegistration(byte[] data) {
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        lengthIP = byteReader.readByte();

        ipAddress = byteReader.readString(lengthIP);

        portNum = byteReader.readInt();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeByte(lengthIP);

        byteWriter.writeString(ipAddress);

        byteWriter.writeInt(portNum);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort(){
        return portNum;
    }

    @Override
    public byte getType() {
        return type;
    }

}

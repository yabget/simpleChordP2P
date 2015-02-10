package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeReportsTaskFinished implements Event {

    byte type;
    byte ipLength;
    String ipAddress;
    int port;
    int nodeID;

    public OverlayNodeReportsTaskFinished(String ipAddress, int port, int nodeID){
        this.type = Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED;
        this.ipAddress = ipAddress;
        this.ipLength = (byte) ipAddress.length();
        this.port = port;
        this.nodeID = nodeID;
    }

    public OverlayNodeReportsTaskFinished(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try{
            type = dis.readByte();

            ipLength = dis.readByte();

            byte[] ipAddr = new byte[ipLength];
            dis.readFully(ipAddr);

            ipAddress = new String(ipAddr);

            port = dis.readInt();

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
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeByte(ipLength);

            byte[] ipBytes = ipAddress.getBytes();
            dos.write(ipBytes);

            dos.writeInt(port);

            dos.writeInt(nodeID);

            dos.flush();
            toSend = baos.toByteArray();

            baos.close();
            dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return toSend;
    }

    @Override
    public byte getType() {
        return type;
    }
}

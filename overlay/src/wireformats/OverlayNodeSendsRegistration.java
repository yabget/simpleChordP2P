package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsRegistration implements Event {

    private int lengthIP;
    private String ipAddr;
    private int portNum;
    private byte[] dataBytes;
    private byte type;

    public OverlayNodeSendsRegistration(String ipAddr, int portNum) {
        this.ipAddr = ipAddr;
        this.portNum = portNum;
    }

    public OverlayNodeSendsRegistration(byte[] data) {
        dataBytes = data;

        ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));
        try {
            type = dis.readByte();

            lengthIP = dis.readInt();

            byte[] ipaddr = new byte[lengthIP];
            dis.readFully(ipaddr);

            ipAddr = new String(ipaddr);

            portNum = dis.readInt();

            bais.close();
            dis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddr(){
        return ipAddr;
    }

    public int getPort(){
        return portNum;
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
            lengthIP = ipAddr.length();
            dos.writeInt(lengthIP);

            byte[] ipBytes = ipAddr.getBytes();
            dos.write(ipBytes);

            dos.writeInt(portNum);

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

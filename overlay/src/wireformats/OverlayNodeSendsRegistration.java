package wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsRegistration implements Event {

    private int lengthIP;
    private String ipAddr;
    private int portNum;

    public OverlayNodeSendsRegistration(){

    }

    public OverlayNodeSendsRegistration(String ipAddr, int portNum) {
        lengthIP = ipAddr.length();
        this.ipAddr = ipAddr;
        this.portNum = portNum;
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(Protocol.OVERLAY_NODE_SENDS_REGISTRATION);
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
}

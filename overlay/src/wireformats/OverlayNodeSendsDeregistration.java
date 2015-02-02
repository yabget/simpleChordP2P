package wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsDeregistration implements Event{

    private int lengthIP;
    private String ipAddr;
    private int portNum;
    private int assignedID;
    private byte type;
    private byte[] dataBytes;


    @Override
    public byte[] getBytes() {
        byte[] toSend = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

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

    public OverlayNodeSendsDeregistration(){

    }



}

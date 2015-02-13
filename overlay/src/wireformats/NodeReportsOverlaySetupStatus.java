package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class NodeReportsOverlaySetupStatus implements Event {

    private byte type;
    private int successStatus;
    private byte lengthInfoString;
    private String infoString;

    public NodeReportsOverlaySetupStatus(int successStatus, String infoString){
        this.successStatus = successStatus;
        this.infoString = infoString;
        this.lengthInfoString = (byte) infoString.length();
        this.type = Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS;
    }

    public int getSuccessStatus() {
        return successStatus;
    }

    public boolean isSuccessful(){
        return successStatus != -1;
    }

    public String getInfoString(){
        return infoString;
    }

    public NodeReportsOverlaySetupStatus(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try {
            type = dis.readByte();

            successStatus = dis.readInt();

            lengthInfoString = dis.readByte();

            byte[] infoStringBytes = new byte[lengthInfoString];
            dis.readFully(infoStringBytes);

            infoString = new String(infoStringBytes);

            bais.close();
            dis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeInt(successStatus);

            dos.writeByte(lengthInfoString);

            byte[] infoStringBytes = infoString.getBytes();
            dos.write(infoStringBytes);

            dos.flush();
            toSend = baos.toByteArray();

            baos.close();
            dos.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
        return toSend;
    }

    @Override
    public byte getType() {
        return type;
    }
}

package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryReportsRegistrationStatus implements Event {

    private byte[] data;
    private int assignedID;
    private String infoString;
    private byte type;

    public RegistryReportsRegistrationStatus(int assignedID, String infoString){
        this.assignedID = assignedID;
        this.infoString = infoString;
    }

    public RegistryReportsRegistrationStatus(byte[] data){
        this.data = data;

        ByteArrayInputStream bias = new ByteArrayInputStream(this.data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bias));

        try {
            type = dis.readByte();

            assignedID = dis.readInt();

            byte tempLen = dis.readByte();
            byte[] infoStr = new byte[tempLen];

            dis.readFully(infoStr);

            infoString = new String(infoStr);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    public int getAssignedID(){
        return assignedID;
    }

    public String getInfoString(){
        return infoString;
    }


    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS);

            dos.writeInt(assignedID);

            dos.writeByte(infoString.length());

            byte[] infoStrByte = infoString.getBytes();
            dos.write(infoStrByte);

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
        return data[0];
    }
}

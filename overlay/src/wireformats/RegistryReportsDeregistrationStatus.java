package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryReportsDeregistrationStatus implements Event {

    private byte type;

    public RegistryReportsDeregistrationStatus(){
        type = Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    public RegistryReportsDeregistrationStatus(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try {
            type = dis.readByte();

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
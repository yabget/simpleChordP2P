package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryRequestsTaskInitiate implements Event{

    private byte type;
    private int numPacketsToSend;

    public RegistryRequestsTaskInitiate(int dataPacketsToSend){
        this.numPacketsToSend = dataPacketsToSend;
        this.type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
    }

    public RegistryRequestsTaskInitiate(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try{
            type = dis.readByte();

            numPacketsToSend = dis.readInt();

            bais.close();
            dis.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public int getNumPacketsToSend(){
        return numPacketsToSend;
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeInt(numPacketsToSend);

            dos.flush();

            toSend = baos.toByteArray();

            baos.close();
            dos.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return toSend;
    }

    @Override
    public byte getType() {
        return type;
    }
}

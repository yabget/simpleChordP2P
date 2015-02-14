package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryRequestsTaskInitiate implements Event{

    private byte type;
    private int numPacketsToSend;

    public RegistryRequestsTaskInitiate(int dataPacketsToSend){
        this.type = Protocol.REGISTRY_REQUESTS_TASK_INITIATE;
        this.numPacketsToSend = dataPacketsToSend;
    }

    public RegistryRequestsTaskInitiate(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        numPacketsToSend = byteReader.readInt();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeInt(numPacketsToSend);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public int getNumPacketsToSend(){
        return numPacketsToSend;
    }

    @Override
    public byte getType() {
        return type;
    }
}

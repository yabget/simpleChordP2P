package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryReportsDeregistrationStatus implements Event {

    private byte type;

    public RegistryReportsDeregistrationStatus(){
        type = Protocol.REGISTRY_REPORTS_DEREGISTRATION_STATUS;
    }

    public RegistryReportsDeregistrationStatus(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    @Override
    public byte getType() {
        return type;
    }
}
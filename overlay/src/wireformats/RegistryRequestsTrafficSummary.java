package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryRequestsTrafficSummary implements Event {

    private byte type;

    public RegistryRequestsTrafficSummary(){
        type = Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY;
    }

    public RegistryRequestsTrafficSummary(byte[] data){
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

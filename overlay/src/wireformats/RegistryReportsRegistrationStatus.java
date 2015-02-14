package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistryReportsRegistrationStatus implements Event {

    private byte type;
    private int assignedID;
    private byte infoLength;
    private String infoString;

    public RegistryReportsRegistrationStatus(int assignedID, String infoString){
        this.type = Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS;
        this.assignedID = assignedID;
        this.infoLength = (byte) infoString.length();
        this.infoString = infoString;
    }

    public RegistryReportsRegistrationStatus(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        assignedID = byteReader.readInt();

        infoLength = byteReader.readByte();

        infoString = byteReader.readString(infoLength);

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeInt(assignedID);

        byteWriter.writeByte(infoLength);

        byteWriter.writeString(infoString);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public int getAssignedID(){
        return assignedID;
    }

    public String getInfoString(){
        return infoString;
    }

    @Override
    public byte getType() {
        return type;
    }
}

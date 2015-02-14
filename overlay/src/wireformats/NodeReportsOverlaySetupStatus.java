package wireformats;

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

    public NodeReportsOverlaySetupStatus(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        successStatus = byteReader.readInt();

        lengthInfoString = byteReader.readByte();

        infoString = byteReader.readString(lengthInfoString);

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeInt(successStatus);

        byteWriter.writeByte(lengthInfoString);

        byteWriter.writeString(infoString);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    @Override
    public byte getType() {
        return type;
    }
}

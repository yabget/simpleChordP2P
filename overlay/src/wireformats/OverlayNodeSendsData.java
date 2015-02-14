package wireformats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsData implements Event {

    private byte type;
    private int destinationID;
    private int sourceID;

    private int payload;

    private int traceLength;
    private List<Integer> trace;

    public OverlayNodeSendsData(int destinationID, int sourceID, int payload, ArrayList<Integer> trace){
        this.type = Protocol.OVERLAY_NODE_SENDS_DATA;
        this.destinationID = destinationID;
        this.sourceID = sourceID;
        this.payload = payload;
        this.trace = trace;
        this.traceLength = trace.size();
    }

    public OverlayNodeSendsData(byte[] data){
        trace = new ArrayList<>();

        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        destinationID = byteReader.readInt();

        sourceID = byteReader.readInt();

        payload = byteReader.readInt();

        traceLength = byteReader.readInt();

        trace = byteReader.readIntList(traceLength);

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeInt(destinationID);

        byteWriter.writeInt(sourceID);

        byteWriter.writeInt(payload);

        byteWriter.writeInt(traceLength);

        byteWriter.writeIntList(trace);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public String toString(){
        return "Src: " + sourceID + "\tDest: " + destinationID + "\tLoad: " + payload + "\tTrace: " + trace;
    }

    public int getPayload(){
        return payload;
    }

    public boolean amInTrace(int nodeID){
        return trace.contains(nodeID);
    }

    public int getDestinationID(){
        return destinationID;
    }

    public boolean isDestination(int nodeID){
        return nodeID == destinationID;
    }

    public void addToTrace(int nodeID){
        trace.add(nodeID);
    }

    @Override
    public byte getType() {
        return type;
    }
}

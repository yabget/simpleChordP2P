package wireformats;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeReportsTrafficSummary implements Event {

    private byte type;
    private int nodeID;
    private int totalSent;
    private int totalRelayed;
    private long sumSent;
    private AtomicInteger totalReceived;
    private long sumReceived;

    public OverlayNodeReportsTrafficSummary(
            int nodeID, int totalSent, int totalRelayed,
            long sumSent, AtomicInteger totalReceived, long sumReceived
    ){
        this.type = Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY;
        this.nodeID = nodeID;
        this.totalSent = totalSent;
        this.totalRelayed = totalRelayed;
        this.sumSent = sumSent;
        this.totalReceived = totalReceived;
        this.sumReceived = sumReceived;
    }

    public OverlayNodeReportsTrafficSummary(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        nodeID = byteReader.readInt();

        totalSent = byteReader.readInt();

        totalRelayed = byteReader.readInt();

        sumSent = byteReader.readLong();

        int totRec =byteReader.readInt();
        totalReceived = new AtomicInteger(totRec);

        sumReceived = byteReader.readLong();

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeInt(nodeID);

        byteWriter.writeInt(totalSent);

        byteWriter.writeInt(totalRelayed);

        byteWriter.writeLong(sumSent);

        byteWriter.writeInt(totalReceived.get());

        byteWriter.writeLong(sumReceived);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public int getNodeID() {
        return nodeID;
    }

    public int getTotalSent() {
        return totalSent;
    }

    public int getTotalRelayed() {
        return totalRelayed;
    }

    public long getSumSent() {
        return sumSent;
    }

    public AtomicInteger getTotalReceived() {
        return totalReceived;
    }

    public long getSumReceived() {
        return sumReceived;
    }

    @Override
    public byte getType() {
        return type;
    }
}

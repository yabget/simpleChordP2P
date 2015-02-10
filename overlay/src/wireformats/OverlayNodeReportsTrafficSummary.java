package wireformats;

import java.io.*;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeReportsTrafficSummary implements Event {

    private byte type;
    private int nodeID;
    private int totalSent;
    private int totalRelayed;
    private long sumSent;
    private int totalReceived;
    private long sumReceived;

    public OverlayNodeReportsTrafficSummary(
            int nodeID, int totalSent, int totalRelayed,
            long sumSent, int totalReceived, long sumReceived
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
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try {
            type = dis.readByte();

            nodeID = dis.readInt();

            totalSent = dis.readInt();

            totalRelayed = dis.readInt();

            sumSent = dis.readLong();

            totalReceived = dis.readInt();

            sumReceived = dis.readLong();

            bais.close();
            dis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public int getTotalReceived() {
        return totalReceived;
    }

    public long getSumReceived() {
        return sumReceived;
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeInt(nodeID);

            dos.writeInt(totalSent);

            dos.writeInt(totalRelayed);

            dos.writeLong(sumSent);

            dos.writeInt(totalReceived);

            dos.writeLong(sumReceived);

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

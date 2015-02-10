package wireformats;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by ydubale on 1/22/15.
 */
public class OverlayNodeSendsData implements Event {

    byte type;
    int destinationID;
    int sourceID;

    int payload;

    int traceLength;
    ArrayList<Integer> trace;

    public OverlayNodeSendsData(int destinationID, int sourceID, int payload, ArrayList<Integer> trace){
        this.type = Protocol.OVERLAY_NODE_SENDS_DATA;
        this.destinationID = destinationID;
        this.sourceID = sourceID;
        this.payload = payload;
        this.trace = trace;
        this.traceLength = trace.size();
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

    public OverlayNodeSendsData(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try {
            type = dis.readByte();

            destinationID = dis.readInt();

            sourceID = dis.readInt();

            payload = dis.readInt();;

            traceLength = dis.readInt();

            if(trace == null){
                trace = new ArrayList<>(traceLength);
            }

            for(int i=0; i < traceLength; i++){
                trace.add(dis.readInt());
            }

            bais.close();
            dis.close();
        }
        catch (IOException ioe){
            ioe.printStackTrace();
        }
    }


    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeInt(destinationID);

            dos.writeInt(sourceID);

            dos.writeInt(payload);

            dos.writeInt(traceLength);

            for(int id : trace){
                dos.writeInt(id);
            }

            dos.flush();
            toSend = baos.toByteArray();

            baos.close();
            dos.close();

        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }

        return toSend;
    }

    @Override
    public byte getType() {
        return type;
    }
}

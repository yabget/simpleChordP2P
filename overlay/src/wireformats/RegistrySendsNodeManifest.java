package wireformats;

import routing.RoutingEntry;
import routing.RoutingTable;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistrySendsNodeManifest implements Event, Runnable {

    private RoutingTable routingTable;
    private byte routingTableSize;
    private Set<Integer> allNodeIDs;
    private int numNodes;
    private byte type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;

    public RegistrySendsNodeManifest(RoutingTable routingTable, Set<Integer> allNodeIDs){
        this.routingTable = routingTable;
        routingTableSize = routingTable.getTableSize();
        this.allNodeIDs = allNodeIDs;
        numNodes = allNodeIDs.size();
    }

    public RoutingTable getRoutingTable(){
        return routingTable;
    }

    public Set<Integer> getAllNodeIDs() {
        return allNodeIDs;
    }

    public RegistrySendsNodeManifest(byte[] data){
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(new BufferedInputStream(bais));

        try{
            type = dis.readByte();

            routingTableSize = dis.readByte();

            routingTable = new RoutingTable();

            for(int i=0; i < routingTableSize; i++){
                int nodeID = dis.readInt();

                byte lengthIP = dis.readByte();

                byte[] ipBytes = new byte[lengthIP];
                dis.readFully(ipBytes);

                String ipAddr = new String(ipBytes);

                int port = dis.readInt();

                RoutingEntry tempREntry = new RoutingEntry(nodeID, lengthIP, ipAddr, port);
                routingTable.addEntry(tempREntry);
            }

            allNodeIDs = new HashSet<Integer>();

            numNodes = dis.readByte();

            for(int i = 0; i < numNodes; i++){
                allNodeIDs.add(dis.readInt());
            }

            bais.close();
            dis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getBytes() {
        byte[] toSend = null;
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(baos));

            dos.writeByte(type);

            dos.writeByte(routingTableSize);

            for(RoutingEntry rEntry : routingTable.getEntries()){
                dos.write(rEntry.getBytes());
            }

            dos.writeByte(numNodes);

            for(Integer nodeID : allNodeIDs){
                dos.writeInt(nodeID);
            }

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

    @Override
    public void run() {

    }
}

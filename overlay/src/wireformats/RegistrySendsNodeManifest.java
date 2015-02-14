package wireformats;

import routing.RoutingEntry;
import routing.RoutingTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistrySendsNodeManifest implements Event {

    private byte type;
    private byte routingTableSize;
    private RoutingTable routingTable;

    private byte numNodes;
    private List<Integer> allNodeIDs;

    public RegistrySendsNodeManifest(RoutingTable routingTable, ArrayList<Integer> allNodeIDs){
        this.type = Protocol.REGISTRY_SENDS_NODE_MANIFEST;
        this.routingTableSize = routingTable.getTableSize();
        this.routingTable = routingTable;

        this.numNodes = (byte) allNodeIDs.size();
        this.allNodeIDs = allNodeIDs;
    }

    public RegistrySendsNodeManifest(byte[] data){
        ByteReader byteReader = new ByteReader(data);

        type = byteReader.readByte();

        routingTableSize = byteReader.readByte();

        routingTable = new RoutingTable();

        for(int i=0; i < routingTableSize; i++){
            int nodeID = byteReader.readInt();
            byte lengthIP = byteReader.readByte();
            String ipAddress = byteReader.readString(lengthIP);
            int port = byteReader.readInt();

            RoutingEntry rEntry = new RoutingEntry(nodeID, lengthIP, ipAddress, port);
            routingTable.addEntry(rEntry);
        }

        numNodes = byteReader.readByte();

        allNodeIDs = byteReader.readIntList(numNodes);

        byteReader.close();
    }

    @Override
    public byte[] getBytes() {
        ByteWriter byteWriter = new ByteWriter();

        byteWriter.writeByte(type);

        byteWriter.writeByte(routingTableSize);

        for(RoutingEntry rEntry : routingTable.getEntries()){
            byteWriter.writeByteArray(rEntry.getBytes());
        }

        byteWriter.writeByte(numNodes);

        byteWriter.writeIntList(allNodeIDs);

        byteWriter.close();

        return byteWriter.getBytes();
    }

    public RoutingTable getRoutingTable(){
        return routingTable;
    }

    public List<Integer> getAllNodeIDs() {
        return allNodeIDs;
    }

    @Override
    public byte getType() {
        return type;
    }
}

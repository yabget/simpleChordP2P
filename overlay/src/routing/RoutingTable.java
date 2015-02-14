package routing;

import transport.TCPConnectionsCache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydubale on 1/22/15.
 */
public class RoutingTable {

    private ArrayList<RoutingEntry> entries;

    public RoutingTable(){
        entries = new ArrayList<>();
    }

    public void addEntry(RoutingEntry entry){
        entries.add(entry);
    }

    public List<RoutingEntry> getEntries(){
        return entries;
    }

    public byte getTableSize(){
        return (byte) entries.size();
    }

    /**
     * Returns the routing table for a given index in the sorted array
     * Limits: Routing table size should be between 1-4
     * @param index - The index of the sorted array
     * @param sortedIDs - Sorted collection of node IDs
     * @return A RoutinTable object for the node at 'index'
     */
    public static RoutingTable getRoutingTableForIndex(
            int index, int routingTableSize, ArrayList<Integer> sortedIDs, TCPConnectionsCache tcpCC){

        RoutingTable rTable = new RoutingTable();

        int power = 1;

        for(int i = 0; i < routingTableSize; i++){
            // entryindex is the next entry in the routing table
            int entryIndex = (index + power) % sortedIDs.size(); // Accounts for circular overflow

            int nodeID = sortedIDs.get(entryIndex);

            rTable.addEntry(new RoutingEntry(nodeID, tcpCC.getTCPConnection(nodeID)));

            power = (power << 1); // Increase by powers of 2
        }

        return rTable;
    }

    public int determineBestNode(int destinationID) {
        int maxNode = entries.get(0).getNodeID();
        int entriesSize = entries.size();

        int allFail = 0;

        for(int i = 0; i < entriesSize; i++){
            int currNode = entries.get(i).getNodeID();

            if(currNode == destinationID){
                return currNode;
            }

            if(currNode < destinationID && currNode > maxNode){
                maxNode = currNode;
            }
            else{
                allFail++;
            }
        }

        if(allFail == entriesSize){
            return entries.get(entriesSize-1).getNodeID(); // Return last node
        }

        return maxNode;
    }

}
package routing;

import transport.TCPConnectionsCache;

import java.util.*;

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

    public static void main(String args[]){
        RoutingTable rTable = new RoutingTable();

        //Routing entry
        //int nodeID, byte lengthIP, String ipAddress, int port

        Random rand = new Random();

        int[] nums = new int[10];

        for(int i=0; i < nums.length; i++){
            nums[i] = rand.nextInt(128);
        }

        Arrays.sort(nums);
        System.out.println("Nodes: ");
        for(int i : nums){
            System.out.print(i + "\t");
        }
        System.out.println();


        byte b = 23;

        for(int i=0; i < nums.length; i++){
            RoutingEntry rE = new RoutingEntry(nums[i], b, "haha", 13442);
            rTable.addEntry(rE);
        }

        for(RoutingEntry rEntry: rTable.getEntries()){
            System.out.println(rEntry);
        }
        System.out.println();

        Scanner scan = new Scanner(System.in);

        while(true){
            int dest = scan.nextInt();
            System.out.println(rTable.determineBestNode(dest));
        }



    }

}
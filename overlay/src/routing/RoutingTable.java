package routing;

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

    private boolean isInBetween(int destination, int leftValue, int rightValue){
        return (destination > leftValue) && (destination < rightValue);
    }

    public int determineBestNode(int destinationID) {
        int maxNode = entries.get(0).getNodeID();
        int entriesSize = entries.size();

        int allFail = 0;

        for(int i = 0; i < entriesSize; i++){
            int currNode = entries.get(i).getNodeID();

            if(currNode == destinationID){
                //System.out.println("[**INRT**]: " + currNode);
                return currNode;
            }

            if(currNode < destinationID && currNode > maxNode){
                maxNode = currNode;
            }
            else{
                allFail++;
                //System.out.println("All Fail: " + allFail + "\tEntrySize: " + entriesSize);
            }
        }

        if(allFail == entriesSize){
            //System.out.println("[**LAST**]: " + entries.get(entriesSize-1).getNodeID());
            return entries.get(entriesSize-1).getNodeID(); // Return last node
        }

        //System.out.println("[**BEST**]: " + maxNode);
        return maxNode;
    }

}
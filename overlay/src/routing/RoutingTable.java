package routing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydubale on 1/22/15.
 */
public class RoutingTable {

    private ArrayList<RoutingEntry> entries;
    private boolean wrapsAround;
    private int idWrapOccursAt;

    public RoutingTable(){
        entries = new ArrayList<>();
        wrapsAround = false;
    }

    public void addEntry(RoutingEntry entry){
        int lastIndex = entries.size()-1;
        int lastNodeID = entries.get(lastIndex).getNodeID();

        if(entry.getNodeID() < lastNodeID){
            wrapsAround = true;
            idWrapOccursAt = entry.getNodeID();
        }

        entries.add(entry);
    }

    public List<RoutingEntry> getEntries(){
        return entries;
    }

    public byte getTableSize(){
        return (byte) entries.size();
    }

    public boolean hasNode(int nodeID){
        for(RoutingEntry rEntry : entries){
            if(rEntry.getNodeID() == nodeID){
                return true;
            }
        }
        return false;
    }

    public int determineBestNode(int destinationID) {
        int bestID = entries.get(0).getNodeID();

        for(RoutingEntry routingEntry : entries){
            int currNodeID = routingEntry.getNodeID();

            if(currNodeID == destinationID){
                return destinationID;
            }

            if(wrapsAround){
                if(currNodeID == idWrapOccursAt){
                    break;
                }
                bestID = currNodeID;
            }
            else {
                if(currNodeID < destinationID){
                    bestID = currNodeID;
                }
            }
        }

        return bestID;
    }

}

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
        if(entries.size() > 0){
            int lastIndex = entries.size()-1;
            int lastNodeID = entries.get(lastIndex).getNodeID();

            if(entry.getNodeID() < lastNodeID){
                wrapsAround = true;
                idWrapOccursAt = entry.getNodeID();
            }
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

    private boolean isInBetween(int destination, int leftValue, int rightValue){
        return (destination > leftValue) && (destination < rightValue);
    }

    public int determineBestNode(int destinationID) {
        int bestID = entries.get(0).getNodeID();

        if(bestID == destinationID){
            return bestID;
        }

        for(int i= 0; i < entries.size()-1; i++){
            int firstNode = entries.get(i).getNodeID();
            int secondNode = entries.get(i+1).getNodeID();

            if(secondNode == destinationID){
                return secondNode;
            }

            if(isInBetween(destinationID, firstNode, secondNode)){
                bestID = firstNode;
            }
        }

        return bestID;
    }

}

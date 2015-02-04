package routing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ydubale on 1/22/15.
 */
public class RoutingTable {

    ArrayList<RoutingEntry> entries;


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

}

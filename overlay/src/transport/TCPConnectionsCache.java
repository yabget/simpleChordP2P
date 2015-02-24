package transport;

import wireformats.Event;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPConnectionsCache {

    private ConcurrentHashMap<Integer, TCPConnection> tcpConns;

    public TCPConnectionsCache(){
        tcpConns = new ConcurrentHashMap<>();
    }

    public void addNewConn(int nodeID, TCPConnection  tcpC){
        tcpConns.put(nodeID, tcpC);
    }

    public void removeConn(int nodeID){
        tcpConns.remove(nodeID);
    }

    public TCPConnection getTCPConnection(int nodeID){
        return tcpConns.get(nodeID);
    }

    public void sendEvent(int nodeID, Event event){
        tcpConns.get(nodeID).sendData(event.getBytes());
    }

    public String toString(){
        String toReturn = "\nExisting connections\n";
        for(Integer i : tcpConns.keySet()){
            toReturn += "Node " + i + " " + tcpConns.get(i) + "\n";
        }
        return toReturn;
    }
}
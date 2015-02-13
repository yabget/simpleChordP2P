package transport;

import wireformats.Event;

import java.util.Hashtable;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPConnectionsCache {

    private Hashtable<Integer, TCPConnection> tcpConns;

    public TCPConnectionsCache(){
        tcpConns = new Hashtable<>();
    }

    public void addNewConn(int nodeID, TCPConnection  tcpC){
        tcpConns.put(nodeID, tcpC);
    }

    public int getIDofConnection(String ipAddress, int port){
        for(Integer id : tcpConns.keySet()){
            TCPConnection currConn = tcpConns.get(id);
            if(currConn.getIP().equals(ipAddress) && currConn.getPort() == port){
                return id;
            }
        }
        return -1;
    }

    public TCPConnection getTCPConnection(int nodeID){
        return tcpConns.get(nodeID);
    }

    public void sendEvent(int nodeID, Event event){
        tcpConns.get(nodeID).sendData(event.getBytes());
    }

    public String toString(){
        String toReturn = "Existing connections\n";
        for(Integer i : tcpConns.keySet()){
            toReturn += "Node " + i + " " + tcpConns.get(i) + "\n";
        }
        return toReturn;
    }

    public boolean connectionExists(TCPConnection tcpC){
        return tcpConns.containsValue(tcpC);
    }
}

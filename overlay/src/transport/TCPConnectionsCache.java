package transport;

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

    public boolean connectionExists(TCPConnection tcpC){
        return tcpConns.containsValue(tcpC);
    }

    public Hashtable<Integer, TCPConnection> getAllTCPConnections(){
        return tcpConns;
    }

}

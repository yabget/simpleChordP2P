package transport;

import node.MessagingNode;

import java.net.Socket;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPConnectionsCache {

    private Hashtable<Integer, TCPConnection> tcpConns;
    private Hashtable<Integer, MessagingNode> messNodes;

    public TCPConnectionsCache(){
        tcpConns = new Hashtable<>();
        messNodes = new Hashtable<>();
    }


    public void addNewConn(int nodeID, Socket socket){

        TCPConnection tcpC = new TCPConnection(socket);

        tcpConns.put(nodeID, tcpC);

    }

    public Hashtable<Integer, MessagingNode> getAllMessagingNodes(){
        return messNodes;
    }

    public Hashtable<Integer, TCPConnection> getAllTCPConnections(){
        return tcpConns;
    }






}

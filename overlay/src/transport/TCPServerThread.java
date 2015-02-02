package transport;


import node.Node;
import wireformats.Event;
import wireformats.EventFactory;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPServerThread implements Runnable {

    private Socket socket;
    private ServerSocket ss;
    private Node node;


    public TCPServerThread(Socket socket, Node node){
        this.socket = socket;
        this.node = node;
    }

    @Override
    public void run() {
        while(true){
            while(socket.isConnected()){
                TCPConnection tcpC = new TCPConnection(socket);
                byte[] meData = tcpC.recieveData();
                EventFactory ef = EventFactory.getInstance();
                Event toReply = node.onEvent(ef.getEvent(meData));
                tcpC.sendData(toReply.getBytes());
            }
        }

    }

}

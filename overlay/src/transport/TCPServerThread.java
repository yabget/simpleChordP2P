package transport;

import node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPServerThread extends Thread {

    private int serverPort;
    private Node node;

    public TCPServerThread(Node node, int serverPort){
        this.serverPort = serverPort;
        this.node = node;
    }

    @Override
    public void run() {
        try {
            ServerSocket servSock = new ServerSocket(serverPort);

            Socket socket;
            while((socket = servSock.accept()) != null){
                //Starts a new receiver thread to listen on the socket
                TCPConnection newConnection = new TCPConnection(socket, node);
                node.addConnection(newConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

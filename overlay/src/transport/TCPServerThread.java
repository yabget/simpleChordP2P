package transport;

import node.Node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPServerThread implements Runnable {

    private ServerSocket serverSocket;
    private Node node;

    public TCPServerThread(Node node, ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        this.node = node;
    }

    @Override
    public void run() {
        try {
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                //Starts a new receiver thread to listen on the socket
                System.out.println("Accepted new connection!");
                TCPConnection newConnection = new TCPConnection(socket, node);
                newConnection.startReceiveThread();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
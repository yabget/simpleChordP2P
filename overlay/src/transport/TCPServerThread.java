package transport;


import node.MessagingNode;
import node.Node;
import wireformats.Event;
import wireformats.EventFactory;
import wireformats.OverlayNodeSendsRegistration;

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
        TCPConnection tcpC = new TCPConnection(socket);

        // Send initial message to registry
        if(node != null && node instanceof MessagingNode){
            OverlayNodeSendsRegistration sendReg = new OverlayNodeSendsRegistration(
                    socket.getLocalAddress().getHostAddress(), socket.getLocalPort()
            );
            tcpC.sendData(sendReg.getBytes());
            System.out.println("Trying to register myself to registry.");
        }

        while(true){
            while(socket.isConnected()){
                byte[] recievedD = tcpC.recieveData();
                System.out.println("1st byte received (protocol): " + recievedD[0]);

                Event receivedE = EventFactory.getInstance().getEvent(recievedD);
                Event toReply = node.onEvent(receivedE);

                if(toReply != null){
                    tcpC.sendData(toReply.getBytes());
                }
            }
        }

    }

}

package transport;

import node.Node;
import wireformats.Event;
import wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPConnection {

    private TCPSender tcpSender;
    private TCPReceiver tcpReceiver;

    public TCPConnection(Socket socket, Node node){
        tcpSender = new TCPSender(socket);
        tcpReceiver = new TCPReceiver(socket, node);

        Thread receiveThread = new Thread(tcpReceiver);
        receiveThread.start();
    }

    public void sendData(byte[] dataToSend){
        tcpSender.sendData(dataToSend);
    }

    private class TCPSender{
        private DataOutputStream dos;

        public TCPSender(Socket socket){
            try{
                dos = new DataOutputStream(socket.getOutputStream());
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        public void sendData(byte[] dataToSend){
            try{
                int dataLen = dataToSend.length;
                dos.writeInt(dataLen);
                dos.write(dataToSend, 0, dataLen);
                dos.flush();
            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    private class TCPReceiver implements Runnable {

        private Socket socket;
        private DataInputStream dis;
        private Node node;

        public TCPReceiver(Socket socket, Node node) {
            try {
                this.socket = socket;
                this.node = node;
                dis = new DataInputStream(socket.getInputStream());
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
        @Override
        public void run() {
            try {
                int dataLen;
                while(socket != null){
                    dataLen = dis.readInt();
                    byte[] data = new byte[dataLen];
                    dis.readFully(data, 0, dataLen);

                    EventFactory eventFac = EventFactory.getInstance();

                    Event receivedEvent = eventFac.getEvent(data);
                    node.onEvent(receivedEvent);
                }
            }
            catch(IOException ioe ){
                ioe.printStackTrace();
            }
        }
    }
}

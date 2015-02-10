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
    }

    public void startReceiveThread(){
        Thread receiveThread = new Thread(tcpReceiver);
        receiveThread.start();
    }

    public String getIP(){
        return tcpSender.socket.getInetAddress().getHostAddress();
    }

    public int getPort(){
        return tcpSender.socket.getPort();
    }

    public String toString(){
        return getIP() + " " + getPort();
    }

    //todo: pass in Event
    public void sendData(byte[] dataToSend){
        tcpSender.sendData(dataToSend);
    }

    private class TCPSender{

        private Socket socket;

        public TCPSender(Socket socket){
            this.socket = socket;
        }

        public void sendData(byte[] dataToSend){
            try{
                //System.out.println("About to send");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                //System.out.println("Got the DOS");
                int dataLen = dataToSend.length;

                synchronized (dos){
                    dos.writeInt(dataLen);
                    dos.write(dataToSend, 0, dataLen);
                }
                //System.out.println("Wrote the length");

                //System.out.println("Wrote the data");
                dos.flush();
                //System.out.println("SENT!");

            }
            catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    private class TCPReceiver implements Runnable {

        private Socket socket;
        private Node node;

        public TCPReceiver(Socket socket, Node node) {
            this.socket = socket;
            this.node = node;
        }

        @Override
        public void run() {
            try {
                int dataLen;

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                EventFactory eventFac = EventFactory.getInstance();

                while(socket != null){
                    dataLen = dis.readInt();
                    byte[] data = new byte[dataLen];
                    dis.readFully(data, 0, dataLen);

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

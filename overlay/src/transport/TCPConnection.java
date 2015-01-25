package transport;

import wireformats.Event;
import wireformats.EventFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPConnection {


    public void sendData(byte[] dataToSend, Socket socket){
        TCPSender tcpS = new TCPSender(socket);
        tcpS.sendData(dataToSend);
    }

    public void configRegistry(ServerSocket ss){

    }

    private class TCPSender{
        private Socket socket;
        private DataOutputStream dos;

        public TCPSender(Socket socket){
            try{
                this.socket = socket;
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

    private class TCPReceiverThread implements Runnable{

        private Socket socket;
        private DataInputStream dis;

        public TCPReceiverThread(Socket socket) {
            try {
                this.socket = socket;
                dis = new DataInputStream(socket.getInputStream());
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        @Override
        public void run() {
            int dataLen;
            while(this.socket != null){
                try {
                    dataLen = dis.readInt();
                    byte[] data = new byte[dataLen];
                    dis.readFully(data, 0, dataLen);

                    Event thisEvent = EventFactory.getInstance().getEvent(data[0]);


                }
                catch (SocketException se){
                    se.printStackTrace();
                    break;
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                    break;
                }
            }
        }
    }
}

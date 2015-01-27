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

    public byte[] recieveData(Socket socket) {
        TCPReceiver tcpR = new TCPReceiver(socket);
        return tcpR.receive();
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

    private class TCPReceiver {

        private Socket socket;
        private DataInputStream dis;

        public TCPReceiver(Socket socket) {
            try {
                this.socket = socket;
                dis = new DataInputStream(socket.getInputStream());
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        public byte[] receive() {
            byte[] data = null;
            try {
                int dataLen;
                while(socket != null){
                    dataLen = dis.readInt();
                    data = new byte[dataLen];
                    dis.readFully(data, 0, dataLen);

                }
            }
            catch(IOException ioe ){
                ioe.printStackTrace();
            }
            return data;
        }

    }
}

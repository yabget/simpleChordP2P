package transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ydubale on 1/22/15.
 */
public class TCPServerThread implements Runnable {

    private Socket sock = null;
    private ServerSocket ss;

    public TCPServerThread(ServerSocket ss){
        this.ss = ss;
    }

    @Override
    public void run() {
        try {
            System.out.println("Listening on Port : " + ss.getLocalPort());

            sock = ss.accept();

            DataInputStream dis = new DataInputStream(sock.getInputStream());
            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

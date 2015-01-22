import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by yabsubu12 on 1/21/15.
 */
public class Server {

    static class ServerThreads implements Runnable {

        private Socket mainSock = null;
        private int serverNum;
        protected Thread myThread = null;

        public  ServerThreads(Socket mySoc, int num){
            mainSock = mySoc;
            serverNum = num;
        }

        @Override
        public void run() {
            try{
                System.out.println("Accepted connection");

                PrintWriter output = new PrintWriter(mainSock.getOutputStream(), true);
                System.out.println("Sending first mess to client.");
                output.printf("HI CLIENT! I am server %s.\n", serverNum);

                BufferedReader in = new BufferedReader(new InputStreamReader(mainSock.getInputStream()));

                BufferedReader CLInput = new BufferedReader(new InputStreamReader(System.in));

                String input;
                System.out.println("Server INPUT: " + in.ready());
                System.out.printf("Client %s: ", serverNum);

                while((input = in.readLine()) != null){
                    System.out.println(input);
                    System.out.print("You: ");
                    output.println(CLInput.readLine());
                    System.out.printf("Client %s: ", serverNum);
                }
            } catch(IOException ioe){
                System.out.println("I FAILED");
                ioe.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {

        System.out.println("Enter 0(server) or 1(client)");
        Scanner scan = new Scanner(System.in);
        int ServOrClient = scan.nextInt();

        ArrayList<Thread> threads = new ArrayList<Thread>();

        if(ServOrClient == 0){
            // Server
            int count = 0;
            ServerSocket ss = null;
            try{
                ss = new ServerSocket(0, 10);
            }
            catch (IOException ioe){
                ioe.printStackTrace();
                System.exit(1);
            }

            while(true){
                System.out.println("Listening on Port : " + ss.getLocalPort());
                Socket mySoc = null;
                try {
                    mySoc = ss.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                (new Thread(new ServerThreads(mySoc, count))).start();
                count++;
            }
        }
        else {
            System.out.println("---------------- CLIENT ----------------");
            System.out.println("Enter IP to connect to ");
            String ip = scan.next();
            System.out.println("Enter port to connect ");
            int port = scan.nextInt();

            Socket sock = null;
            try {
                sock = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Made connection with server");

            assert sock != null;
            PrintWriter output = null;
            try {
                output = new PrintWriter(sock.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Got output writer");

            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Got input reader");

            BufferedReader CLInput = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Got std input reader");

            System.out.print("Server: ");



            try {
                System.out.println("READY INPUT: " + input.ready());
                System.out.println(input.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            }
            String mess;
            System.out.print("You: ");
            try {
                while((mess = CLInput.readLine()) != null){
                    output.println(mess);
                    System.out.print("Server: ");
                    String resp = input.readLine();
                    System.out.println(resp);
                    System.out.print("You: ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

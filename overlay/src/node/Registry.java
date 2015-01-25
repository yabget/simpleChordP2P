package node;

import transport.TCPServerThread;
import util.CommandLineParser;
import util.RegistryCommandsParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * Created by ydubale on 1/20/15.
 */
public class Registry {

    private ServerSocket ss;
    private int portNum;

    public Registry(int port){
        try {
            this.portNum = port;
            ss = new ServerSocket(portNum, 10);

            (new Thread(new TCPServerThread(ss))).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        CommandLineParser clp = new CommandLineParser();

        clp.validateRegisCLA(args);

        Registry reg = new Registry(clp.port_num);

        RegistryCommandsParser rcp = new RegistryCommandsParser();
        Scanner scan = new Scanner(System.in);
        String input;

        System.out.print("Command: ");
        while((input = scan.nextLine()) != null){
            rcp.parseArgument(input);
            System.out.print("Command: ");
        }

    }

}

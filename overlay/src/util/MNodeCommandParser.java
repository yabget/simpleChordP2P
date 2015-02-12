package util;

import node.MessagingNode;
import wireformats.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ydubale on 1/25/15.
 */
public class MNodeCommandParser {

    public static final String PRINT_COUNTERS_AND_DIAGNOSTICS = "print-counters-and-diagnostics";
    public static final String EXIT_OVERALY = "exit-overlay";
    public static final String HELP = "help";

    private MessagingNode mNode;

    public MNodeCommandParser(MessagingNode mNode){
        this.mNode = mNode;
    }

    private void printOptions() throws UnknownHostException {
        InetAddress inetA = InetAddress.getLocalHost();
        System.out.println("You are on " + inetA.getHostName() + " ip " + inetA.getHostAddress());
        System.out.println("\t" + PRINT_COUNTERS_AND_DIAGNOSTICS);
        System.out.println("\t" + EXIT_OVERALY);
        System.out.println("\t" + HELP);
    }

    public Event parseArgument(String command){
        if(command.equals(PRINT_COUNTERS_AND_DIAGNOSTICS)){
            mNode.print_counters_and_diagnostics();
        }
        else if(command.equals(EXIT_OVERALY)){
            mNode.sendDeregistration();
        }
        else {
            try {
                printOptions();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

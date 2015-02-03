package util;

import wireformats.Event;
import wireformats.OverlayNodeSendsDeregistration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ydubale on 1/25/15.
 */
public class MNodeCommandParser {

    public static final String PRINT_COUNTERS_AND_DIAGNOSTICS = "print-counters-and-diagnostics";
    public static final String EXIT_OVERALY = "exit-overlay";
    public static final String HELP = "help";

    public MNodeCommandParser(){

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

        }
        else if(command.equals(EXIT_OVERALY)){
            return (Event)new OverlayNodeSendsDeregistration();
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

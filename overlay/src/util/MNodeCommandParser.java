package util;

/**
 * Created by ydubale on 1/25/15.
 */
public class MNodeCommandParser {

    public static final String PRINT_COUNTERS_AND_DIAGNOSTICS = "print-counters-and-diagnostics";
    public static final String EXIT_OVERALY = "exit-overlay";
    public static final String HELP = "help";

    private String hostName;
    private String ip;

    public MNodeCommandParser(String hostName, String ip){
        this.hostName = hostName;
        this.ip = ip;
    }

    private void printOptions(){
        System.out.println("You are on " + hostName + " ip " + ip);
        System.out.println("\t" + PRINT_COUNTERS_AND_DIAGNOSTICS);
        System.out.println("\t" + EXIT_OVERALY);
        System.out.println("\t" + HELP);
    }

    public void parseArgument(String command){
        if(command.equals(PRINT_COUNTERS_AND_DIAGNOSTICS)){

        }
        else if(command.equals(EXIT_OVERALY)){

        }
        else {
            printOptions();
        }
    }

}

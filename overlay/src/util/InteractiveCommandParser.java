package util;

/**
 * Created by ydubale on 1/22/15.
 */
public class InteractiveCommandParser {

    private static final String INVALID_IP_FORMAT = "Improper IP Address format.";
    private static final String INVALID_IP_ADDRESS = "Please provide a valid IP address.";
    private static final String INVALID_PORT_RANGE = "Invalid port number. Must be between 1024 - 65535";
    private static final String INVALID_PORT_FORMAT = "Improper port number format.";
    private static final String INVALID_NUM_ARGS = "Not enough arguments. Specify host-IP and port.";

    private static final int MIN_VALID_PORT = 1024;
    private static final int MAX_VALID_PORT = 65535;

    private static final int IP_DOT_SECTIONS = 4;
    private static final int MIN_IPV4_VAL = 0;
    private static final int MAX_IPV4_VAL = 255;

    public String ip_addr = null;
    public int port_num = 0;

    public InteractiveCommandParser(){

    }

    private void printErrorExit(String error){
        System.out.println(error);
        System.exit(1);
    }

    private void checkIP(String reg_host){

        try {
            String[] ipVals = reg_host.split("\\.");

            if(ipVals.length != IP_DOT_SECTIONS){
                printErrorExit(INVALID_IP_FORMAT);
            }

            for (String num : ipVals){
                int ipval = Integer.parseInt(num);
                if(ipval < MIN_IPV4_VAL || ipval > MAX_IPV4_VAL){
                    printErrorExit(INVALID_IP_FORMAT);
                }
            }

            this.ip_addr = reg_host;
        }
        catch (NumberFormatException nfe){
            printErrorExit(INVALID_IP_ADDRESS);
        }
    }

    private void checkPort(String port){
        try{
            int portNum = Integer.parseInt(port);
            if(portNum < MIN_VALID_PORT || portNum > MAX_VALID_PORT){
                printErrorExit(INVALID_PORT_RANGE);
            }
            this.port_num = portNum;
        } catch(NumberFormatException notNum){
            printErrorExit(INVALID_PORT_FORMAT);
        }
    }

    public void validateMNodeCommandLineArgs(String args[]){
        if(args.length != 2){
            printErrorExit(INVALID_NUM_ARGS);
        }
        checkIP(args[0]);
        checkPort(args[1]);
    }


}

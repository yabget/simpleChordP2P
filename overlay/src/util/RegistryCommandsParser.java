package util;

/**
 * Created by ydubale on 1/25/15.
 */
public class RegistryCommandsParser {

    private final static String LIST_MESSAGING_NODES = "list-messaging-nodes";
    private final static String SETUP_OVERLAY = "setup-overlay";
    private final static String LIST_ROUTING_TABLES = "list-routing-tables";
    private final static String START = "start";
    private final static String HELP = "help";

    private void printOptions(){
        System.out.println("You are at the registry. Possible commands are:");
        System.out.println("\t" + LIST_MESSAGING_NODES);
        System.out.println("\t" + SETUP_OVERLAY + " number-of-routing-table-entries (e.g. 3)");
        System.out.println("\t" + LIST_ROUTING_TABLES);
        System.out.println("\t" + START + " number-of-messages (e.g. 25000)");
        System.out.println("\t" + HELP);
    }


    public void parseArgument(String argument){
        String[] args = argument.split(" ");

        if(args.length <= 0 || args.length > 2) {
            System.out.println("Arguments not recognized.");
            System.out.println("Enter 'help' for list of valid commands");
        }

        String command = args[0];

        if(command.equals(LIST_MESSAGING_NODES)){

        }
        else if(command.equals(SETUP_OVERLAY)){

        }
        else if(command.equals(LIST_ROUTING_TABLES)){

        }
        else if(command.equals(START)){

        }
        else{
            printOptions();
        }
    }

}

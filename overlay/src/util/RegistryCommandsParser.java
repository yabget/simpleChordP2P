package util;

import node.Registry;

/**
 * Created by ydubale on 1/25/15.
 */
public class RegistryCommandsParser {

    Registry registry;

    private final static String LIST_MESSAGING_NODES = "list-messaging-nodes";
    private final static String SETUP_OVERLAY = "setup-overlay";
    private final static String LIST_ROUTING_TABLES = "list-routing-tables";
    private final static String START = "start";
    private final static String HELP = "help";

    public RegistryCommandsParser(Registry registry){
        this.registry = registry;
    }

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
            registry.list_messaging_nodes();
        }
        else if(command.equals(SETUP_OVERLAY)){
            int size_table = 3;
            if(args.length == 2){
                try{
                    size_table = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException nfe){
                    System.out.println("Unable to parse setup-overlay integer. Using default value of 3");
                }
            }
            registry.setup_overlay(size_table);
        }
        else if(command.equals(LIST_ROUTING_TABLES)){
            registry.list_routing_tables();
        }
        else if(command.equals(START)){
            int numMessagesToSend = 2;
            if(args.length == 2){
                try{
                    numMessagesToSend = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException nfe){
                    System.out.println("Unable to parse setup-overlay integer. Using default value of 3");
                }
            }
            registry.start(numMessagesToSend);
        }
        else{
            printOptions();
        }
    }

}

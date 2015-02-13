package util;

import node.Registry;

/**
 * Created by ydubale on 1/25/15.
 */
public class RegistryCommandsParser {

    private final static int DEFAULT_ROUTING_TABLE_SIZE = 3;

    private Registry registry;

    // Commands
    private final static String LIST_MESSAGING_NODES = "list-messaging-nodes";
    private final static String SETUP_OVERLAY = "setup-overlay";
    private final static String LIST_ROUTING_TABLES = "list-routing-tables";
    private final static String START = "start";
    private final static String HELP = "help";

    //Error Strings
    private final static String INVALID_COMMAND = "Invalid Command. Type 'help' for list of valid commands.";
    private final static String INVALID_NUMBER = "Unable to parse integer. Please try again.";
    private final static String INVALID_MESSAGES_TO_SEND = "Please specify how many " +
            "messages nodes should send to eachother.";

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

    private int parseStringInt(String numberString){
        int number = -1;
        try{
            number = Integer.parseInt(numberString);
        }
        catch(NumberFormatException nfe){
            System.out.println(INVALID_NUMBER);
        }
        return number;
    }

    private void setupOverlay(String[] args){
        int sizeTable = DEFAULT_ROUTING_TABLE_SIZE;
        if(args.length == 2){
            sizeTable = parseStringInt(args[1]);
            if(sizeTable == -1){
                System.out.println(INVALID_NUMBER);
                return;
            }
        }
        registry.setup_overlay(sizeTable);
    }

    private void start(String[] args){
        if(args.length == 2){
            int numMessagesToSend = parseStringInt(args[1]);
            if(numMessagesToSend != -1){
                registry.start(numMessagesToSend);
            }
        }
        else{
            System.out.println(INVALID_MESSAGES_TO_SEND);
        }
    }

    public void parseArgument(String argument){
        String[] args = argument.split(" ");

        if(args.length <= 0 || args.length > 2) {
            System.out.println(INVALID_COMMAND);
        }

        String command = args[0];

        if(command.equals(LIST_MESSAGING_NODES)){
            registry.list_messaging_nodes();
        }
        else if(command.equals(SETUP_OVERLAY)){
            setupOverlay(args);
        }
        else if(command.equals(LIST_ROUTING_TABLES)){
            registry.list_routing_tables();
        }
        else if(command.equals(START)){
            start(args);
        }
        else{
            printOptions();
        }
    }

}
package util;

import node.MessagingNode;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ydubale on 1/22/15.
 */
public class StatisticsCollectorAndDisplay {

    private static final String trafficSummaryHeader = "\n" +
            "Node\tPackets Sent\tPackets Recv\tPackets Relayed\tSum Values Sent\t\tSum Values Received";

    public static void printTrafficSummaryForAll(ConcurrentHashMap<Integer, MessagingNode> messNode){

        System.out.println(trafficSummaryHeader);

        int sumPacketSent = 0;
        int sumPacketRecv = 0;
        int sumPacketRelay = 0;
        long sumValuesSent = 0;
        long sumValuesRecv = 0;

        for(Integer nodeID : messNode.keySet()){
            MessagingNode mNode = messNode.get(nodeID);

            sumPacketSent += mNode.getOnodeRepTraffSum().getTotalSent();
            sumPacketRecv += mNode.getOnodeRepTraffSum().getTotalReceived().get();
            sumPacketRelay += mNode.getOnodeRepTraffSum().getTotalRelayed();
            sumValuesSent += mNode.getOnodeRepTraffSum().getSumSent();
            sumValuesRecv += mNode.getOnodeRepTraffSum().getSumReceived();

            System.out.println(nodeID + "\t" + mNode.getTrafficSummary());
        }

        String sumRow = "Sum\t" + sumPacketSent + "\t\t" + sumPacketRecv + "\t\t" +
                sumPacketRelay + "\t\t" + sumValuesSent + "\t\t" + sumValuesRecv;

        System.out.println(sumRow);
    }

}

package node;

import transport.TCPConnection;
import wireformats.Event;

/**
 * Created by ydubale on 1/22/15.
 */
public interface Node {

    public Event onEvent(Event event);
    public void startServer(int portNumber);
    public void addConnection(TCPConnection tcpC);


}

package node;

import wireformats.Event;

/**
 * Created by ydubale on 1/22/15.
 */
public interface Node {

    public void onEvent(Event event);
    public void startServer(int portNumber);

}

package node;

import wireformats.Event;

/**
 * Created by ydubale on 1/22/15.
 */
public interface Node {

    public Event onEvent(Event event);
}

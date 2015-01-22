/**
 * Created by ydubale on 1/22/15.
 */
public class EventFactory {
    private static EventFactory ourInstance = new EventFactory();

    public static EventFactory getInstance() {
        return ourInstance;
    }

    private EventFactory() {
    }
}

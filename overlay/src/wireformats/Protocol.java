package wireformats;
/**
 * Created by ydubale on 1/20/15.
 */
public interface Protocol {

    public static final byte OVERLAY_NODE_SENDS_REGISTRATION = 2;
    public static final byte REGISTRY_REPORTS_REGISTRATION_STATUS = 3;

    public static final byte REGISTRY_SENDS_NODE_MANIFEST = 4;
    public static final byte NODE_REPORTS_OVERLAY_SETUP_STATUS = 5;

    public static final byte REGISTRY_REQUESTS_TASK_INITIATE = 6;
    public static final byte OVERLAY_NODE_SENDS_DATA = 7;
    public static final byte OVERLAY_NODE_REPORTS_TASK_FINISHED = 8;

    public static final byte REGISTRY_REQUESTS_TRAFFIC_SUMMARY = 9;
    public static final byte OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY = 10;

}

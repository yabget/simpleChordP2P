package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class EventFactory {
    private static final EventFactory ourInstance = new EventFactory();

    public static EventFactory getInstance() {
        return ourInstance;
    }

    private EventFactory() {

    }

    public Event getEvent(byte[] data) {
        Event mainE = null;
        byte protocol = data[0];

        switch (protocol) {
            case Protocol.OVERLAY_NODE_SENDS_DATA:
                mainE = new OverlayNodeSendsData(data);
                break;
            case Protocol.OVERLAY_NODE_SENDS_REGISTRATION:
                mainE = new OverlayNodeSendsRegistration(data);
                break;
            case Protocol.REGISTRY_REPORTS_REGISTRATION_STATUS:
                mainE = new RegistryReportsRegistrationStatus(data);
                break;
            case Protocol.REGISTRY_SENDS_NODE_MANIFEST:
                mainE = new RegistrySendsNodeManifest(data);
                break;
            case Protocol.NODE_REPORTS_OVERLAY_SETUP_STATUS:
                mainE = new NodeReportsOverlaySetupStatus(data);
                break;
            case Protocol.REGISTRY_REQUESTS_TASK_INITIATE:
                mainE = new RegistryRequestsTaskInitiate(data);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TASK_FINISHED:
                mainE = new OverlayNodeReportsTaskFinished(data);
                break;
            case Protocol.REGISTRY_REQUESTS_TRAFFIC_SUMMARY:
                mainE = new RegistryRequestsTrafficSummary(data);
                break;
            case Protocol.OVERLAY_NODE_REPORTS_TRAFFIC_SUMMARY:
                mainE = new OverlayNodeReportsTrafficSummary(data);
                break;
            default:
                System.out.println("ERROR IN FACTORY!");
        }

        return mainE;
    }
}

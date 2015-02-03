package wireformats;

/**
 * Created by ydubale on 1/22/15.
 */
public class RegistrySendsNodeManifest implements Event, Runnable {

    public RegistrySendsNodeManifest(int routing_table_size, int num_all_nodes){

    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public byte getType() {
        return 0;
    }

    @Override
    public void run() {

    }
}

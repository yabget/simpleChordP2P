package wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ydubale on 2/13/15.
 */
public class ByteWriter {
    private ByteArrayOutputStream byteArrayOutputStream;
    private DataOutputStream dataOutputStream;
    private byte[] data;

    public ByteWriter(){
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(byteArrayOutputStream));
    }

    public void writeByte(byte toWrite){
        try {
            dataOutputStream.writeByte(toWrite);
        } catch (IOException e) {
            System.out.println("Problem writing byte.");
        }
    }

    public void writeInt(int toWrite){
        try {
            dataOutputStream.writeInt(toWrite);
        } catch (IOException e) {
            System.out.println("Problem writing int.");
        }
    }

    /**
     * Precondition: The length of the string has been written
     * @param toWrite - String to write
     */
    public void writeString(String toWrite){
        byte[] stringBytes = toWrite.getBytes();
        try {
            dataOutputStream.write(stringBytes);
        } catch (IOException e) {
            System.out.println("Problem writing string.");
        }
    }

    /**
     * Precondition: byteWriter is closed
     * @return the byteArrayOutputStream.toByteArray() result after close
     */
    public byte[] getBytes(){
        return data;
    }

    public void close(){
        try {
            dataOutputStream.flush();
            data = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            System.out.println("Problem closing writer.");
        }
    }


}

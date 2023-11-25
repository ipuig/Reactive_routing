package ie.tcd.scss.network;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * PathSequence
 *
 * The idea of this class is to get manageable paths
 * from address payloads, it expects a byte array that
 * contains at the beginning an integer that points the last
 * element position and followed by the destination and the rest
 * of addresses of the path.
 */
public class PathSequence {
    private int size;
    private int[] sequence;
    private String message;
    public static Charset charset = Charset.forName("UTF-8");

    public PathSequence(byte[] payload) {
        ByteBuffer buff = ByteBuffer.wrap(payload);
        size = buff.getInt();
        sequence = new int[size];
        for (int i = 0; i < size; i++) sequence[i] = buff.getInt();

        CharBuffer cbf = charset.decode(buff);
        message = cbf.toString().trim();
    }

    public int pop() {
        size--;
        int old = sequence[size];
        int[] newSequence = new int[size];
        for (int i = 0; i < size; i++) newSequence[i] = sequence[i];
        sequence = newSequence;
        return old;
    }

    public byte[] asPayload() {
        byte[] strBytes = message.getBytes(charset);
        ByteBuffer buff = ByteBuffer.allocate(Integer.BYTES + (size * Integer.BYTES) + (Character.BYTES *strBytes.length));
        buff.putInt(size);
        for (int i : sequence) buff.putInt(i);
        buff.put(strBytes);
        return buff.array();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for(int i: sequence) {
            sb.append(i);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString(); 
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public static PathSequence createWithNewMessage(byte[] first, String second) {
        byte[] strBytes = second.getBytes(charset);
        ByteBuffer buff = ByteBuffer.allocate(first.length + strBytes.length);
        buff.put(first);
        buff.put(strBytes);
        return new PathSequence(buff.array());
    }

    public String getMessage() {
        return message;
    }
}

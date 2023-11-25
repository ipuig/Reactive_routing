package ie.tcd.scss.network;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

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
    private byte[] message;

    public PathSequence(byte[] payload) {
        ByteBuffer buff = ByteBuffer.wrap(payload);
        size = buff.getInt();
        sequence = new int[size];
        for (int i = 0; i < size; i++) sequence[i] = buff.getInt();

        int messageSize = payload.length - size;
        message = new byte[messageSize];
        System.arraycopy(payload, size, message, 0, messageSize);
        System.out.println("message stored:" + message.length);
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
        ByteBuffer buff = ByteBuffer.allocate((size + 1) * Integer.SIZE);
        buff.putInt(size);
        for (int i : sequence) buff.putInt(i);
        byte[] path = buff.array();
        ByteBuffer newBuff = ByteBuffer.allocate(path.length + message.length);
        newBuff.put(path);
        newBuff.put(message);
        return newBuff.array();
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

    public static PathSequence createFromSplit(byte[] first, byte[] second) {
        ByteBuffer buff = ByteBuffer.allocate(first.length + second.length);
        buff.put(first);
        buff.put(second);
        return new PathSequence(buff.array());
    }

    public byte[] getMessage() {
        System.out.println(message.length);
        return message;
    }
}

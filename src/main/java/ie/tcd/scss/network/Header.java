package ie.tcd.scss.network;

import java.nio.ByteBuffer;

public class Header {

    private byte packetType;
    private int senderAddress;
    private short payloadLength;
    private final int HEADER_LENGTH = 7;


    public Header(byte packetType, int senderAddress, short payloadLength) {
        this.packetType = packetType;
        this.senderAddress = senderAddress;
        this.payloadLength = payloadLength;

    }

    public byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
        buffer.put(packetType);
        buffer.putInt(senderAddress);
        buffer.putShort(payloadLength);
        return buffer.array();
    }

    public static Header decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        byte type = buffer.get();
        int addr = buffer.getInt();
        short length = buffer.getShort();
        return new Header(type, addr, length);
    }

    public static byte[] getPayload(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get();
        buffer.getInt();
        buffer.getShort();
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload, 0, payload.length);
        return payload;
    }

    public byte getPacketType() {
        return packetType;
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public short getPayloadLength() {
        return payloadLength;
    }
}

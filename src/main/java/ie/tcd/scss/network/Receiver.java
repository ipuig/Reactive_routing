package ie.tcd.scss.network;

import java.net.DatagramPacket;

public abstract class Receiver implements Runnable {

    public byte receivedPacketType;
    public int receivedSenderAddress;
    public short receivedPayloadLength;
    public byte[] receivedPayload;
    public DatagramPacket receivedPacket;

    public Receiver(DatagramPacket receivedPacket) {
        this.receivedPacket = receivedPacket;
        if(receivedPacket != null) unpack(receivedPacket.getData());
    }

    public void unpack(byte[] data) {
        Header header = Header.decode(data);
        receivedPacketType = header.getPacketType();
        receivedSenderAddress = header.getSenderAddress();
        receivedPayloadLength = header.getPayloadLength();
        receivedPayload = Header.getPayload(data);
    }
}

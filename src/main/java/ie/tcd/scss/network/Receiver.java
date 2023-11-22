package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class Receiver implements Runnable {

    public byte receivedPacketType;
    public int receivedSenderAddress;
    public int receivedPort;
    public short receivedPayloadLength;
    public byte[] receivedPayload;
    public InetAddress receivedAddress;
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
        receivedPort = receivedPacket.getPort();
        receivedAddress = receivedPacket.getAddress();
    }
}

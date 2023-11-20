package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public abstract class Member extends NetworkDevice {

    private InetAddress applicationAddress;

    public Member(int port) {
        super(port);

        try {
            applicationAddress = InetAddress.getByName("172.17.0.2");
            connect();
        }
        catch(Exception e) {
            System.out.println("did not find the server");
            e.printStackTrace();
        }
    }

    public void connect() throws Exception {

        send(PacketType.LOG_IN, 0, new byte[0], applicationAddress);
        System.out.println("log in to the server...");

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        System.out.println("receiving...");

        ByteBuffer buff = ByteBuffer.wrap(Header.getPayload(packet.getData()));
        senderAddress = buff.getInt();
        System.out.println("Success!");
    }

    public void disconnect() throws Exception {
        Header header = new Header(PacketType.LOG_OUT.value(), 0, (short) Integer.SIZE);
        byte[] headerData = header.encode();
        DatagramPacket packet = new DatagramPacket(headerData, headerData.length, applicationAddress, MAIN_NODE_PORT);
        socket.send(packet);
    }
}

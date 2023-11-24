package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public abstract class Member extends NetworkDevice {

    public InetAddress applicationAddress;

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

    /**
     * sends a logging request to the server
     * and receives a random generated address
     * that then is set as the sender address of this node
     */
    public void connect() throws Exception {
        send(PacketType.LOG_IN, 0, new byte[0], applicationAddress, MAIN_NODE_PORT);
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        ByteBuffer buff = ByteBuffer.wrap(Header.getPayload(packet.getData()));
        senderAddress = buff.getInt();
    }

    public void stillConnected() {
        send(PacketType.CONNECTION_ACTIVE, senderAddress, new byte[0], applicationAddress, MAIN_NODE_PORT);
    }

    public void disconnect() {
        send(PacketType.CONNECTION_INACTIVE, senderAddress, new byte[0], applicationAddress, MAIN_NODE_PORT);
    }
    
    public void findPath(int addr) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE);
        buff.putInt(addr);
        sendBroadcast(PacketType.DISCOVER, senderAddress, buff.array());
    }

    private void backtrackAndAddToPath() {
        System.out.println("found");

    }

    public void processDiscover(int addr, byte[] payload) {
        ByteBuffer buff = ByteBuffer.wrap(payload);
        if(addr == buff.getInt()) backtrackAndAddToPath();
        else findPath(addr);
    }
}

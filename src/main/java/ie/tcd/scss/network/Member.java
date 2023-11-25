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
            devicePath.push(senderAddress);
        }
        catch(Exception e) {
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

    private void backtrackAndAddToPath(Receiver received, int addr) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE * 3);
        buff.putInt(2);
        buff.putInt(addr);
        buff.putInt(received.receivedSenderAddress);
        sendBroadcast(PacketType.PATH, senderAddress, buff.array());
    }

    public void processDiscover(Receiver received, int addr) {
        System.out.println("\ndiscover_" + received.receivedSenderAddress);
        ByteBuffer buff = ByteBuffer.wrap(received.receivedPayload);
        if(addr == buff.getInt()) {
            backtrackAndAddToPath(received, addr);
            return;
        } 
        sendBroadcast(PacketType.DISCOVER, senderAddress, received.receivedPayload);
    }

    public boolean isInPath(int addr, byte[] payload) {
        ByteBuffer buff = ByteBuffer.wrap(payload);
        int position = buff.getInt();
        buff.rewind();
        int value = 0;
        for (int i = 0; i <= position; i++) value = buff.getInt();
        return value == addr;
    }

    public boolean processMessage(Receiver receiver, int addr) {

        devicePath.clear();
        devicePath.push(senderAddress);

        if (receiver.receivedSenderAddress != addr) return false;

        var path = new PathSequence(receiver.receivedPayload);

        if (path.isEmpty()) return true;

        int next = path.pop();
        System.out.println("\nmsg_to_" + next);
        sendBroadcast(PacketType.MESSAGE, next, path.asPayload());
        return false;
    }

    public byte[] updatePayload(int addr, byte[] payload) {
        ByteBuffer oldBuff = ByteBuffer.wrap(payload);
        int position = oldBuff.getInt();
        ByteBuffer buff = ByteBuffer.allocate(payload.length + Integer.SIZE);
        buff.putInt(position + 1);
        for(int i = 1; i <= position; i++) buff.putInt(oldBuff.getInt());
        buff.putInt(addr);
        return buff.array();
    }

    public void backtrack(Receiver receiver, int addr) {

        if (addr == receiver.receivedSenderAddress) return;

        int prevAddr = devicePath.pop();

        if(prevAddr == addr) {
            devicePath.push(addr);
            return;
        }

        if (prevAddr == receiver.receivedSenderAddress) {
            backtrack(receiver, addr);
            return;
        }

        System.out.printf("\npath_backtracking_to_%d\n", prevAddr);
        byte[] payload = updatePayload(prevAddr, receiver.receivedPayload);
        sendBroadcast(PacketType.PATH, addr, payload);
    }

    public void printBuffer(byte[] p1) {
        ByteBuffer buff = ByteBuffer.wrap(p1);
        int n = buff.getInt();
        buff.rewind();
        for (int i = 0; i <= n; i++) {
            System.out.println(buff.getInt());
        }
    }
}

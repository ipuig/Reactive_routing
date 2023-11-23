package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class NetworkDevice {

    /**
     * This address represents the address that all my containers share
     * from my host. Since using it will defeat the purpose of reactive 
     * routing I am filtering it out.
     */
    public static String SHARED_BROADCAST_ADDR = "/172.17.255.255";
    public static final int ROUTER_PORT = 5000;
    public static final int ENDPOINT_PORT = 5001;
    public static final int MAIN_NODE_PORT = 6000;
    public static final int THREAD_POOL_SIZE = 20;
    public static final int BUFFER_SIZE = 1024;
    public static final int SERVER_GENERATED_ADDRESS_BOUND = Integer.MAX_VALUE;
    public static final int CONNECTION_REFRESH_RATE_IN_SECONDS = 5;


    public ExecutorService threadPool;
    public DatagramSocket socket;
    public int senderAddress;
    public ArrayList<InetAddress> broadcastAddresses;

    public NetworkDevice(int port) {
        broadcastAddresses = new ArrayList<>();
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            socket = new DatagramSocket(port);
            loadBroadcastAddresses();
        }
        catch(SocketException e) {
            e.printStackTrace();
        }
    }

    private boolean notLoop(NetworkInterface networkInterface) {
        try {
            return !networkInterface.isLoopback();
        }
        catch(Exception e) {
            return false;
        }
    }

    private void loadBroadcastAddresses() throws SocketException {
        NetworkInterface.networkInterfaces()
            .filter(net -> notLoop(net))
            .forEach(networkInterface -> {
                networkInterface.getInterfaceAddresses()
                    .stream()
                    .filter(address -> !address.getBroadcast().toString().equals(SHARED_BROADCAST_ADDR))
                    .map(address -> address.getBroadcast())
                    .forEach(broadcastAddresses::add);
            });
    }

    public void sendBroadcast(PacketType type, int id, byte[] payload) {
        Header header = new Header(type.value(), id, (short) payload.length);
        byte[] headerData = header.encode();
        ByteBuffer buff = ByteBuffer.allocate(headerData.length + payload.length);
        buff.put(headerData);
        buff.put(payload);
        byte[] data = buff.array();
        sendPacket(data);
    }

    public void send(PacketType type, int id, byte[] payload, InetAddress ip, int port) {
        Header header = new Header(type.value(), id, (short) payload.length);
        byte[] headerData = header.encode();
        ByteBuffer buff = ByteBuffer.allocate(headerData.length + payload.length);
        buff.put(headerData);
        buff.put(payload);
        byte[] data = buff.array();
        DatagramPacket toSend = new DatagramPacket(data, data.length, ip, port);

        try {
            socket.send(toSend);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    


    private void sendPacket(byte[] data) {
        broadcastAddresses.forEach(addr -> {
            try {
                DatagramPacket toRouter = new DatagramPacket(data, data.length, addr, ROUTER_PORT);
                DatagramPacket toEndpoint = new DatagramPacket(data, data.length, addr, ENDPOINT_PORT);
                socket.send(toRouter);
                socket.send(toEndpoint);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public abstract void receive();
    public abstract void run();

    static enum PacketType {
        DISCOVER(0), MESSAGE(1), PATH(2), DEAD_END(3), LOG_IN(4),
        CHECK_CONNECTION(5), CONNECTION_ACTIVE(6), CONNECTION_INACTIVE(7),
        RANDOM_ADDR(8), ACK(9), REQUEST_ENDPOINT_LIST(10), 
        ENDPOINT_LIST(11), NOT_SUPPORTED(100);

        byte value;

        PacketType(int value) {
            this.value = (byte) value;
        }

        public static PacketType fromInt(int n) {
            switch(n) {
                case 0: return DISCOVER;
                case 1: return MESSAGE;
                case 2: return PATH;
                case 3: return DEAD_END;
                case 4: return LOG_IN;
                case 5: return CHECK_CONNECTION;
                case 6: return CONNECTION_ACTIVE;
                case 7: return CONNECTION_INACTIVE;
                case 8: return RANDOM_ADDR;
                case 9: return ACK;
                case 10: return REQUEST_ENDPOINT_LIST;
                case 11: return ENDPOINT_LIST;
                default: return NOT_SUPPORTED;
            }
        }

        public byte value() {
            return value;
        }
    }

    public void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import ie.tcd.scss.app.User;

public class MainNode extends NetworkDevice {

    private ConcurrentHashMap<Integer, InetAddress> connections;
    private List<Integer> addressList;
    private List<User> users;

    public MainNode() {
        super(MAIN_NODE_PORT);
        connections = new ConcurrentHashMap<>();
        addressList = new ArrayList<>();
        users = new ArrayList<>();
    }

    @Override
    public void receive() {
        try {
            System.out.println("waiting to receive");
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println("received something");
            threadPool.submit(new ReceiverHandler(packet, addressList, connections));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Aplication listening at port " + MAIN_NODE_PORT + "...");
        try {
            System.out.println("IP: " + InetAddress.getLocalHost());
        }
        catch(Exception e) { }
        /* new Thread(() -> {
            while(true) System.out.println(addressList);
        }).start(); */
        while(true) receive();
    }

    private class ReceiverHandler extends Receiver {

        private List<Integer> addressList;
        private ConcurrentHashMap<Integer, InetAddress> connections;

        public ReceiverHandler(DatagramPacket receivedPacket, List<Integer> addressList, ConcurrentHashMap<Integer, InetAddress> connections) {
            super(receivedPacket);
            this.addressList = addressList;
            this.connections = connections;
        }

        @Override
        public void run() {
            System.out.println("Running receive");

            System.out.println(PacketType.fromInt(receivedPacketType));
            if (PacketType.fromInt(receivedPacketType) == PacketType.LOG_IN) {
                System.out.println("received logging request");
                int payload = generateRandomAddress();
                connections.put(payload, receivedPacket.getAddress());
                Header header = new Header(PacketType.RANDOM_ADDR.value(), 0, (short) Integer.SIZE);
                byte[] headerData = header.encode();
                ByteBuffer buff = ByteBuffer.allocate(headerData.length + Integer.SIZE);
                buff.put(headerData);
                buff.putInt(payload);
                byte[] data = buff.array();

                try {
                    DatagramPacket sending = new DatagramPacket(data, data.length, receivedPacket.getAddress(), receivedPacket.getPort());
                    socket.send(sending);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private int generateRandomAddress() {

            Random rnd = new Random();
            int value = rnd.nextInt(Integer.MAX_VALUE);

            if(addressList.contains(value)) return generateRandomAddress();
            addressList.add(value);
            return value;
        }
    }
}

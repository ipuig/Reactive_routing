package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class MainNode extends NetworkDevice {

    private ConcurrentHashMap<Integer, NodeInfo> connections;
    private ArrayBlockingQueue<Integer> active;
    private static final int CONNECTION_REFRESH_RATE_IN_SECONDS = 5;
    private static final int NUMBER_OF_DEVICES_ALLOWED = 30;

    public MainNode() {
        super(MAIN_NODE_PORT);
        connections = new ConcurrentHashMap<>();
    }

    @Override
    public void receive() {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            threadPool.submit(new ReceiverHandler(packet));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Aplication listening at port " + MAIN_NODE_PORT + "...");
        new Thread(() -> {

           while(true) {
               active = new ArrayBlockingQueue<>(NUMBER_OF_DEVICES_ALLOWED);
               System.out.printf("%d devices connected\n%s", 
                       connections.size(),
                       connections.isEmpty() ? "" : connections.keySet().toString() + "\n");

               delay(CONNECTION_REFRESH_RATE_IN_SECONDS);
               connections.elements()
                   .asIterator()
                   .forEachRemaining(this::checkConnection);
               updateConnections();
           } 

        }).start();
        while(true) receive();
    }

    private void updateConnections() {
        if (active.isEmpty()) {
            connections.clear();
            return;
        }

        connections.forEach((k, v) -> {
            if (!active.contains(k)) connections.remove(k);
        });
    }

    private void checkConnection(NodeInfo nodeInfo) {
        send(PacketType.CHECK_CONNECTION, SERVER_GENERATED_ADDRESS_BOUND, new byte[0], nodeInfo.getAddress(), nodeInfo.getPort());
    }

    private void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private class ReceiverHandler extends Receiver {

        public ReceiverHandler(DatagramPacket receivedPacket) {
            super(receivedPacket);
        }

        @Override
        public void run() {

            switch(PacketType.fromInt(receivedPacketType)) {
                case LOG_IN:
                    System.out.println("----------------------------");
                    sendGeneratedAddress();
                    break;

                case CONNECTION_ACTIVE:
                    active.add(receivedSenderAddress);
                    break;

                case CONNECTION_INACTIVE:
                    System.out.println(senderAddress + " disconnected");
                    connections.remove(receivedSenderAddress);
                    break;

                case ACK:
                    break;

                default:
                    break;
            }
        }

        private void sendGeneratedAddress() {
            int generatedAddr = generateRandomAddress();
            connections.put(generatedAddr, new NodeInfo(receivedAddress, receivedPort));
            active.add(generatedAddr);
            System.out.printf("Received log request from %s\nreal address=%s\ngenerated=%s\n",
                    receivedPort == ENDPOINT_PORT ? "endpoint" : "router", 
                    receivedAddress.toString().substring(1), generatedAddr);

            ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE);
            buff.putInt(generatedAddr);
            byte[] payload = buff.array();
            send(PacketType.RANDOM_ADDR, SERVER_GENERATED_ADDRESS_BOUND, payload, receivedAddress, receivedPort);
        }

        /**
         * Generates a random address for the devices that connect to this node
         * I am using Integer.MAX_VALUE to minimise the number of recursions for
         * for this method, and since rnd.nextInt(limit) is exclusive, I can
         * use Integer.MAX_VALUE to represent the address for this device.
         */
        private int generateRandomAddress() {
            Random rnd = new Random();
            int value = rnd.nextInt(Integer.MAX_VALUE);
            if(connections.containsKey(value)) return generateRandomAddress();
            return value;
        }

    }
}

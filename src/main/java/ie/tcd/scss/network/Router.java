package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Router extends Member {

    public Router() {
        super(ROUTER_PORT);
    }

    public void run() {
        System.out.println("router no-" + senderAddress);
        while(true)
            receive();
    }


    public void receive() {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            threadPool.submit(new ReceiverHandler(packet));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ReceiverHandler extends Receiver {

        public ReceiverHandler(DatagramPacket packet) {
            super(packet);
        }

        public void run() {

            switch(PacketType.fromInt(receivedPacketType)) {
                case MESSAGE:
                    System.out.println("Received a message from " + receivedSenderAddress + " forwarding...");
                    send(PacketType.DISCOVER, senderAddress, receivedPayload);
                    break;

                case DISCOVER:
                    System.out.printf("Received a discover from %d, forwarding...\n", receivedSenderAddress);
                    send(PacketType.DISCOVER, senderAddress, receivedPayload);
                    break;

                case PATH:
                    System.out.println("Received a path from " + receivedSenderAddress);
                    break;

                case DEAD_END:
                    System.out.println("Received a dead end from " + receivedSenderAddress);
                    break;
                
                case RANDOM_ADDR:
                    System.out.println("heya");
                    break;

                case ACK:
                    break;

                default:
                case NOT_SUPPORTED:
                    break;
            }

        }

    }
}

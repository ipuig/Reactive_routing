package ie.tcd.scss.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Scanner;

public class Endpoint extends Member {

    public Endpoint() {
        super(ENDPOINT_PORT);
    }

    public void run() {
        new Thread(() -> {while(true) receive();}).start();
        System.out.println("endpoint no-" + senderAddress);
        Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Payload: ");
            byte[] payload = in.nextLine().getBytes();
            send(PacketType.MESSAGE, senderAddress, payload);
        }
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
                    break;

                case DISCOVER:
                    if (receivedSenderAddress != senderAddress) {
                        System.out.println("Received a discover from " +  receivedSenderAddress);
                        System.out.println("I received this message: " + new String(receivedPayload));
                    }
                    break;

                case PATH:
                    System.out.println("Received a path from " + receivedSenderAddress);
                    break;

                case DEAD_END:
                    System.out.println("Received a dead end from " + receivedSenderAddress);
                    break;

                case RANDOM_ADDR:
                    System.out.println("heya i received the new address");
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

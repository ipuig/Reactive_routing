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
                    break;

                case DISCOVER:
                    break;

                case PATH:
                    break;

                case DEAD_END:
                    break;
                
                case RANDOM_ADDR:
                    break;

                case CHECK_CONNECTION:
                    stillConnected();
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

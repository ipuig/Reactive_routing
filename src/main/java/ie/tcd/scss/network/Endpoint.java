package ie.tcd.scss.network;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;


import ie.tcd.scss.gui.Host;

public class Endpoint extends Member {

    private List<Integer> otherHostsAddress;
    private Host gui;


    public Endpoint() {
        super(ENDPOINT_PORT);
        otherHostsAddress = new ArrayList<>();
    }

    public void run() {
        new Thread(() -> {while(true) receive();}).start();
        System.out.println("endpoint no-" + senderAddress);
        EventQueue.invokeLater(() -> {
            gui = new Host(this);
        });
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

    public void requestEndpointList() {
        System.out.println("looking for other endpoints...");
        send(PacketType.REQUEST_ENDPOINT_LIST, senderAddress, new byte[0], applicationAddress, MAIN_NODE_PORT);
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
                    System.out.println("Random address received");
                    break;

                case CHECK_CONNECTION:
                    stillConnected();
                    break;

                case ENDPOINT_LIST:
                    populateHosts();
                    break;

                case ACK:
                    break;

                default:
                case NOT_SUPPORTED:
                    break;

            }
        }

        private void populateHosts() {
            otherHostsAddress = new ArrayList<>();
            ByteBuffer payloadBuffer = ByteBuffer.wrap(receivedPayload);
            int numberOfHosts = payloadBuffer.getInt();
            for (int i = 0; i < numberOfHosts; i++) otherHostsAddress.add(payloadBuffer.getInt());
        }
    }

    public List<Integer> getOtherHostsAddress() {
        return otherHostsAddress;
    }

}

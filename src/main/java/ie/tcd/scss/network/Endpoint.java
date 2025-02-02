package ie.tcd.scss.network;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import ie.tcd.scss.gui.Host;

public class Endpoint extends Member {

    private List<Integer> otherHostsAddress;
    private Host gui;
    private int sendingTo;
    private String msg;

    public Endpoint() {
        super(ENDPOINT_PORT);
        otherHostsAddress = new ArrayList<>();
        sendingTo = Integer.MAX_VALUE;
    }

    public void run() {
        new Thread(() -> {
            while(true) receive();
        }).start();

        new Thread(() -> {
            while(true) {
                requestEndpointList();
                delay(CONNECTION_REFRESH_RATE_IN_SECONDS / 2);
            }
        }).start();

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
        send(PacketType.REQUEST_ENDPOINT_LIST, senderAddress, new byte[0], applicationAddress, MAIN_NODE_PORT);
    }

    private class ReceiverHandler extends Receiver {

        public ReceiverHandler(DatagramPacket packet) {
            super(packet);
        }

        public void run() {

            switch(PacketType.fromInt(receivedPacketType)) {

                case MESSAGE:

                    if(processMessage(this, senderAddress)) {

                        var endPath = new PathSequence(receivedPayload);
                        String msg = endPath.getMessage();
                        System.out.println("received the message: " + msg);
                        gui.receiveMessage(msg);

                    }

                    break;

                case DISCOVER:
                    if (!devicePath.contains(receivedSenderAddress)) {
                        devicePath.push(receivedSenderAddress);
                        processDiscover(this, senderAddress);
                    }
                    break;

                case PATH:
                    if (isSender()) {
                        devicePath.clear();
                        devicePath.push(senderAddress);
                        sendMessage(receivedPayload, msg);
                    }
                    else if (isInPath(senderAddress, receivedPayload)) backtrack(this, senderAddress);

                case DEAD_END:
                    break;

                case RANDOM_ADDR:
                    break;

                case CHECK_CONNECTION:
                    stillConnected();

                    break;

                case ENDPOINT_LIST:
                    populateHosts();
                    updateHostView();
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
            for (int i = 0; i < numberOfHosts; i++) {
                final int currentAddress = payloadBuffer.getInt();
                if (currentAddress != senderAddress)
                    otherHostsAddress.add(currentAddress);
            }
        }

        private boolean isSender() {
            ByteBuffer buff = ByteBuffer.wrap(receivedPayload);
            buff.getInt(); // position
            return buff.getInt() == sendingTo;
        }
    }

    private void updateHostView() {
        DefaultListModel<Integer> lm = new DefaultListModel<Integer>();
        lm.addAll(otherHostsAddress);
        gui.availableHosts.setModel(lm);
    }

    private void sendMessage(byte[] payload, String msg) {
        var path = PathSequence.createWithNewMessage(payload, msg);
        if (path.pop() == senderAddress) {
            System.out.println("Found path within " + path.getSize() + " hops");
            sendBroadcast(PacketType.MESSAGE, path.pop(), path.asPayload());
        }

    }

    public void prepareMessage(int addr, String msg) {
        this.sendingTo = addr;
        this.msg = msg;
    }
}

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;

public class Router extends NetworkDevice {

    private ConcurrentHashMap<String, Short> routingTable;

    public Router(int port) {
        super(port);
        this.receiver = () -> {
            // System.out.println(socket);
            // byte[] data = this.socket.receive();
            // System.out.println(new String(data));
        };
    }

    public void discover() {
    }

    public void send() {

    }

    public void receive() {
        try {
            byte[] receiveData = new byte[1024];
            this.packageToReceive = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(packageToReceive);
            threadPool.submit(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start() {
        while(true) receive();
    }

}


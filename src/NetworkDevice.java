import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class NetworkDevice {

    public static final int THREAD_POOL_SIZE = 10;
    public DatagramSocket socket;
    public DatagramPacket packageToSend;
    public DatagramPacket packageToReceive;
    public ExecutorService threadPool;

    public ReceiveHandler receiver;
    private final int BUFFER_SIZE = 1024;

    public NetworkDevice(int port) {
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try {
            this.socket = new DatagramSocket(port);
            this.socket.setBroadcast(true);
        }
        catch(Exception e) {
            e.printStackTrace();
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

    abstract void start();
    abstract void send();

    public void receive() {
        try {
            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            threadPool.submit(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

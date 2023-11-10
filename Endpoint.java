import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Endpoint {


    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(5001, InetAddress.getByName("0.0.0.0"));
        socket.setBroadcast(true);

        while(true) {
            byte[] payload = "Message from the endpoint 2".getBytes();
            DatagramPacket packet = new DatagramPacket(payload, payload.length, InetAddress.getByName("0.0.0.0"), 5000);
            socket.send(packet);
        }


        
    }
}

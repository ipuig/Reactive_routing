import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Router {


    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(5000, InetAddress.getByName("0.0.0.0"));
        socket.setBroadcast(true);

        while(true) {
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(packet);
            System.out.println(new String(packet.getData()).trim());
        }


        
    }
}

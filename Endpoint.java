import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.stream.Stream;
import java.util.ArrayList;

public class Endpoint {

    public static String SHARED_BROADCAST_ADDR = "/172.17.255.255";

    public static boolean compareAddr(byte[] addr1, byte[] addr2) {
        if(addr1.length != addr2.length) return false;
        for(int i = 0; i < addr1.length; i ++) if(addr1[i] != addr2[i]) return false;
        return true;
    }

    public static boolean notLoop(NetworkInterface networkInterface) {
        try {
            return !networkInterface.isLoopback();
        }
        catch(Exception e) {
            return false;
        }
    }


    public static void main(String[] args) throws Exception {

        DatagramSocket socket = new DatagramSocket(5001);
        socket.setBroadcast(true);

        ArrayList<InetAddress> broadcastAddresses = new ArrayList<>();
        
        NetworkInterface.networkInterfaces()
            .filter(net -> notLoop(net))
            .forEach(networkInterface -> {
                networkInterface.getInterfaceAddresses()
                    .stream()
                    .filter(address -> !address.getBroadcast().toString().equals(SHARED_BROADCAST_ADDR))
                    .map(address -> address.getBroadcast())
                    .forEach(broadcastAddresses::add);
            });

        System.out.println(broadcastAddresses);




        while(true) {
            byte[] payload = "Message from the endpoint 2".getBytes();
            broadcastAddresses.forEach(addr -> {
                DatagramPacket packet = new DatagramPacket(payload, payload.length, addr, 5000);
                try {
                    socket.send(packet);

                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }
}

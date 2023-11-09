import java.net.DatagramPacket;

public class Endpoint extends NetworkDevice {

    public Endpoint() {
        super();
    }

    public void start() {


        String message = "something to send";
        byte[] buffer = message.getBytes();

        packageToSend = new DatagramPacket(buffer, buffer.length);
        try {
            socket.setBroadcast(true);
            socket.send(packageToSend);
            socket.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void send() {

    }
    
}

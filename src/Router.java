import java.net.DatagramPacket;

public class Router extends NetworkDevice {
    
    public Router() {
        super();
        receiver = () -> {
            System.out.println(socket);
        };
    }

    public void start() {
        while(true) receive();
    }

    public void send() {

    }


}


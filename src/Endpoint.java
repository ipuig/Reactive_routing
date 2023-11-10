import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

public class Endpoint extends NetworkDevice {

    public Endpoint(int port) {
        super(port);
    }

    public void start() {
        while(true) {
            send();
            delay(5);
        }
    }

    public void send() {
        String payload = UserInterface.userInput();
        try {
            InetAddress addr1 =  InetAddress.getByName("192.168.1.255");
            InetAddress addr2 =  InetAddress.getByName("192.168.2.255");
            packageToSend = new DatagramPacket(payload.getBytes(), payload.length(), addr1, 0);
            socket.send(packageToSend);
            packageToSend = new DatagramPacket(payload.getBytes(), payload.length(), addr2, 0);
            socket.send(packageToSend);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    private class UserInterface {

        public static String userInput() {
            try(Scanner in = new Scanner(System.in)) {
                return in.nextLine();
            }
            catch(Exception e) {
                e.printStackTrace();
                return userInput();
            }

        }

    }
    
}

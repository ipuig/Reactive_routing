package ie.tcd.scss;

import ie.tcd.scss.network.Endpoint;
import ie.tcd.scss.network.MainNode;
import ie.tcd.scss.network.NetworkDevice;
import ie.tcd.scss.network.Router;
import ie.tcd.scss.app.User;

public class Application {

    public static void main(String[] args) {

        NetworkDevice device;
        if(args[0].equals("endpoint")) device = new Endpoint();
        else if (args[0].equals("router")) device = new Router();
        else device = new MainNode();
        device.run();
    }
}

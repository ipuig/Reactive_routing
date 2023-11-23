package ie.tcd.scss.network;

import java.net.InetAddress;

public class NodeInfo {

    private int id;
    private InetAddress address;
    private int port;
    private boolean active;

    public NodeInfo(int id, InetAddress address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

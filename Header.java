import java.nio.ByteBuffer;

public class Header {
    private byte packetType;

    // Constructor to initialize header fields
    public Header() {
    }

    public Header(byte packetType, short senderAddress) {
        this.packetType = packetType;
        this.senderAddress = senderAddress;

    }

    // Encode the header into a byte array
    public byte[] encode() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH);
        return buffer.array();
    }

    // Decode a byte array into a header object
    public static Header decode(byte[] data) {
        return new Header();
    }
    
    // Getter methods for header fields

    static enum PACKET_TYPE {
        DISCOVER, MESSAGE, PATH, DEAD_END;

        public static PACKET_TYPE fromInt(int n) {
            if(n == 0) return DISCOVER;
            if (n == 1) return MESSAGE;
            if (n == 2) return PATH;
            return DEAD_END;
        }
    }
}

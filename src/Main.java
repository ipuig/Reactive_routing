public class Main {

    enum InstanceType {
        ENPOINT, ROUTER, NOTHING;

        public static InstanceType fromInteger(int value) {
            if(value == 1) return ENPOINT;
            if(value == 0) return ROUTER;
            return NOTHING;
        }
    }

    public static InstanceType processArgs(String[] args) {
        try {
            return InstanceType.fromInteger(Integer.parseInt(args[0]));
        }
        catch(Exception e) {
            System.out.println("Invalid input");
            return InstanceType.NOTHING;
        }

    }
    
    public static void main(String[] args) {

        NetworkDevice device;

        switch(processArgs(args)) {
            case ENPOINT:
                device = new Endpoint();
                device.start();
                break;

            case ROUTER:
                System.out.println("Running router");
                device = new Router();
                device.start();
                break;

            default:
            case NOTHING:
                break;
        }
    }

}

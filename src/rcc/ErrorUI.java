package rcc;


public class ErrorUI {
    
    public static final int NETWORK_SETUP_FAILURE = 1;
    public static final int NETWORK_FAILURE = 2;
    
    public static void display(Exception e, int errorType){
        switch(errorType){
            case NETWORK_SETUP_FAILURE:
                System.err.println("Network setup failure. System will exit.");
                break;
            case NETWORK_FAILURE:
                System.err.println("Network connection has failed. System will exit.");
                break;
            default:
                System.err.println("Unknown error. System will exit.");
                break;
        }
        System.err.println(e);
    }
    
    public static void display(Exception e, String message){
        System.err.println("ERROR: " + message);
    }
}

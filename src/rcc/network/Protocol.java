package rcc.network;


public class Protocol {
        
    /* PROTOCOL VARIABLES: Version 1 */
    public static final int PROTOCOL_VERSION = 1;
    public static final int USERTYPE_CLIENT = 2;
    
    public static final int DATATYPE_CONNECTION_REQUEST = 1;
    public static final int CONNECTION_REQUEST_LENGTH = 0;
    
    public static final int DATATYPE_REJECT = 2;
    public static final int REJECT_LENGTH = 0;
    
    public static final int DATATYPE_ERROR = 3;
    public static final int ERROR_LENGTH = 4;
    
    public static final int DATATYPE_DISCONNECT = 4;
    public static final int DISCONNECT_LENGTH = 0;
    
    public static final int DATATYPE_REQUEST_LOGIN_INFO = 5;
    public static final int REQUEST_LOGIN_INFO_LENGTH = 0;
    
    public static final int DATATYPE_LOGIN_INFO = 6;
    
    public static final int DATATYPE_PHASE_CHANGE = 10;
    public static final int PHASE_CHANGE_LENGTH = 1;
    
    public static final int DATATYPE_ROBOT_LIST = 11;
    
    public static final int DATATYPE_ROBOT_SELECTION = 12;
    
    public static final int DATATYPE_REQUEST_STREAM_SOCKET_INFO = 20;
    public static final int REQUEST_STREAM_SOCKET_INFO_LENGTH = 0;
    
    public static final int DATATYPE_STREAM_SOCKET_INFO = 21;
    
    public static final int DATATYPE_RESET = 30;
    public static final int RESET_LENGTH = 0;
    
    public static final int DATATYPE_SHUTDOWN = 31;
    public static final int SHUTDOWN_LENGTH = 0;
    
    public static final int DATATYPE_COMMAND = 100;
    public static final int COMMAND_LENGTH = 2;
        
    //COMMAND DEFINITIONS:
    public static final int PRESS_W = 1;
    public static final int RELEASE_W = 2;
    
    public static final int PRESS_S = 3;
    public static final int RELEASE_S = 4;
    
    public static final int PRESS_A = 5;
    public static final int RELEASE_A = 6;
    
    public static final int PRESS_D = 7;
    public static final int RELEASE_D = 8;
    
    public static final int CAMERA_UP_DOWN = 10;
    public static final int CAMERA_LEFT_RIGHT = 11;
    
    /* PROTOCOL VARIABLES DONE */
    
}


//Header "struct"
class Header{
    char version;
    char type;
    int size;
    public Header(){
        
    }
}
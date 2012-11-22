package rcc.network;
import rcc.ErrorUI;
import rcc.RCC;
import rcc.video.Screen;

import java.net.InetAddress;


public class NetworkManager {
    
    private RCC rcc;
    public Connector conn;
    private boolean active;
    
    public String ipAddress;
    public String port;
    public String localAddr;
    
    public String lasterr;
    public NetworkThread net;
    
    public NetworkManager(Screen screen, RCC rcc){
        this.rcc = rcc;
        net = new NetworkThread(screen); //TODO: Fix this
        active = false;
        ipAddress = rcc.settings.serverIPAddress;
        port = rcc.settings.serverTCPPort;
        try{
            localAddr = InetAddress.getLocalHost().getHostAddress();
        }catch(Exception e){
            localAddr = "0.0.0.0";
        }
        lasterr = "Some fail.";
    }
    
    public boolean connect(String username, String password){
        if(username.length() == 0){
            lasterr = "ERROR: No username provided.";
            return false;
        }
        else if(password.length() == 0){
            lasterr = "ERROR: No password provided.";
            return false;
        }
        
        int intPort = 0;
        if(ipAddress.length() < 1){
            lasterr = "ERROR: No IP address provided. Check CONFIG options.";
            return false;
        }
        try{
            intPort = Integer.parseInt(port);
        }catch(Exception e){
            lasterr = "ERROR: Port must be a number. Check CONFIG options.";
            ErrorUI.display(e, "Port must be a number.");
            return false;
        }
        if(intPort < 1 || intPort > 65535){
            lasterr = "ERROR: Port must be between 1 and 65535." +
                    " Check CONFIG options.";
            return false;
        }
        conn = new Connector(rcc,
                username, password,
                ipAddress, intPort);
        conn.start();
        active = true;
        return true;
    }
    
    public void cancelConnect(){
        if(active){
            conn.proceed = false;
            active = false;
        }
    }
    
    public void disconnect(){
        active = false;
    }
}

/*   
    public String lasterr;
    
    private Screen screen;
    private Socket robot;
    private ConnectionThread conn;
    public boolean connected;
    
    
    //CONSTRUCTOR (basic setup of screen and connected status).
    public NetworkManager(Screen screen){
        this.screen = screen;
        this.connected = false;
    }
    
    
    //given an IP address and port number (in string form),
    // will attempt to convert the IP and Port to valid formats,
    // and call the below connect method.
    //RETURNS true if connection successful, false otherwise.
    public boolean connect(String address, String port){
        int intPort = 0;
        try{
            intPort = Integer.parseInt(port);
        }catch(Exception e){
            ErrorUI.display(e, "Port must be a number.");
            return false;
        }
        return connect(address, intPort);
    }
    
    
    
    //given a IP address and port number, will attempt to
    // connect to the server.
    //RETURNS true if connection successful, false otherwise.
    // also returns false if already connected.
    public boolean connect(String address, int port){
        if(connected)
            return false;
        
        try{
            robot = new Socket(address, port);
            connected = true;
            conn = new ConnectionThread(this, robot, screen);
            conn.start();
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_SETUP_FAILURE);
            lasterr = e.getMessage();
            return false;
        }
        return true;
    }
    
    
    //sets the connected value to force, causing any
    // running threads to stop processing.
    public void disconnect(){
        if(connected){
            connected = false;
            conn.disconnect();
        }
    }
}
*/
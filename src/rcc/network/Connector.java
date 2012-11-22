package rcc.network;
import rcc.controls.SelectionControls;
import rcc.RCC;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class Connector extends Thread {
    
    private RCC rcc;
    private String username;
    private String password;
    private String address;
    private int port;
    public boolean proceed;
    
    private boolean robotChosen;
    private boolean active;
    
    private static final String DC = "Connection to server was lost.";
    private static final String PM = "Protocol error: unexpected data received.";
    
    private Socket server;
    private DataInputStream serverIn;
    private DataOutputStream serverOut;
    private Header head;
    private boolean getRobotList;
    private String lasterr;
    
    
    private UDPClient udpClient;
    
    
    public Connector(RCC rcc,
            String username, String password,
            String address, int port){
        this.rcc = rcc;
        this.username = username;
        this.password = password;
        this.address = address;
        this.port = port;
        this.proceed = true;
        this.head = new Header();
        this.lasterr = "";
        robotChosen = false;
        active = false;
        getRobotList = true;
    }
    
    
    @Override
    //Run the thread (handle all connections)
    public void run(){
        boolean result = connectToServer();
        if(proceed){
            if(result){
                result = runPhaseTwo();
                if(result){
                    while(result){
                        result = runPhaseThree();
                        if(result){
                            result = runPhaseTwo();
                        }
                    }
                }
            }
            rcc.mainPanel.setToInitControls(lasterr);
        }
        
        //if thread dies without return, close server.
        active = false;
        try{
            serverIn.close();
            serverOut.close();
            server.close();
        }catch(Exception e){
            //do nothing
        }
    }
    
    
    //PHASE 3: general send-listen phase three protocol
    //RETURNS: True if continuing, false if done (disconnect).
    private boolean runPhaseThree(){
        rcc.mainPanel.setToMainControls();
        rcc.mainPanel.screen.removeDisconnectedMessage();
        rcc.mainPanel.screen.setToLoading();
                    
        //start listening on UDP client:
        udpClient.start();
        active = true;
                    
        //listen for server information here:
        int readResult = 0;
        while(active){
            try{
                readResult = receiveHeader(head);
                if(readResult < 0){
                    if(active)
                        lasterr = DC;
                    break;
                }
                            
                if(head.type == (char)Protocol.DATATYPE_DISCONNECT){
                    lasterr = "Connection closed by server.";
                    break;
                }
                else if(head.type == (char)Protocol.DATATYPE_RESET){
                    lasterr = "Connection reset.";
                    udpClient.terminate();
                    return true;
                }else if(head.type == (char)Protocol.DATATYPE_ROBOT_LIST){
                    getRobotList = false;
                    udpClient.terminate();
                    return true;
                }
            }catch(Exception e){
                lasterr = "Socket read ERROR: " + e.getMessage();
                break;
            }
        }
                    
        //handle disconnection:                    
        udpClient.terminate();
        rcc.mainPanel.setToInitControls(lasterr);
        return false;
    }
    
    
    //PHASE 3: send command to the server (if socket is already active!)
    public boolean sendCommand(int cmd, int degrees){
        if(active){
            int result = sendHeader((char)Protocol.DATATYPE_COMMAND,
                    Protocol.COMMAND_LENGTH);
            if(result < 0)
                return false;
            byte[] buffer = new byte[2];
            buffer[0] = (byte)cmd;
            buffer[1] = (byte)(char)degrees;
            result = sendData(buffer);
            if(result < 0)
                return false;
            else
                return true;
        }
        return false;
    }
    
    //PHASE 3: send disconnect message:
    public boolean disconnect(){
        if(active){
            active = false;
            int result = sendHeader((char)Protocol.DATATYPE_DISCONNECT,
                    Protocol.DISCONNECT_LENGTH);
            if(result < 0)
                return false;
            try{
                server.close();
            }catch(Exception e){
                try{
                    this.interrupt();
                }catch(Exception e2){}
            }
            lasterr = "Connection closed by user.";
        }
        return true;
    }
    
    //PHASE 3: send a reset message
    public boolean reset(){
        if(active){
            active = false;
            int result = sendHeader((char)Protocol.DATATYPE_RESET,
                    Protocol.RESET_LENGTH);
            if(result < 0)
                return false;
        }
        return true;
    }
    
    //PHASE 3: send shutdown message:
    public boolean sendShutdown(){
        //this needs fixing on both ends
        if(active){
            int result = sendHeader((char)Protocol.DATATYPE_SHUTDOWN,
                    Protocol.SHUTDOWN_LENGTH);
            if(result < 0)
                return false;
            //active = false;
        }
        return true;
    }
    
    
    //PHASE 2: general send-listen phase two protocol.
    private boolean runPhaseTwo(){
        
        //collect robot list from server:
        int result;
        if(getRobotList){
            result = receiveHeader(head);
            if(result < 0){
                lasterr = DC;
                return false;
            }
        }
        
        if(head.type != (char)Protocol.DATATYPE_ROBOT_LIST){
            lasterr = PM;
            return false;
        }
                
        byte[] buffer = new byte[head.size];
        result = receiveData(buffer);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        ArrayList<String> robots = new ArrayList<String>();
        String robotID = "";
        for(int i=0; i<buffer.length; i++){
            if((char)buffer[i] == (char)0){
                robots.add(robotID);
                robotID = "";
            }
            else{
                robotID += (char)buffer[i];
            }
        }
        
        SelectionControls selCtrls = new SelectionControls(this, robots);
        rcc.mainPanel.setToSelectionControls(selCtrls);
        robotChosen = false;
        
        //now listen for more headers:
        while(true){
            result = receiveHeader(head);
            if(result < 0){
                lasterr = DC;
                return false;
            }
            
            if(head.type == (char)Protocol.DATATYPE_ROBOT_LIST){
                buffer = new byte[head.size];
                result = receiveData(buffer);
                if(result < 0){
                    lasterr = DC;
                    return false;
                }
                
                robots.clear();
                robotID = "";
                for(int i=0; i<buffer.length; i++){
                    if((char)buffer[i] == (char)0){
                        robots.add(robotID);
                        robotID = "";
                    }
                    else{
                        robotID += (char)buffer[i];
                    }
                }
                selCtrls.updateList(robots);
            }else{
                break;
            }
        }
        
        rcc.mainPanel.setToWaitControls(1);
        
        //expecing REQUEST STREAM SOCKET INFO or RESETS
        //  (resets viable after this point)
        if(head.type != (char)Protocol.DATATYPE_REQUEST_STREAM_SOCKET_INFO){
            if(head.type == (char)Protocol.DATATYPE_RESET){
                return runPhaseTwo();
            }
            else if(head.type == (char)Protocol.DATATYPE_REJECT){
                lasterr = "Selection cancelled.";
            }else{
                lasterr = PM;
            }
            return false;
        }
        
        //setup UDP client and send UPD stream data here
        udpClient = new UDPClient(rcc.mainPanel.screen);
        udpClient.setup();
        String udpAddr = rcc.netManager.localAddr;
        int udpPort = udpClient.getLocalPort();
        
        result = sendHeader((char)Protocol.DATATYPE_STREAM_SOCKET_INFO,
                udpAddr.length() + 4);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        //create and send address information
        buffer = new byte[udpAddr.length() + 4];
        for(int i=0; i<udpAddr.length(); i++){
            buffer[i] = (byte)udpAddr.charAt(i);
        }
        buffer[udpAddr.length()] = (byte)(udpPort);
        buffer[udpAddr.length() + 1] = (byte)(udpPort >>> 8);
        buffer[udpAddr.length() + 2] = (byte)(udpPort >>> 16);
        buffer[udpAddr.length() + 3] = (byte)(udpPort >>> 24);
        
        result = sendData(buffer);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        //receive new phase change:
        result = receiveHeader(head);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        if(head.type != (char)Protocol.DATATYPE_PHASE_CHANGE){
            if(head.type == (char)Protocol.DATATYPE_RESET){
                return runPhaseTwo();
            }
            else
                lasterr = PM;
            return false;
        }
        
        //recieve phase number
        buffer = new byte[1];
        result = receiveData(buffer);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        if(buffer[0] != (char)3){
            lasterr = "Incorrect phase switch: Protocol Mismatch.";
            return false;
        }
        
        return true;
    }
    
    
    //PHASE 2 send selection (called by GUI client),
    //  and forces continuing of PHASE 2.
    public void sendSelection(String selection){
        if(!robotChosen){
            int result = sendHeader((char)Protocol.DATATYPE_ROBOT_SELECTION,
                    selection.length());
            if(result < 0){
                lasterr = DC;
                rcc.mainPanel.setToInitControls(lasterr);
            }
        
            byte[] buffer = new byte[selection.length()];
            for(int i=0; i<selection.length(); i++){
                buffer[i] = (byte)selection.charAt(i);
            }
            result = sendData(buffer);
            if(result < 0){
                lasterr = DC;
                rcc.mainPanel.setToInitControls(lasterr);
            }
            
            robotChosen = true;
        }
    }
    
    
    //PHASE 2 send disconnect (called by GUI cancel)
    //  and sends a message to the server to DC and reply
    //  a reject.
    public void sendDisconnect(){
        int result = sendHeader((char)Protocol.DATATYPE_DISCONNECT,
                    Protocol.DISCONNECT_LENGTH);
        if(result < 0){
            lasterr = DC;
            rcc.mainPanel.setToInitControls(lasterr);
        }
    }
    
    
    //PHASE 1: Connect to server.
    private boolean connectToServer(){
        try{
            server = new Socket(address, port);
            serverIn = new DataInputStream(server.getInputStream());
            serverOut = new DataOutputStream(server.getOutputStream());
        }catch(Exception e){
            lasterr = "Connection error: " + e.getMessage();
            return false;
        }
        
        //connection request:
        int result = sendHeader((char)Protocol.DATATYPE_CONNECTION_REQUEST,
                Protocol.CONNECTION_REQUEST_LENGTH);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        //reject or accept
        result = receiveHeader(head);
        if(head.type != (char)Protocol.DATATYPE_REQUEST_LOGIN_INFO){
            lasterr = "Connection rejected by server.";
            return false;
        }
        
        //send login info
        int len = (2 + username.length() + password.length());
        result = sendHeader((char)Protocol.DATATYPE_LOGIN_INFO,
                len);
        byte[] buffer = new byte[len];
        buffer[0] = (char)Protocol.USERTYPE_CLIENT;
        for(int i=0; i<username.length(); i++){
            buffer[i+1] = (byte)username.charAt(i);
        }
        buffer[username.length() + 1] = (char)0;
        for(int i=0; i<password.length(); i++){
            buffer[i+2+username.length()] = (byte)password.charAt(i);
        }
        result = sendData(buffer);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        
        //reject or authenticated
        result = receiveHeader(head);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        if(head.type != (char)Protocol.DATATYPE_PHASE_CHANGE){
            lasterr = "Authentication denied by server.";
            return false;
        }
        
        //read phase change (and check for Phase 1):
        buffer = new byte[1];
        result = receiveData(buffer);
        if(result < 0){
            lasterr = DC;
            return false;
        }
        if(buffer[0] != (char)2){
            lasterr = "Incorrect phase switch: Protocol Mismatch.";
            return false;
        }
        
        return true;
    }
    
    
    //send header generic method
    private int sendHeader(char type, int size){
        byte[] buffer = new byte[6];
        buffer[0] = (char)Protocol.PROTOCOL_VERSION;
        buffer[1] = (byte)type;
        
        buffer[2] = (byte)(size);
        buffer[3] = (byte)(size >>> 8);
        buffer[4] = (byte)(size >>> 16);
        buffer[5] = (byte)(size >>> 24);
        
        return sendData(buffer);
    }
    
    //generic sendData method (for everything in byte form):
    private int sendData(byte[] buffer){
        try{
            serverOut.write(buffer, 0, buffer.length);
            serverOut.flush();
        }catch(Exception e){
            return -1;
        }
        return buffer.length;
    }
    
    //user to receive a header (inserted into header):
    private int receiveHeader(Header head){
        byte[] b = new byte[6];
        int result = receiveData(b);
        if(result < 0)
            return -1;
        
        head.version = (char)b[0];
        head.type = (char)b[1];
        
        int val = 0;
        for(int i=0; i<4; i++){
            val += (b[i+2] & 0xFF) << (8 *i);
        }
        head.size = val;
        
        return 0;
    }

    //receive TCP data from the socket in length of buffer
    private int receiveData(byte[] buffer){
        int received = 0;
        int i = 0;
        try{
            for(i=0; i<buffer.length; i+=received){
               received = serverIn.read(buffer, i, buffer.length - i);
               if(received < 0)
                   return -1;
            }
        }catch(Exception e){
            return -1;
        }
        return i;
    }

}

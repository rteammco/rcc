package rcc.network;
import rcc.controls.SelectionControls;
import rcc.ErrorUI;
import rcc.video.Screen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class NetworkThread extends Thread{
    
    private static final String DC = "Connection to server was lost.";
    private Screen screen;
    
    private Socket server;
    private DataInputStream serverIn;
    private DataOutputStream serverOut;
    
    public String lasterr;
    
    
    public NetworkThread(Screen screen){
        this.screen = screen;
        lasterr = "";
    }
    
    
    public boolean connectToServer(
            String username, String password,
            String serverIP, int serverPort){
        
        //create client variables:
        try{
            server = new Socket(serverIP, serverPort);
            serverIn = new DataInputStream(server.getInputStream());
            serverOut = new DataOutputStream(server.getOutputStream());
        }catch(Exception e){
            lasterr = "Connection error: " + e.getMessage();
            return false;
        }
        
        Header head = new Header();
         
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
        
        return true;
    }
    
    
    //disconnect socket:
    public void disconnect(){
        try{
            server.close();
        }catch(Exception e){
            //do nothing
        }
    }
    
    //listen thread:
    public void run(){
        /*
        screen.connected(server.getInetAddress().getHostAddress());
        
        try{
            while(connected){
                Header head = new Header();
                int result = receiveHeader(head);
                if(result < 0)
                    break;
                
                if(head.type == Protocol.DATATYPE_VIDEO){
                    byte[] buffer = new byte[head.size];
                    result = receiveData(buffer);
                    if(result < 0)
                        break;
                    
                    InputStream imageIn =
                        new ByteArrayInputStream(buffer, 0, head.size);
                    BufferedImage image = ImageIO.read(imageIn);
                    screen.updateFrame(image);
                }else if(head.type == Protocol.DATATYPE_ERROR){
                    //do something
                }else if(head.type == Protocol.DATATYPE_DISCONNECT){
                    break;
                }
            }
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_FAILURE);
        }
        
        try{
            serverIn.close();
            serverOut.close();
            server.close();
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_SETUP_FAILURE);
        }
        
        screen.disconnected();
         */
    }
    
    
    
    public int sendCommand(int cmd){
        char command = (char)cmd;
        int result = sendHeader((char)Protocol.DATATYPE_COMMAND,
                Protocol.COMMAND_LENGTH);
        if(result < 0)
            return -1;
        
        byte[] buffer = new byte[1];
        buffer[0] = (byte)command;
        return sendData(buffer);
    }
    
    private int sendHeader(char type, int size){
        byte[] buffer = new byte[6];
        buffer[0] = (char)Protocol.PROTOCOL_VERSION;
        buffer[1] = (byte)type;
        
        for(int i=0; i<4; i++){
            int offset = (buffer.length - 1 - i) * 8;
            buffer[i+2] = (byte)((size >>> offset) & 0xFF);
        }
        
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
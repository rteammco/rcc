package rcc.network;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import rcc.video.Screen;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.imageio.ImageIO;


public class UDPClient extends Thread {
    
    public static final int UDP_MAX_SIZE = 65507;
    
    public String lasterr;
    
    private boolean active;
    private boolean sourceKnown;
    
    private DatagramSocket socket;
    private byte[] buffer;
    private DatagramPacket packet;
    
    private Screen screen;
    
    
    //Create the class (CONSTRUCTOR):
    public UDPClient(Screen screen){
        this.screen = screen;
        lasterr = "Zomg it blew uP!";
        active = false;
        sourceKnown = false;
    }
    
    
    //setup the socket and packet:
    public boolean setup(){
        try{
            socket = new DatagramSocket();
            buffer = new byte[UDP_MAX_SIZE];
            packet = new DatagramPacket(buffer, buffer.length);
            active = true;
        }catch(Exception e){
            lasterr = "UDP Socket setup failed!";
            return false;
        }
        return true;
    }
    
    
    //run the receiving:
    @Override
    public void run(){
        while(active){
            try{
                //receive packet:
                socket.receive(packet);
                
                if(!sourceKnown){
                    screen.connected(
                            packet.getAddress().getHostAddress());
                    sourceKnown = true;
                }
                
                InputStream imageIn = new ByteArrayInputStream(
                        packet.getData(), 0, packet.getLength());
                
                BufferedImage image = ImageIO.read(imageIn);
                screen.updateFrame(image);
            
                //Reset packet length
                packet.setLength(buffer.length);
            }catch(Exception e){
                lasterr = "UDP Read failed.";
            }
        }
        screen.disconnected();
    }
    
    
    //Attempts to close the socket (else interrupt the thread)
    //  and stops the loop, thus ending the listening cycle.
    public void terminate(){
        try{
            socket.close();
        }catch(Exception e){
            lasterr = "Socket close error: " + e.getMessage();
            try{
                this.interrupt();
            }catch(Exception e2){}
        }
        active = false;
    }
    
    
    //Get this socket's local address and return it as a string.
    public String getLocalAddress(){
        if(!active)
            return "127.0.0.1";
        else
            try{
                return InetAddress.getLocalHost().getHostAddress();
            }catch(Exception e){
                return "0.0.0.0";
            }
    }
    
    //Get this socket's local port number and return it as a string.
    public int getLocalPort(){
        if(!active)
            return 0;
        else
            return socket.getLocalPort();
    }
}

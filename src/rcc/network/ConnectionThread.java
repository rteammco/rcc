package rcc.network;
import rcc.ErrorUI;
import rcc.video.Screen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.Socket;
import javax.imageio.ImageIO;


/*
 * A simple class that handles the runtime of a thread.
 * Will stop if disconnect() is called, and will otherwise run
 * until execution. Once finished, it will notify the
 * NetworkManager with which it was constructed that the
 * process was done.
 */
public class ConnectionThread extends Thread{
    
    private NetworkManager netManager;
    private Socket robot;
    private Screen screen;
    private boolean connected;
    
    public ConnectionThread(NetworkManager netManager,
            Socket robot, Screen screen){
        this.netManager = netManager;
        this.robot = robot;
        this.screen = screen;
        
        connected = true;
    }
    
    
    //run the thread, keep on listening!
    @Override
    public void run(){
        //setup:
        DataInputStream in = null;
        DataOutputStream out = null;
        try{
            in = new DataInputStream(robot.getInputStream());
            out = new DataOutputStream(robot.getOutputStream());
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_FAILURE);
            connected = false;
            return;
        }
        
        screen.connected(robot.getInetAddress().getHostAddress());
        
        try{
            while(connected){
        
                //read in size:
                int total = 0;
                int size = 4;
                byte[] buff = new byte[size];
                while(true){
                    int result = in.read(buff, total, size - total);
                    if(result < 0){
                        ErrorUI.display(new Exception("Read ERROR (size)."),
                                ErrorUI.NETWORK_FAILURE);
                        size = 0;
                        connected = false;
                        break;
                    }
                    total += result;
                    if(total == size){
                        size = convertByteToInt(buff);
                        break;
                    }
                }
                
                //read in the next image:
                total = 0;
                buff = new byte[size];
                while(true){
                    int result = in.read(buff, total, size - total);
                    if(result < 0){
                        ErrorUI.display(new Exception("Read ERROR (img)."),
                                ErrorUI.NETWORK_FAILURE);
                        connected = false;
                        break;
                    }
                    total += result;
                    if(result == size || result == 0){
                        break;
                    }
                }
                
                //set image to screen:
                if(size > 0 && size == total){
                    InputStream imageIn =
                            new ByteArrayInputStream(buff, 0, size);
                    BufferedImage image = ImageIO.read(imageIn);
                    screen.updateFrame(image);
                }
            }
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_FAILURE);
            connected = false;
        }
        
        try{
            in.close();
            out.close();
            robot.close();
        }catch(Exception e){
            ErrorUI.display(e, ErrorUI.NETWORK_SETUP_FAILURE);
        }
        screen.disconnected();
        netManager.disconnect();
    }
    
    
    //disconnect the thread (stop the loop):
    public void disconnect(){
        connected = false;
    }
    
    
    //convert a byte[] to an integer. If b is not 4, return -1.
    private int convertByteToInt(byte[] b){
        if(b.length != 4)
            return -1;
        int val = 0;
        val = val | (b[3] & 0xFF);
        val = val << 8;
        val = val | (b[2] & 0xFF);
        val = val << 8;
        val = val | (b[1] & 0xFF);
        val = val <<8;
        val = val | (b[0] & 0xFF);
        return val;
    }
}

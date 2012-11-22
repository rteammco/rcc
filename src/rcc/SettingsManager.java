package rcc;

import java.io.File;
import java.util.Scanner;

/*
 * This class loads, and then contains all values related
 * to running of the program. That is, it establishes the following
 * items, which are later referenced and shared between
 * different classes:
 * - Screen Aspect Ration
 * - Volume Level
 */
public class SettingsManager {
    
    public int aspectRatio;
    public int volume;
    
    public String serverIPAddress;
    public String serverTCPPort;
    public String serverUDPPort;
    
    public SettingsManager(){
        aspectRatio = 0;
        volume = 0;
    }
    
    
    //Reads configuration file and attempts to set up
    //  the properties.
    //Returns FALSE if file read failed,
    //  and applies default settings instead.
    public boolean readConfigFile(String fileURL){
        try{
            File config = new File(fileURL);
            Scanner sc = new Scanner(config);
            serverIPAddress = sc.nextLine();
            serverTCPPort = sc.nextLine();
            serverUDPPort = sc.nextLine();
            sc.close();
        }catch(Exception e){
            serverIPAddress = "127.0.0.1";
            serverTCPPort = "9999";
            serverUDPPort = "7777";
            return false;
        }
        return true;
    }
}

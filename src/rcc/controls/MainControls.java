package rcc.controls;
import rcc.MainPanel;
import rcc.graphics.Graphic;
import rcc.graphics.Button;
import rcc.graphics.MovementKeyButton;
import rcc.graphics.CameraPositioner;
import rcc.graphics.VideoControlSet;
import rcc.network.Protocol;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;


public class MainControls extends JPanel implements ControlPanel{
    
    private MainPanel mainPanel;
    
    private ArrayList<Graphic> guiComponents;
    private int numComponents;
    
    private Button shutDown;
    private Button reset;
    private Button disconnect;
    private Button exit;
    
    private VideoControlSet vcs;
    
    private MovementKeyButton wKey;
    private MovementKeyButton aKey;
    private MovementKeyButton sKey;
    private MovementKeyButton dKey;
    
    private CameraPositioner stick;
    
    //CONSTRUCTOR:
    //basic setup, and establish mouse/keyboard
    // listeners:
    public MainControls(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        
        mainPanel.screen.setToLoading();
        setBackground(Color.gray);
        
        this.guiComponents = new ArrayList<Graphic>();
        this.numComponents = 0;
        
        //mouse listener:
        addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e){}
            @Override
            public void mouseEntered(MouseEvent e){}
            @Override
            public void mouseExited(MouseEvent e){}
            @Override
            public void mousePressed(MouseEvent e){
                checkMousePressed(e);
            }
            @Override
            public void mouseReleased(MouseEvent e){
                checkMouseReleased(e);
            }
        });
        
        //mouse movement listener:
        addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e){
                checkMouseDragged(e);
            }
            @Override
            public void mouseMoved(MouseEvent e){
                checkMouseMoved(e);
            }
        });
        
        //setup UI:
        setup();
    }
    
    
    //mouse pressed check method:
    private void checkMousePressed(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        
        if(wKey.mouseClickCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_W, 0);
            repaint();
        }else if(aKey.mouseClickCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_A, 0);
            repaint();
        }else if(sKey.mouseClickCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_S, 0);
            repaint();
        }else if(dKey.mouseClickCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_D, 0);
            repaint();
        }else{
            boolean repaint = false;
            for(int i=0; i<numComponents; i++){
                repaint =( 
                    (guiComponents.get(i).mouseClickCheck(mousex, mousey))
                        || repaint);
            }
            if(repaint)
                repaint();
            }
    }
    
    
    //mouse released check method:
    private void checkMouseReleased(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        
        if(shutDown.mouseReleaseCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.sendShutdown();
        }else if(reset.mouseReleaseCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.reset();
        }else if(disconnect.mouseReleaseCheck(mousex, mousey)){
            mainPanel.rcc.netManager.conn.disconnect();
        }else if(exit.mouseReleaseCheck(mousex, mousey)){
            System.exit(1);
        }else if(wKey.mouseReleaseCheck(mousex, mousey))
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_W, 0);
            //System.err.println("W Pressed");
        else if(aKey.mouseReleaseCheck(mousex, mousey))
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_A, 0);
            //System.err.println("A Pressed");
        else if(sKey.mouseReleaseCheck(mousex, mousey))
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_S, 0);
            //System.err.println("S Pressed");
        else if(dKey.mouseReleaseCheck(mousex, mousey))
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_D, 0);
            //System.err.println("D Pressed");
        else if(stick.mouseReleaseCheck(mousex, mousey)){
            Point camAngle = stick.getLocation();
            mainPanel.rcc.netManager.conn.sendCommand(
                    Protocol.CAMERA_UP_DOWN, camAngle.y);
            mainPanel.rcc.netManager.conn.sendCommand(
                    Protocol.CAMERA_LEFT_RIGHT, camAngle.x);
            //System.err.println(camAngle.x + ", " + camAngle.y);
        }
        vcs.mouseReleaseCheck(mousex, mousey);
            
        repaint();
    }
    
    
    //mouse moved check method:
    private void checkMouseMoved(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        boolean repaint = false;
        for(int i=0; i<numComponents; i++){
            repaint =( 
                (guiComponents.get(i).mouseOverCheck(mousex, mousey))
                    || repaint);
        }
        repaint();
    }
    
    
    //mouse dragged check method:
    private void checkMouseDragged(MouseEvent e){
        //for the "joystick" and the volume bar:
        int x = e.getX();
        int y = e.getY();
        //try dragging joystick or volume bar,
        // if applicable handle actions and repaint.
        boolean repaint = stick.dragged(x, y)
                || vcs.checkMouseDragged(x, y);
        if(repaint)
            repaint();
    }
    
    
    //key pressed handler method:
    @Override
    public void handleKeyPress(KeyEvent e){
        char key = e.getKeyChar();
        if(key == 'w' || key == 'W'){
            if(wKey.mode != Button.DEPRESSED){
                wKey.depress();
                mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_W, 0);
                repaint();
            }
        }else if(key == 'a' || key == 'A'){
            if(aKey.mode != Button.DEPRESSED){
                aKey.depress();
                mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_A, 0);
                repaint();
            }
        }else if(key == 's' || key == 'S'){
            if(sKey.mode != Button.DEPRESSED){
                sKey.depress();
                mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_S, 0);
                repaint();
            }
        }else if(key == 'd' || key == 'D'){
            if(dKey.mode != Button.DEPRESSED){
                dKey.depress();
                mainPanel.rcc.netManager.conn.sendCommand(Protocol.PRESS_D, 0);
                repaint();
            }
        }
    }
    
    
    //key released handle method:
    @Override
    public void handleKeyRelease(KeyEvent e){
        char key = e.getKeyChar();
        if(key == 'w' || key == 'W'){
            wKey.undepress();
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_W, 0);
            repaint();
        }else if(key == 'a' || key == 'A'){
            aKey.undepress();
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_A, 0);
            repaint();
        }else if(key == 's' || key == 'S'){
            sKey.undepress();
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_S, 0);
            repaint();
        }else if(key == 'd' || key == 'D'){
            dKey.undepress();
            mainPanel.rcc.netManager.conn.sendCommand(Protocol.RELEASE_D, 0);
            repaint();
        }
    }
    
    
    //handle GUI setup over here (list of graphic items):
    private void setup(){
        //VIDEO CONTROLS:
        vcs = new VideoControlSet(385, 20, mainPanel);
        guiComponents.add(vcs);
        
        //ROBOT CONTROLS:
        //wasd keys: - 365 (390 - 25) is abs. center
        setupRobotControls(355, 80);
        
        //OPTIONS:
        shutDown = new Button("Shut Down", 10, 20, 100, 40);
        guiComponents.add(shutDown);
        reset = new Button("Reset", 10, 70, 100, 40);
        guiComponents.add(reset);
        disconnect = new Button("Disconnect", 10, 120, 100, 40);
        guiComponents.add(disconnect);
        exit = new Button("Exit", 10, 170, 100, 40);
        guiComponents.add(exit);
        
        numComponents = guiComponents.size();
    }
    
    
    //sets up the WASD keys and joystick around a given center:
    private void setupRobotControls(int x, int y){
        //WSAD
        wKey = new MovementKeyButton('W', x, y);
        guiComponents.add(wKey);
        aKey = new MovementKeyButton('A', x - 60, y + 60);
        guiComponents.add(aKey);
        sKey = new MovementKeyButton('S', x, y + 60);
        guiComponents.add(sKey);
        dKey = new MovementKeyButton('D', x + 60, y + 60);
        guiComponents.add(dKey);
        
        //joystick
        stick = new CameraPositioner(x + 230, y + 10);
        guiComponents.add(stick);
    }
    
    
    //handle drawing the GUI here:
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        for(int i=0; i<numComponents; i++){
            guiComponents.get(i).draw(g);
        }
        
    }
    
    
    //Tab cycle
    @Override
    public void nextComponent(){}
}

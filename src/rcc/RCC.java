package rcc;
import rcc.network.NetworkManager;
import rcc.video.Screen;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import javax.swing.JApplet;
import javax.swing.JFrame;


public class RCC extends JApplet {
    
    public SettingsManager settings;

    public NetworkManager netManager;
    public MainPanel mainPanel;
    private Screen screen;
    
    //constructor (for Applet Emulator):
    // DO NOT use this for the Applet, this is designed
    // to construct a frame to contain the listeners and
    // contents while running in non-applet mode.
    
    @SuppressWarnings("LeakingThisInConstructor")
    public RCC(int x, int y){
        JFrame frame = new JFrame();
        frame.setTitle("Robot Control Center");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        URL link = this.getClass().getClassLoader()
                .getResource("RCC.gif");
        if(link == null) System.exit(-1);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(link));
        frame.setResizable(false);
        
        //keyboard listener:
        this.setFocusable(false);
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){
                mainPanel.curPanel.handleKeyPress(e);
            }
            @Override
            public void keyReleased(KeyEvent e){
                mainPanel.curPanel.handleKeyRelease(e);
            }
            @Override
            public void keyTyped(KeyEvent e){}
        });
        
        
        frame.setSize(new Dimension(x, y));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int locX = (screenSize.width / 2) - (x / 2);
        int locY = (screenSize.height / 2) - (y / 2);
        frame.setLocation(locX, locY);
        frame.add(this);
        
        //add "init" components:
        settings = new SettingsManager();
        boolean configured = settings.readConfigFile("config.rcc");
        screen = new Screen(settings);
        netManager = new NetworkManager(screen, this);
        mainPanel = new MainPanel(this, screen, 570);
        if(configured)
            mainPanel.setToInitControls();
        else
            mainPanel.setToInitControls("Warning: CONFIG file not found.");
        
        //mainPanel.setToMainControls();
        
        add(mainPanel);
        
        frame.setVisible(true);
    }
    
    
    public RCC(){
        super();
    }
    
    //initialize everything (JApplet's "constructor")
    // this method is used only be the JApplet, and is NOT
    // to be used by the default Window constructor
    // which has its own set of Keyboard listeners.
    @Override
    public void init(){
        settings = new SettingsManager();
        screen = new Screen(settings);
        netManager = new NetworkManager(screen, this);
        mainPanel = new MainPanel(this, screen, 570);
        
        mainPanel.setToInitControls("Warning: CONFIG file not found.");
        
        add(mainPanel);
        
        //add keyboard listener:
        setFocusable(true);
        addKeyListener(new KeyListener(){
            @Override
            public void keyPressed(KeyEvent e){
                mainPanel.curPanel.handleKeyPress(e);
            }
            @Override
            public void keyReleased(KeyEvent e){
                mainPanel.curPanel.handleKeyRelease(e);
            }
            @Override
            public void keyTyped(KeyEvent e){}
        });
    }
    
    
    //main method: use for testing purposes (emulates with JFrame):
    public static void main(String[] args) {
        //temporary assumed size variables:
        int x = 780;
        int y = 680;
        RCC rcc = new RCC(x, y);
    }
}

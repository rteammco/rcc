package rcc.controls;
import rcc.MainPanel;
import rcc.graphics.Animated;
import rcc.graphics.Button;
import rcc.graphics.Graphic;
import rcc.graphics.PasswordField;
import rcc.graphics.TextField;
import rcc.graphics.Label;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;


public class InitControls extends JPanel implements ControlPanel{
    
    private MainPanel mainPanel;
    
    private ArrayList<Graphic> guiComponents;
    private ArrayList<Animated> animatedComponents;
    private int numComponents;
    
    private Button config;
    private Button exit;
    
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField selectedField;
    private Button connect;
    private Label errorLabel;
    
    
    //CONSTRUCTOR (with ERROR notice) - see below for full contructor:
    public InitControls(MainPanel mainPanel, String error){
        this(mainPanel);
        errorLabel.setText(error);
    }
    
    
    //CONSTRUCTOR:
    //basic setup, and establish mouse/keyboard
    // listeners:
    public InitControls(MainPanel mainPanel){
        this.mainPanel = mainPanel;
        
        setBackground(Color.gray);
        
        this.guiComponents = new ArrayList<Graphic>();
        this.animatedComponents = new ArrayList<Animated>();
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
        
        //animation timer:
        Timer timer = new Timer(30, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i=0; i<animatedComponents.size(); i++)
                    animatedComponents.get(i).updateFrame();
                repaint();
            }
        });
        timer.start();
    }    
    
    //mouse pressed check method:
    private void checkMousePressed(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        
        boolean repaint = false;
        for(int i=0; i<numComponents; i++){
            repaint =( 
                (guiComponents.get(i).mouseClickCheck(mousex, mousey))
                    || repaint);
        }
        if(repaint)
            repaint();
    }
    
    
    //mouse released check method:
    private void checkMouseReleased(MouseEvent e){
        int mousex = e.getX();
        int mousey = e.getY();
        
        if(connect.mouseReleaseCheck(mousex, mousey)){
            if(mainPanel.rcc.netManager.connect(
                    usernameField.getText(), passwordField.getText())){
                mainPanel.setToWaitControls(0);                
            }else{
                errorLabel.setText(mainPanel.rcc.netManager.lasterr);
            }
        }
        else if(config.mouseReleaseCheck(mousex, mousey))
            mainPanel.setToConfigControls();
        else if(exit.mouseReleaseCheck(mousex, mousey))
            System.exit(1);
            
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
        int x = e.getX();
        int y = e.getY();
        if(usernameField.mouseDragCheck(x, y) ||
                passwordField.mouseDragCheck(x, y)){
            repaint();
        }
    }
    
    
    //key pressed handler method:
    @Override
    public void handleKeyPress(KeyEvent e){
        char key = e.getKeyChar();
        
        if(key == KeyEvent.VK_TAB){
            nextComponent();
            return;
        }
        
        if(usernameField.selected)
            selectedField = usernameField;
        else if(passwordField.selected)
            selectedField = passwordField;
        else
            return;
        
        if(e.getKeyCode() == KeyEvent.VK_LEFT){ //left arrow key
            selectedField.moveCursor(0);
        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT){ //right arrow
            selectedField.moveCursor(1);
        }else if(key == 8){ //BACKSPACE key
            selectedField.backspace(selectedField.CURSOR_INDEX - 1);
        }else if(key == 127){ //DEL key
            selectedField.delete(selectedField.CURSOR_INDEX);
        }else if(key == KeyEvent.VK_ENTER){ //ENTER key
            if(mainPanel.rcc.netManager.connect(
                    usernameField.getText(), passwordField.getText())){
                mainPanel.setToWaitControls(0);                
            }else{
                errorLabel.setText(mainPanel.rcc.netManager.lasterr);
            }
        }else if(key >= 32 && key <= 126) { //accepted text character
            selectedField.insert(selectedField.CURSOR_INDEX, key);
        }
        repaint();
    }
    
    
    //key released handle method:
    @Override
    public void handleKeyRelease(KeyEvent e){
        //pass - no implementation for this method yet.
    }
    
    //Tab cycle (switch to next component)
    @Override
    public void nextComponent(){
        if(usernameField.selected){
            passwordField.setSelected(true);
            usernameField.setSelected(false);
        }else{
            usernameField.setSelected(true);
            passwordField.setSelected(false);
        }
        repaint();
    }
    
    
    //handle GUI setup over here (list of graphic items):
    private void setup(){        
        //CONNECT TEXT FIELDS:
        setupTextFields(385, 30);
        
        //OPTIONS:
        config = new Button("Config", 10, 120, 100, 40);
        guiComponents.add(config);
        exit = new Button("Exit", 10, 170, 100, 40);
        guiComponents.add(exit);
        
        numComponents = guiComponents.size();
    }
    
    //sets up the text fields around a given center:
    private void setupTextFields(int x, int y){
        //label:
        Label title = new Label(x, y,
                "Enter Login Information:",
                Label.largeFont, Color.white);
        guiComponents.add(title);
        
        //IP Text Field:
        usernameField = new TextField("Username:", x - 55, y + 35,
                "Your login screenname.");
        guiComponents.add(usernameField);
        animatedComponents.add(usernameField);
        
        //PORT Number Field:
        passwordField = new PasswordField("Password:", x - 55, y + 65,
                "Your password.");
        guiComponents.add(passwordField);
        animatedComponents.add(passwordField);
        
        //Connect Button:
        connect = new Button("Connect", x - 40, y + 100, 90, 30);
        guiComponents.add(connect);
        
        //Error Label (when errors are to be shown:
        errorLabel = new Label(x + 5, y + 150,
                "",
                Label.defaultFont,
                Color.red);
        guiComponents.add(errorLabel);
    }
    
    
    //handle drawing the GUI here:
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        for(int i=0; i<numComponents; i++){
            guiComponents.get(i).draw(g);
        }
        
    }
}

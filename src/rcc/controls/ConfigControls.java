package rcc.controls;
import rcc.MainPanel;
import rcc.graphics.Animated;
import rcc.graphics.Button;
import rcc.graphics.Graphic;
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


public class ConfigControls extends JPanel implements ControlPanel{
    
    private MainPanel mainPanel;
    
    private ArrayList<Graphic> guiComponents;
    private ArrayList<Animated> animatedComponents;
    private int numComponents;
    
    private Button apply;
    private Button okay;
    
    private TextField ipField;
    private TextField portField;
    private TextField localAddrField;
    private TextField selectedField;
    private Label applyLabel;
    
    
    //CONSTRUCTOR (with ERROR notice) - see below for full contructor:
    public ConfigControls(MainPanel mainPanel, String error){
        this(mainPanel);
    }
    
    
    //CONSTRUCTOR:
    //basic setup, and establish mouse/keyboard
    // listeners:
    public ConfigControls(MainPanel mainPanel){
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
        
        if(apply.mouseReleaseCheck(mousex, mousey)){
            apply();
        }
        else if(okay.mouseReleaseCheck(mousex, mousey)){
           apply();
           mainPanel.setToInitControls();
        }
            
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
        if(ipField.mouseDragCheck(x, y) ||
                portField.mouseDragCheck(x, y) ||
                localAddrField.mouseDragCheck(x, y)){
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
        
        if(ipField.selected)
            selectedField = ipField;
        else if(portField.selected)
            selectedField = portField;
        else if(localAddrField.selected)
            selectedField = localAddrField;
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
            apply();
            mainPanel.setToInitControls();
        }else if(key >= 32 && key <= 126) {
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
        if(ipField.selected){
            portField.setSelected(true);
            localAddrField.setSelected(false);
            ipField.setSelected(false);
        }else if(portField.selected){
            localAddrField.setSelected(true);
            portField.setSelected(false);
            ipField.setSelected(false);
        }else{
            ipField.setSelected(true);
            portField.setSelected(false);
            localAddrField.setSelected(false);
        }
        repaint();
    }
    
    
    //applies the state of current text fields to the main control system.
    private void apply(){
        String applyText = "";
        if(ipField.getText().length() > 0){
            mainPanel.rcc.netManager.ipAddress = ipField.getText();
            applyText += " Server IP: " + ipField.getText();
        }
        if(portField.getText().length() > 0){
            mainPanel.rcc.netManager.port = portField.getText();
            applyText += " Server PORT: " + portField.getText();
        }
        if(localAddrField.getText().length() > 0){
            mainPanel.rcc.netManager.localAddr = localAddrField.getText();
            applyText += " Local IP: " + localAddrField.getText();
        }
        
        if(applyText.length() > 0){
            applyLabel.setText("SET" + applyText);
            repaint();
        }
    }
    
    
    //handle GUI setup over here (list of graphic items):
    private void setup(){
        //CONNECT TEXT FIELDS:
        setupTextFields(385, 30);
        
        //OPTIONS:
        //Apply Button:
        apply = new Button("Apply", 10, 120, 100, 40);
        guiComponents.add(apply);
        
        //Okay Button:
        okay = new Button("Okay", 10, 170, 100, 40);
        guiComponents.add(okay);
        
        numComponents = guiComponents.size();
    }
    
    
    //sets up the text fields around a given center:
    private void setupTextFields(int x, int y){
        //Title LABEL:
        Label title = new Label(x, y,
                "Configuration Settings:",
                Label.largeFont, Color.white);
        guiComponents.add(title);
        
        //IP Text Field:
        ipField = new TextField("Server Address:", x - 55, y + 35,
                "Server's IP Address");
        ipField.setText(mainPanel.rcc.netManager.ipAddress);
        guiComponents.add(ipField);
        animatedComponents.add(ipField);
        
        //PORT Number Field:
        portField = new TextField("Server Port:", x - 55, y + 65,
                "Port Number (i.e. 7777)");
        portField.setText(mainPanel.rcc.netManager.port);
        guiComponents.add(portField);
        animatedComponents.add(portField);
        
        //LOCAL ADDRESS Field:
        localAddrField = new TextField("Local Address:", x - 55, y + 95,
                "Your computer's IP");
        localAddrField.setText(mainPanel.rcc.netManager.localAddr);
        guiComponents.add(localAddrField);
        animatedComponents.add(localAddrField);
        
        //Apply label!
        applyLabel = new Label(x + 5, y + 150,
                "",
                Label.defaultFont,
                Color.white);
        guiComponents.add(applyLabel);
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
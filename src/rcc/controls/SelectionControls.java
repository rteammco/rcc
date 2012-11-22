package rcc.controls;
import rcc.graphics.Button;
import rcc.graphics.Label;
import rcc.graphics.SelectionList;
import rcc.network.Connector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;


public class SelectionControls extends JPanel implements ControlPanel {
    
    private Connector conn;
    
    //private SelectionList sel;
    private ArrayList robotList;
    private Label mainLabel;
    private SelectionList selList;
    private Button cancel;
    
    public SelectionControls(Connector conn, ArrayList<String> list){
        setBackground(Color.gray);
        
        this.conn = conn;
        
        this.robotList = list;
        cancel = new Button("Cancel", 10, 170, 100, 40);
        mainLabel = new Label(385, 30,
                "Click on an available robot below to choose:",
                Label.largeFont, Color.BLACK);
        selList = new SelectionList(385, 60, list, 25);
        
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
            public void mouseDragged(MouseEvent e){}
            @Override
            public void mouseMoved(MouseEvent e){
                checkMouseMoved(e);
            }
        });
    }
    
    
    public void updateList(ArrayList<String> newList){
        this.robotList = newList;
        selList.changeList(newList);
        repaint();
    }
    
    private void checkMousePressed(MouseEvent e){
        if(cancel.mouseClickCheck(e.getX(), e.getY()))
            repaint();
        else if(selList.mouseClickCheck(e.getX(), e.getY()))
            repaint();
    }
    
    private void checkMouseReleased(MouseEvent e){
        if(cancel.mouseReleaseCheck(e.getX(), e.getY())){
            conn.sendDisconnect();
        }
        if(selList.mouseReleaseCheck(e.getX(), e.getY())){
            conn.sendSelection(
                    robotList.get(selList.lastSelectionIndex).toString());
            
        }
        repaint();
    }
    
    private void checkMouseMoved(MouseEvent e){
        cancel.mouseOverCheck(e.getX(), e.getY());
        selList.mouseOverCheck(e.getX(), e.getY());
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics gs){
        super.paintComponent(gs);
        
        cancel.draw(gs);
        mainLabel.draw(gs);
        selList.draw(gs);
    }
    
    
    //key buttons: DO NOTHING
    @Override
    public void handleKeyPress(KeyEvent e){}
    @Override
    public void handleKeyRelease(KeyEvent e){}
    //Tab cycle
    @Override
    public void nextComponent(){}
    
}

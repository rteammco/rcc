package rcc.controls;
import rcc.graphics.Label;
import rcc.MainPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;


public class WaitControls extends JPanel implements ControlPanel {
    
    private int pos;
    private Color[] wheel;
    //private Button cancel;
    private Label label;
    
    public WaitControls(final MainPanel mp, int status){
        setBackground(Color.gray);
        
        label = new Label(385, 30, "",
                Label.largeFont, Color.black);
        if(status == 0)
            label.setText("Connecting to server... Please wait.");
        else
            label.setText("Talking to server... Please wait.");
        
        //loading wheel
        pos = 0;
        wheel = new Color[8];
        int r = 205;
        int g = 200;
        int b = 230;
        for(int i=0; i<wheel.length; i++){
            wheel[i] = new Color(r, g, b);
            r-=15;
            g-=10;
            b-=10;
        }
        
        /*
        cancel = new Button("Cancel", 345, 155, 90, 30);
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
                if(cancel.mouseClickCheck(e.getX(), e.getY())){
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                if(cancel.mouseReleaseCheck(e.getX(), e.getY())){
                    mp.rcc.netManager.conn.sendDisconnect();
                }
            }
        });
        //mouse movement listener:
        addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e){}
            @Override
            public void mouseMoved(MouseEvent e){
                if(cancel.mouseOverCheck(e.getX(), e.getY())){
                    repaint();
                }
            }
        });
         */
        
        //animation timer:
        Timer timer = new Timer(60, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                pos++;
                if(pos >= wheel.length)
                    pos = 0;
                repaint();
                //mp.setDividerLocation(mp.height / 4 * 3);
            }
        });
        timer.start();
    }
    
    @Override
    public void paintComponent(Graphics gs){
        super.paintComponent(gs);
        
        Graphics2D g = (Graphics2D)gs;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Ariel", Font.BOLD, 18));
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;
        
        label.draw(g);
        //cancel.draw(g);
        
        //spinning wheel of time
        g.setStroke(new BasicStroke(4));        
        for(int i=0; i<wheel.length; i++){
            int index = i + pos;
            if(index >= wheel.length)
                index -= wheel.length;
            g.setColor(wheel[index]);
            g.drawArc(centerX-9, centerY-13, 25, 25, i*45, 45);
        }
        
    }
    
    //key buttons:
    @Override
    public void handleKeyPress(KeyEvent e){}
    @Override
    public void handleKeyRelease(KeyEvent e){}
    //Tab cycle
    @Override
    public void nextComponent(){}
}

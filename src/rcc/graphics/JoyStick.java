package rcc.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class JoyStick extends Button{
    
    private static final Color backhc = new Color(50, 50, 50);
    
    private int arc;
    private int stickx;
    private int sticky;
    
    public JoyStick(int x, int y){
        super("?", x, y, 70, 70);
        
        arc = 15;
        stickx = xpos + 10;
        sticky = ypos + 10;
    }
    
    @Override
    public void draw(Graphics g){
        //draw static base:
        g.setColor(backrc);
        g.fillRoundRect(xpos, ypos, width, height, arc, arc);
        
        //draw outline:
        if(mode == HIGHLIGHTED || mode == DEPRESSED){
            g.setColor(Color.yellow);
            g.drawRoundRect(xpos, ypos, width, height, arc, arc);
        }
        
        if(mode != DEPRESSED)
            g.setColor(backhc);
        else
            g.setColor(backdc);
        g.fillOval(stickx, sticky, width - 20, height - 20);
    }
    
    
    //when dragged, this will move the stick along the mouse:
    public boolean dragged(int x, int y){
        if(mode != DEPRESSED) //must be clicked on
            return false;
        
        if(x > xpos + 10 && x < xpos + 60)
            stickx = x - 25;
        if(y > ypos + 10 && y < ypos + 60)
        sticky = y - 25;
        return true;
    }
    
    //if mouse released, this will automatically set itself
    // to a HIGHLIGHTED mode (if intersecting).
    //Also, this will initiate a position fallback (to 0).
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        boolean release = false;
        if(this.mode == DEPRESSED){
            //make stick return to default position:
            //TODO - possible, animate this int he future
            stickx = xpos + 10;
            sticky = ypos + 10;
            release = true;
        }
        if(intersects(x, y)){
            this.mode = HIGHLIGHTED;
            return true;
        }else{
            this.mode = REGULAR;
            return false || release;
        }
    }
}

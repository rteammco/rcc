package rcc.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class CameraPositioner extends Button{
    
    private static final Color backhc = new Color(50, 50, 50);
    
    private int arc;
    private int curX;
    private int curY;
    private Point location;
    
    public CameraPositioner(int x, int y){
        super("?", x, y, 80, 80);
        
        arc = 15;
        curX = x + 30;
        curY = y + 30;
        location = new Point(30, 30);
    }
    
    @Override
    public void draw(Graphics g){
        //draw static base:
        g.setColor(backrc);
        g.fillRoundRect(xpos, ypos, width, height, arc, arc);
        
        //draw coordinate grid:
        g.setColor(Color.green);
        for(int i=1; i<=3; i++){
            g.drawLine(xpos, ypos + (i * (height / 4)),
                    (xpos + width), ypos + (i * (height / 4)));
            g.drawLine(xpos + (i * (width / 4)), ypos,
                    xpos + (i * (width / 4)), (ypos + height));
        }
        
        //draw label and coordinate text:
        g.setColor(Color.black);
        g.setFont(Label.defaultFont);
        Label.drawCenteredLabel(g, "Camera Position",
                xpos + (width / 2), ypos - 15);
        Label.drawCenteredLabel(g, "(" + (location.x * 3) + ", " +
                (location.y * 3) + ")",
                xpos + (width / 2), ypos + height + 15);
        
        //draw outline:
        if(mode == HIGHLIGHTED || mode == DEPRESSED){
            g.setColor(Color.yellow);
        }else{
            g.setColor(Color.black);
        }
        g.drawRoundRect(xpos, ypos, width, height, arc, arc);
        
        if(mode != DEPRESSED)
            g.setColor(Color.white);
        else
            g.setColor(Color.yellow);
        g.fillOval(curX, curY, width - 60, height - 60);
    }
    
    
    //when dragged, this will move the stick along the mouse:
    public boolean dragged(int x, int y){
        if(mode != DEPRESSED) //must be clicked on
            return false;
        
        if(x >= xpos + 10 && x <= xpos + 70)
            curX = x - 10;
        if(y >= ypos + 10 && y <= ypos + 70)
            curY = y - 10;
        
        location.x = (curX - xpos);
        location.y = (curY - ypos);
        
        return true;
    }
    
    //if mouse released, this will automatically set itself
    // to a HIGHLIGHTED mode (if intersecting).
    //Also, this will initiate a position fallback (to 0).
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        boolean release = false;
        if(this.mode == DEPRESSED){
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
    
    
    //Returns the Point (x, y) location of the cursor.
    //  x and y range from 0 to 180.
    public Point getLocation(){
        return location;
    }
}
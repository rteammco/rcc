package rcc.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;


public class InteractiveLabel extends Label {
    
    
    //if drawn once, dimensions will have been established.
    public static final int REGULAR = 0;
    public static final int HIGHLIGHTED = 1;
    public static final int DEPRESSED = 2;
    
    private int mode;
    
    private boolean dimensionsFound;
    private Rectangle rect;
    
    private Color highlightedColor;
    private Color depressedColor;
    
    public InteractiveLabel(int x, int y, String text, Font font){
        super(x, y, text, font, Color.white);
        
        mode = REGULAR;
        
        highlightedColor = Color.cyan;
        depressedColor = Color.black;
        
        dimensionsFound = false;
        rect = new Rectangle();
    }
    
    
    @Override
    //Draw to the given Graphics object (draws itself!)
    public void draw(Graphics g){
        if(contents.length() == 0)
            return;
        if(!dimensionsFound){
            rect.width = getTextWidth(g, contents);
            rect.height = getTextHeight(g, contents);
            rect.x = xpos - (rect.width / 2);
            rect.y = ypos - (rect.height / 2);
            dimensionsFound = true;
        }
        g.setFont(font);
        
        //set color based on mode:
        if(mode == HIGHLIGHTED)
            g.setColor(highlightedColor);
        else if(mode == DEPRESSED)
            g.setColor(depressedColor);
        else
            g.setColor(color);
        
        drawCenteredLabel(g, contents, xpos, ypos);        
    }
    
    
    //if moused over, this will automatically set
    // itself to HIGHLIGHTED mode.
    //RETURNS true if repaint is necessary (changed modes)
    @Override
    public boolean mouseOverCheck(int x, int y){
        if(intersects(x, y)){
            this.mode = HIGHLIGHTED;
            return true;
        }else{
            this.mode = REGULAR;
            return false;
        }
    }
    
    
    //if mouse clicked, this will automatically set itself
    // to a DEPRESSED mode.
    //RETURNS true if repaint is necessary (changed modes)
    @Override
    public boolean mouseClickCheck(int x, int y){
        if(intersects(x, y)){
            this.mode = DEPRESSED;
            return true;
        }else
            return false;
    }
    
    
    //if mouse released, this will automatically set itself
    // to a HIGHLIGHTED mode.
    //RETURNS true if repaint and action by button is necessary.
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        if(intersects(x, y)){
            boolean retVal = false;
            if(this.mode == DEPRESSED)
                retVal = true;
            this.mode = HIGHLIGHTED;
            return retVal;
        }else{
            this.mode = REGULAR;
            return false;
        }
    }
    
    
    //Checks if this object is intersecting the given
    // x and y values.
    //This method is used by mouseClickCheck and mouseOverCheck
    // to perform necessary intersection checks.
    private boolean intersects(int x, int y){
        if(!dimensionsFound)
            return false;
        else{
            if(x > rect.x && x < (rect.x + rect.width) &&
                    y > rect.y && y < (rect.y + rect.height)){
                return true;
            }
            else
                return false;
        }
    }
}

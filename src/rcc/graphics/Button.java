package rcc.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class Button implements Graphic{
    
    //modes:
    public static final int REGULAR = 0;
    public static final int HIGHLIGHTED = 1;
    public static final int DEPRESSED = 2;
    
    //standard button font format:
    protected static final Font defaultFont =
            new Font("Ariel", Font.BOLD, 14);
    
    //colors:
    protected static final Color backrc = new Color(20, 20, 20);
    protected static final Color backhc = new Color(30, 30, 30);
    protected static final Color backdc = new Color(193, 205, 193);
    protected static final Color textrc = Color.white;
    protected static final Color texthc = new Color(127, 255, 212);
    
    public int mode;
    
    protected String text;
    protected int xpos;
    protected int ypos;
    protected int width;
    protected int height;
    protected int arch;
    protected int arcw;
    
    public Button(String text, int x, int y, int width, int height){
        this.mode = REGULAR;
        this.text = text;
        this.xpos = x;
        this.ypos = y;
        this.width = width;
        this.height = height;
        this.arcw = width / 6;
        this.arch = height / 3;
    }
    
    
    //draw the button, based on the mode it is currently in:
    @Override
    public void draw(Graphics g){
        //set up background color
        switch(mode){
            case REGULAR:
                g.setColor(backrc);
                break;
            case HIGHLIGHTED:
                g.setColor(backhc);
                break;
            case DEPRESSED:
                g.setColor(backdc);
                break;
            default:
                return;
        }
        //fill in background
        g.fillRoundRect(xpos, ypos, width, height, arcw, arch);
        
        //fill in the general outline:
        if(mode != REGULAR){
            g.setColor(backrc);
            g.drawRoundRect(xpos, ypos, width, height, arcw, arch);
        }
        
        //set up text color
        switch(mode){
            case REGULAR:
                g.setColor(textrc);
                break;
            case HIGHLIGHTED:
                g.setColor(texthc);
                break;
            case DEPRESSED:
                g.setColor(texthc);
                break;
            default:
                return;
        }
        //set up fonts and draw the text:
        g.setFont(defaultFont);
        Label.drawCenteredLabel(g, text,
                xpos + (width / 2), ypos + (height / 2));
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
    protected boolean intersects(int x, int y){
        if(x < this.xpos)
            return false;
        else if(y < this.ypos)
            return false;
        else if((x < (this.xpos + this.width)) &&
                (y < (this.ypos + this.height)))
            return true;
        else
            return false;
    }
}

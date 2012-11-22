package rcc.graphics;

import java.awt.Font;
import java.awt.Graphics;


public class MovementKeyButton extends Button{
    
    
    private boolean override; //locks mode in place if true:
    
    private String key;
    private int arc;

    public MovementKeyButton(char key, int x, int y){
        super("?", x, y, 50, 50);
        
        this.override = false;
        this.key = "" + key;
        this.arc = 10;
    }
    
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
        g.fillRoundRect(xpos, ypos, width, height, arc, arc);
        
        //fill in the general outline:
        if(mode != REGULAR){
            g.setColor(backrc);
            g.drawRoundRect(xpos, ypos, width, height, arc, arc);
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
        g.setFont(new Font("Ariel", Font.BOLD, 30));
        Label.drawCenteredLabel(g, key,
                xpos + (width / 2), ypos + (height / 2));
    }

    
    //override for mouseover check (if override is true)
    @Override
    public boolean mouseOverCheck(int x, int y){
        if(!override)
            return super.mouseOverCheck(x, y);
        else
            return false;
    }
    
    //override for mouseclick check
    @Override
    public boolean mouseClickCheck(int x, int y){
        if(!override)
            return super.mouseClickCheck(x, y);
        else
            return false;
    }
    
    //override for mouserelease check
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        if(!override)
            return super.mouseReleaseCheck(x, y);
        else
            return false;
    }
    
    
    //overrides the mode and forces depression:
    public void depress(){
        mode = DEPRESSED;
        override = true;
    }
    
    //releases override and resets the button to default
    public void undepress(){
        mode = REGULAR;
        override = false;
    }
}

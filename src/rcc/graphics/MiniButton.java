package rcc.graphics;
import rcc.video.Screen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;


public class MiniButton extends Button{
    
    private boolean downpressed;
    
    //un-downpresses all links if this becomes downpresse!
    private ArrayList<MiniButton> links;
    
    public MiniButton(String text, int x, int y){
        super(text, x, y, 26, 12);
        
        downpressed = false;
        links = new ArrayList<MiniButton>();
    }
    
    @Override
    public void draw(Graphics g){
        //draw outline:
        if(!downpressed){
            if(mode == HIGHLIGHTED)
                g.setColor(Color.yellow);
            else
               g.setColor(textrc);
        }else
            g.setColor(backhc);
        g.drawRoundRect(xpos, ypos, width, height, arcw, arch);
        
        //draw background (where applicable):
        if(downpressed){
            g.setColor(backhc);
            g.fillRoundRect(xpos, ypos, width, height, arcw, arch);
        }
        
        //set up text color
        if(mode == HIGHLIGHTED)
            g.setColor(texthc);
        else
            g.setColor(textrc);
        //set up fonts and draw the text:
        g.setFont(new Font("Ariel", Font.PLAIN, 10));
        Label.drawCenteredLabel(g, text,
                xpos + (width / 2), ypos + (height / 2));
    }
    
    
    //if mouse clicked, this will automatically set itself
    // to a downpressed mode. Override from "DEPRESSED" mode,
    // as this particular button type has no DEPRESSED mode.
    //RETURNS true if repaint is necessary (changed modes)
    @Override
    public boolean mouseClickCheck(int x, int y){
        if(intersects(x, y)){
            downpressed = true;
            int num = links.size();
            for(int i=0; i<num; i++)
                links.get(i).unpress();
            return true;
        }else
            return false;
    }
    
    
    //set up the links of all other buttons,
    // passed in at arbitrary numbers:
    public void setLinks(MiniButton... buttons){
        for(MiniButton b : buttons)
            links.add(b);
    }
    
    
    //circles through all linked buttons, and un-downpresses
    // them (to replace itself as being pressed).
    public void unpress(){
        downpressed = false;
    }
    
    //forces this button to be pressed:
    public void press(){
        downpressed = true;
    }
}
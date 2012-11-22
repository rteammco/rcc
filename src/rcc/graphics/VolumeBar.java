package rcc.graphics;

import java.awt.Color;
import java.awt.Graphics;


public class VolumeBar extends Button{
    
    public int curVol;
    private int sliderPos;
    private int lastNonmuteVol;
    
    private int[] volXpoints;
    private int[] volYpoints;
    
    private int iconMode;
    
    public VolumeBar(int volume, int x, int y){
        super("?", x, y - 8, 70, 10);
        
        sliderPos = volume;
        curVol = volume;
        lastNonmuteVol = volume;
        
        volXpoints = new int[] {xpos - 25, xpos - 15, xpos - 15};
        volYpoints = new int[] {ypos + 5, ypos, ypos + 10};
        
        iconMode = REGULAR;
    }
    
    
    //draw method override (all complexities included):
    @Override
    public void draw(Graphics g){
        switch(iconMode){
            case REGULAR:
                g.setColor(textrc);
                break;
            case HIGHLIGHTED:
                g.setColor(Color.cyan);
                break;
            case DEPRESSED:
                g.setColor(Color.yellow);
                break;
            default:
                g.setColor(textrc);
                break;
        }
        
        //icon
        g.fillPolygon(volXpoints, volYpoints, 3);
        g.fillRect(xpos - 24, ypos + 3, 4, 5);
        
        //icon level bars:
        int bars = (int)(.99 + (.15 * sliderPos));
        for(int i=0; i<bars; i++){
            g.drawArc(xpos - 23 - (i*6), ypos - (i*5),
                    10 * (i+1), 10 * (i+1), 30, -60);
        }
        
        if(iconMode != REGULAR)
            g.setColor(textrc);
        
        //bar
        g.fillRect(xpos, ypos - 3, 3, 15);
        g.fillRect(xpos + 60, ypos - 3, 3, 15);
        g.fillRect(xpos, ypos + 4, 60, 2);
        
        //if muted: draw the red mute circle/slash:
        g.setColor(Color.red);
        if(sliderPos == 0){
            g.drawOval(xpos - 27, ypos - 2, 15, 15);
            g.drawLine(xpos - 24, ypos + 11, xpos - 16, ypos);
        }
        
        //slider w/ position
        if(mode == HIGHLIGHTED)
            g.setColor(Color.yellow);
        else if(mode == DEPRESSED)
            g.setColor(Color.green);
        g.fillOval(xpos + (int)(2.7 * sliderPos), ypos, 8, 8);
    }


    //when dragged, this will move the bar along the mouse:
    public int dragged(int x, int y){
        if(mode != DEPRESSED) //must be clicked on
            return -1;
        
        if(x >= xpos && x < xpos + 55){
            sliderPos = (int)((x - xpos) / 2.6);
        }
        return sliderPos;
    }
    
    
    //checks for mouseover (override - needs to check the
    // slider and the volume icon seperately):
    @Override
    public boolean mouseOverCheck(int x, int y){
        if(intersects(x, y)){
            this.mode = HIGHLIGHTED;
            return true;
        }else if(intersectsIcon(x, y)){
            this.iconMode = HIGHLIGHTED;
            return true;
        }else{
            this.iconMode = REGULAR;
            this.mode = REGULAR;
            return false;
        }
    }
    
    
    //checks for clicks (override - needs to check the
    // slider and the volume icon seperately):
    @Override
    public boolean mouseClickCheck(int x, int y){
        if(intersects(x, y)){
            this.mode = DEPRESSED;
            return true;
        }else if(intersectsIcon(x, y)){
            this.iconMode = DEPRESSED;
            return true;
        }else{
            return false;
        }
    }
    
    
    //checks for click releases (override - needs to check the
    // slider and the volume icon seperately):
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        if(intersects(x, y)){
            boolean retVal = false;
            if(this.mode == DEPRESSED)
                retVal = true;
            this.mode = HIGHLIGHTED;
            curVol = sliderPos;
            if(curVol > 0)
                lastNonmuteVol = curVol;
            return retVal;
        }else if(intersectsIcon(x, y)){
            boolean retVal = false;
            if(this.iconMode == DEPRESSED){
                retVal = true;
                if(sliderPos == 0){
                    curVol = lastNonmuteVol;
                }else{
                    lastNonmuteVol = curVol;
                    curVol = 0;
                }
                sliderPos = curVol;
            }
            this.iconMode = HIGHLIGHTED;
            return retVal;
        }else{
            this.mode = REGULAR;
            this.iconMode = REGULAR;
            return false;
        }
    }
    
    
    //checks intersection of the icon (to the left of
    // the actual volume bar slider):
    private boolean intersectsIcon(int x, int y){
        if(x < this.xpos - 30)
            return false;
        else if(y < this.ypos - 5)
            return false;
        else if((x < (this.xpos -5)) &&
                (y < (this.ypos + this.height + 5)))
            return true;
        else
            return false;
    }
}

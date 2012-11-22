package rcc.graphics;

import java.awt.Color;
import java.awt.Graphics;


public class VideoPlayButton extends Button{
    
    public static final int PAUSE = 1;
    public static final int PLAY = 2;
    
    private int[] playXpoints;
    private int[] playYpoints;
    
    public int playMode;
    
    public VideoPlayButton(int x, int y){
        super("?", x - 15, y - 15, 30, 30);
        
        playMode = PLAY;
        
        playXpoints = new int[] {xpos + 5, xpos + 5, xpos + 21};
        playYpoints = new int[] {ypos + 6, ypos + 20, ypos + 13};
    }
    
    @Override
    public void draw(Graphics g){        
        //set up symbol color
        switch(mode){
            case REGULAR:
                g.setColor(textrc);
                break;
            case HIGHLIGHTED:
                g.setColor(texthc);
                break;
            case DEPRESSED:
                g.setColor(Color.yellow);
                break;
            default:
                return;
        }
        //draw the symbol:
        switch(playMode){
            case 1: //paused, so draw a start play button
                drawPlayButton(g);
                break;
            case 2: //playing, so draw a start pause button
                drawPauseButton(g);
                break;
            default:
                break;
        }
    }
    
    //draw a play graphic on the button's location
    private void drawPlayButton(Graphics g){
        g.fillPolygon(playXpoints, playYpoints, 3);
    }
    
    //draw a pause graphic on the button's location
    private void drawPauseButton(Graphics g){
        g.fillRect(xpos + 7, ypos + 5, 4, 16);
        g.fillRect(xpos + 15, ypos + 5, 4, 16);
    }
    
    
    //set a mode for the button (defaults to pause)
    public void setPlayMode(int playMode){
        this.playMode = playMode;
    }
    
}

package rcc.graphics;
import rcc.MainPanel;
import rcc.video.Screen;

import java.awt.Graphics;
import java.util.ArrayList;


public class VideoControlSet implements Graphic{

    private MainPanel mainPanel;
    
    private ArrayList<Graphic> buttons;
    int numButtons;
    
    private VideoPlayButton playButton;
    private VolumeBar volumeBar;
    private MiniButton aspectDefault;
    private MiniButton aspectWide;
    private MiniButton aspectFull;
    
    public VideoControlSet(int x, int y, MainPanel mainPanel){
        this.mainPanel = mainPanel;
        
        buttons = new ArrayList<Graphic>();
        
        aspectDefault = new MiniButton("Norm", x - 140, y - 6);
        aspectDefault.press();
        buttons.add(aspectDefault);
        aspectWide = new MiniButton("Wide", x - 110, y - 6);
        buttons.add(aspectWide);
        aspectFull = new MiniButton("Full", x - 80, y - 6);
        buttons.add(aspectFull);
        
        //link aspect buttons together:
        aspectDefault.setLinks(aspectWide, aspectFull);
        aspectWide.setLinks(aspectDefault, aspectFull);
        aspectFull.setLinks(aspectDefault, aspectWide);
        
        //pause/continue feed
        playButton = new VideoPlayButton(x, y);
        buttons.add(playButton);
        
        //volume
        volumeBar = new VolumeBar(mainPanel.rcc.settings.volume,
                x + 65, y);
        buttons.add(volumeBar);
        
        numButtons = buttons.size();
    }
    

    @Override
    public void draw(Graphics g) {
        for(int i=0; i<numButtons; i++){
            buttons.get(i).draw(g);
        }
    }
    
    
    //mouse drag event for volume bar:
    public boolean checkMouseDragged(int x, int y){
        int vol = volumeBar.dragged(x, y);
        if(vol >= 0){
            mainPanel.rcc.settings.volume = volumeBar.curVol;
            return true;
        }
        return false;
    }

    
    @Override
    //check all components for mouseover:
    public boolean mouseOverCheck(int x, int y) {
        boolean repaint = false;
        for(int i=0; i<numButtons; i++){
            repaint =( 
                (buttons.get(i).mouseOverCheck(x, y))
                    || repaint);
        }
        return repaint;
    }

    
    @Override
    //Check all components for a click!
    public boolean mouseClickCheck(int x, int y) {
        //first, check the aspect buttons:
        if(aspectDefault.mouseClickCheck(x, y)){
            mainPanel.screen.setAspectRatio(Screen.DEFAULT);
            return true;
        }else if(aspectWide.mouseClickCheck(x, y)){
            mainPanel.screen.setAspectRatio(Screen.WIDESCREEN);
            return true;
        }else if(aspectFull.mouseClickCheck(x, y)){
            mainPanel.screen.setAspectRatio(Screen.FULLSCREEN);
            return true;
        }else{
            boolean repaint = false;
            for(int i=0; i<numButtons; i++){
                repaint =( 
                    (buttons.get(i).mouseClickCheck(x, y))
                        || repaint);
            }
            return repaint;
        }
    }
    

    @Override
    //Check all components for a mouse release
    public boolean mouseReleaseCheck(int x, int y) {
        /*if(aspectDefault.mouseReleaseCheck(x, y)){
            System.err.println("Mini pressed.");
            return true;
        }
        else*/
        if(playButton.mouseReleaseCheck(x, y)){
            if(playButton.playMode == 1)
                playButton.setPlayMode(2);
            else
                playButton.setPlayMode(1);
            mainPanel.screen.pause();
            return true;
        }else if(volumeBar.mouseReleaseCheck(x, y)){
            //nothing i guess
            return true;
        }else
            return false;
    }
    
}

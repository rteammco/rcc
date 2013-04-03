package rcc.video;
import rcc.graphics.Label;
import rcc.SettingsManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import javax.swing.JPanel;


public class Screen extends JPanel{
    
    public static final int DEFAULT = 0;
    public static final int WIDESCREEN = 1;
    public static final int FULLSCREEN = 2;
    
    private SettingsManager settings;
    
    //current frame reference:
    private Image frame;
    private int aspectRatio;
    private String source;
    private String frameErrMsg;
    private boolean frameErr;
    private boolean paused;
    private boolean disconnected;
    private boolean mouseInZone;
    private boolean zoomed;
    private int zoomX;
    private int zoomY;
    
    //constructor (set default background to black),
    // and add mouse action listener:
    public Screen(SettingsManager settings){
        //default the background to black (in case images fail)
        setBackground(Color.black);
        
        this.settings = settings;
        
        /* Instructions for loading an image from a file in Netbeans:
         * 
         * Steps:
         * 1) Create a folder called "resources" in the main
         *      project directory.
         * 2) Add your image to that folder.
         * 3) [in Netbeans]:
         *      - right-click on the project in the
         *          project sidebar to the left, and go to properties.
         *      - select Sources from the left menu
         *      - click Add Folder... to the "Source Package Folders:"
         *          box
         *      - select and open the "resources" folder you made
         *      - click OK
         * 4) Add the code below:
         */
        URL link = this.getClass().getClassLoader()
                .getResource("NullFeed.JPG");
        this.frame = Toolkit.getDefaultToolkit().getImage(link);
        
        source = "No video feed.";
        aspectRatio = settings.aspectRatio;
        paused = false;
        mouseInZone = false;
        zoomed = false;
        zoomX = 0;
        zoomY = 0;
        
        //mouse listener:
        addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent e){}
            @Override
            public void mouseEntered(MouseEvent e){
                if(zoomed == false){
                    mouseInZone = true;
                    repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e){
                mouseInZone = false;
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e){
                mouseInZone = false;
                zoomIn(e.getX(), e.getY());
            }
            @Override
            public void mouseReleased(MouseEvent e){
                zoomOut();
            }
        });
        //and mouse movement (for dragging):
        addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e){
                zoomIn(e.getX(), e.getY());
            }
            @Override
            public void mouseMoved(MouseEvent e){}
        });
    }
    
    //paint the graphics to the screen:
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        //draw the frame to the screen
        if(zoomed){ //if use has frame zoomed in:
            if(aspectRatio == WIDESCREEN){
                g.drawImage(frame,
                        0 - (this.getWidth() / 2 + zoomX),
                        0 - (this.getHeight() / 2 + zoomY),
                        this.getWidth() * 2,
                        this.getHeight() * 2,
                        this);
            }else if(aspectRatio == FULLSCREEN){
                int w = this.getHeight() * 4 / 3;
                int startX = (this.getWidth() - w) / 2;
                g.drawImage(frame,
                        (startX * 2) - (this.getWidth() / 2 + zoomX),
                        0 - (this.getHeight() / 2 + zoomY),
                        w * 2,
                        this.getHeight() * 2,
                        this);
            }else{
                int fWidth = frame.getWidth(this);
                int fHeight = frame.getHeight(this);
                double relation = (double)this.getHeight() / fHeight;
                int w = (int)(fWidth * relation);
                int startX = (this.getWidth() - w) / 2;
                g.drawImage(frame,
                        (startX * 2) - (this.getWidth() / 2 + zoomX),
                        0 - (this.getHeight() / 2 + zoomY),
                        w * 2,
                        this.getHeight() * 2,
                        this);
            }
        }
        else{ //otherwise, frame is normal size:
            if(aspectRatio == WIDESCREEN){
                g.drawImage(frame, 0, 0, this.getWidth(),
                        this.getHeight(), this);
            }else if(aspectRatio == FULLSCREEN){
                int w = this.getHeight() * 4 / 3;
                int startX = (this.getWidth() - w) / 2;
                g.drawImage(frame, startX, 0, w, this.getHeight(), this);
            }else{ //in all other cases, defaults to DEFAULT
                int fWidth = frame.getWidth(this);
                int fHeight = frame.getHeight(this);
                double relation = (double)this.getHeight() / fHeight;
                int w = (int)(fWidth * relation);
                int startX = (this.getWidth() - w) / 2;
                g.drawImage(frame, startX, 0, w, this.getHeight(), this);
            }
        }
        
        if(mouseInZone){
            g.setFont(new Font("Ariel", Font.BOLD, 16));
            Label.drawBoxedLabel(g,
                    Color.gray, Color.white,
                    source, 20, 20);
        }
        
        if(disconnected){
            g.setColor(Color.red);
            g.setFont(new Font("Ariel", Font.BOLD, 40));
            Label.drawCenteredLabel(g,
                    "Robot Disconnected",
                    this.getWidth() / 2,
                    this.getHeight() / 2);
        }else if(paused){
            g.setColor(Color.yellow);
            g.setFont(new Font("Ariel", Font.BOLD, 40));
            Label.drawCenteredLabel(g,
                    "PAUSED",
                    this.getWidth() / 2,
                    this.getHeight() / 2);
        }else if(frameErr){
            g.setColor(Color.red);
            g.setFont(new Font("Ariel", Font.BOLD, 40));
            Label.drawCenteredLabel(g,
                    "FRAME ERROR: " + frameErrMsg,
                    this.getWidth() / 2,
                    this.getHeight() / 2);
        }
    }
    
    //zoom into video around the given X and Y coordinates
    private void zoomIn(int x, int y){
        if(x >= 0 && x <= this.getWidth())
            zoomX = x - (this.getWidth() / 2);
        if(y >= 0 && y <= this.getHeight())
            zoomY = y - (this.getHeight() / 2);
        zoomed = true;
        repaint();
    }
    
    //zoom out of video back to normal
    private void zoomOut(){
        zoomed = false;
        repaint();
    }
    
    //call to update to the next given frame:
    public void updateFrame(Image frame){
        if(!paused){
            frameErr = false;
            this.frame = frame;
            repaint();
        }
    }
    
    
    //pauses the video if playing, and resumes it if
    // it is not.
    public void pause(){
        paused = !paused;
        repaint();
    }
    
    
    //adjust the aspect ratio of the screen. Use the static
    // final ints to determine aspect ratio.
    public void setAspectRatio(int aspectRatio){
        this.aspectRatio = aspectRatio;
        settings.aspectRatio = aspectRatio;
        repaint();
    }
    
    
    //tells the screen to assume that the robot has disconnected,
    // displaying the appropriate visual messages.
    public void disconnected(){
        disconnected = true;
        this.source = "Disconnected from " + source;
        repaint();
    }
    
    
    //sets the image on screen to display the "loading" image,
    // waiting for the video stream to be established.
    public void setToLoading(){
        URL link = this.getClass().getClassLoader()
                .getResource("Loading.JPG");
        this.frame = Toolkit.getDefaultToolkit().getImage(link);
        repaint();
    }
    
    
    //tells the screen to connect, or re-connect; that is,
    // drops any displays of connectivity, and resets to
    // default values, and sets the source to the passed-in
    // string value.
    public void connected(String source){
        if(source.length() == 0){
            
        }
        disconnected = false;
        paused = false;
        aspectRatio = DEFAULT;
        this.source = source;
    }
    
    
    //removes any "disconnected" text from the screen:
    public void removeDisconnectedMessage(){
        disconnected = false;
        repaint();
    }
    
    
    //displays an error on the screen, in regards that the
    // frame was not correctly constructed.
    public void frameError(String message){
        frameErr = true;
        frameErrMsg = message;
    }
}

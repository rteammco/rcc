package rcc.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class TextField extends Button implements Animated{
    
    
    protected static final int characterSpace = 8;
    
    protected String contents;
    public int LAST_POSITION;
    
    protected int labelW;
    
    protected int cursorPos;
    public int CURSOR_INDEX;
    protected int H_START_INDEX;
    protected int H_END_INDEX;
    protected boolean cursorOn;
    private int frameCount;
    
    public boolean selected; //if selected, it is "highlighted"
    
    protected boolean initTextOn;
    protected String initText;
    
    
    //CONSTRUCTOR: label is the label to the left,
    // x and y are the position (starting with the text box
    // as a rectangle, and initText is the text that gets
    // displayed for the first time before user highlights
    // it.
    public TextField(String label, int x, int y, String initText){
        super(label, x, y, 200, 20);
        
        contents = "";
        LAST_POSITION = 0;
        
        labelW = -1;
        
        cursorPos = 0;
        CURSOR_INDEX = 0;
        H_START_INDEX = CURSOR_INDEX;
        H_END_INDEX = CURSOR_INDEX;
        cursorOn = true;
        frameCount = 0;
        
        selected = false;
        
        this.initTextOn = true;
        this.initText = initText;
    }
    
    //Draw to the passed-in graphics
    @Override
    public void draw(Graphics g){
        
        //if selected, draw a selected outline:
        if(selected){
            g.setColor(Color.yellow);
            g.fillRect(xpos - 2, ypos - 2,
                    width + 4, height + 4);
        }
        
        //draw the rectangle box:
        g.setColor(textrc);
        g.fillRect(xpos, ypos, width, height);

        if(mode == HIGHLIGHTED){
            g.setColor(Color.cyan);
            g.drawRect(xpos, ypos, width, height);
        }
        
        //draw text highlight:
        if(selected){
            if((H_END_INDEX - H_START_INDEX) > 0){
                g.setColor(Color.cyan);
                g.fillRect(xpos + 4 + (H_START_INDEX * characterSpace),
                        ypos + 2,
                        (H_END_INDEX - H_START_INDEX) * characterSpace,
                        height - 4);
            }
            else if((H_START_INDEX - H_END_INDEX) > 0){
                g.setColor(Color.cyan);
                g.fillRect(xpos + 4 + (H_END_INDEX * characterSpace),
                        ypos + 2,
                        (H_START_INDEX - H_END_INDEX) * characterSpace,
                        height - 4);
            }
        }
        
        //draw the text:
        g.setColor(backrc);
        g.setFont(defaultFont);
        //if labelLen not yet calculated, find out:
        if(labelW < 0)
            labelW = Label.getTextWidth(g, text);
        Label.drawCenteredLabelOnY(g, text, xpos - labelW - 15,
                ypos + (height / 2));
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        Label.drawCenteredLabelOnY(g, contents, xpos + 5,
                ypos + (height / 2) - 1);
        
        //draws init text if initTextOn is true,
        // meaning no other text is displayed at the time.
        if(initTextOn){
            g.setColor(Color.gray);
            Label.drawCenteredLabelOnY(g, initText, xpos + 5,
                ypos + (height / 2) - 1);
        }
        
        //draw the cursor in its position (if it is on):
        if(selected && cursorOn){
            cursorPos = CURSOR_INDEX * characterSpace;
            g.drawLine(xpos + 4 + cursorPos, ypos + 2,
                    xpos + 4 + cursorPos, ypos + height - 4);
        }
    }
    
    
    //override for a mouse click check (sets the cursor position):
    @Override
    public boolean mouseClickCheck(int x, int y){
        if(intersects(x, y)){
            //locate and approx cursor position
            
            int relativePos = x - xpos - 2;
            CURSOR_INDEX = (relativePos / characterSpace);
            if(CURSOR_INDEX >= LAST_POSITION)
                CURSOR_INDEX = LAST_POSITION;
            H_START_INDEX = CURSOR_INDEX;
            H_END_INDEX = CURSOR_INDEX;
            selected = true;
            initTextOn = false;
            frameCount = 0;
            return true;
        }else{ //more complicated deselection method:
            // returns true if it was selected and no longer is
            // to repaint a deselection!
            boolean retval = false;
            if(selected)
                retval = true;
            selected = false;
            if(contents.length() == 0)
                initTextOn = true;
            return retval;
        }
    }
    
    
    public boolean mouseDragCheck(int x, int y){
        if(intersects(x, y)){
            int relativePos = x - xpos - 2;
            CURSOR_INDEX = (relativePos / characterSpace);
            if(CURSOR_INDEX >= LAST_POSITION)
                CURSOR_INDEX = LAST_POSITION;
            H_END_INDEX = CURSOR_INDEX;
            frameCount = 0;
            return true;
        }else{
            return selected;
        }
    }
    
    
    //forces the cursor to move a position left or right (0 or 1),
    // respectively, if possible!
    public void moveCursor(int direction){
        if(direction == 0 && cursorPos != 0)
            CURSOR_INDEX--;
        else if(direction == 1 && cursorPos !=
                LAST_POSITION * 8)
            CURSOR_INDEX++;
    }
    
    
    //insert a letter here to the given position,
    // positions are indexed from 0 to length - 1.
    public void insert(int pos, char letter){
        if(H_END_INDEX - H_START_INDEX != 0){
            delete(pos);
            pos = CURSOR_INDEX;
        }
        
        if(LAST_POSITION >= 23) //cap limit at 23
            return;
        
        if(pos > LAST_POSITION){
            contents += letter;
            LAST_POSITION++;
            CURSOR_INDEX++;
        }else if(pos >= 0){
            contents = contents.substring(0, pos) + letter +
                    contents.substring(pos, (LAST_POSITION));
            LAST_POSITION++;
            CURSOR_INDEX++;
        }
        frameCount = -5;
    }
    
    //delete a letter from given position,
    // position are indexed from 0 to lenght - 1:
    public void backspace(int pos){
        if(H_END_INDEX - H_START_INDEX != 0){
            if(H_START_INDEX < H_END_INDEX){
                contents = contents.substring(0, H_START_INDEX) +
                        contents.substring(H_END_INDEX, LAST_POSITION);
                CURSOR_INDEX = H_START_INDEX;
            }else{
                contents = contents.substring(0, H_END_INDEX) +
                        contents.substring(H_START_INDEX, LAST_POSITION);
                CURSOR_INDEX = H_END_INDEX;
            }
            LAST_POSITION = contents.length();
            H_END_INDEX = 0;
            H_START_INDEX = 0;
        }
        
        else if(pos >= 0 && pos <= LAST_POSITION){
            contents = contents.substring(0, pos) +
                    contents.substring((pos + 1), (LAST_POSITION));
            LAST_POSITION--;
            CURSOR_INDEX--;
        }
        
        frameCount = -5;
    }
    
    //delete a letter from given position,
    // position are indexed from 0 to lenght - 1:
    public void delete(int pos){
        if(H_END_INDEX - H_START_INDEX != 0){
            if(H_START_INDEX < H_END_INDEX){
                contents = contents.substring(0, H_START_INDEX) +
                        contents.substring(H_END_INDEX, LAST_POSITION);
                CURSOR_INDEX = H_START_INDEX;
            }else{
                contents = contents.substring(0, H_END_INDEX) +
                        contents.substring(H_START_INDEX, LAST_POSITION);
                CURSOR_INDEX = H_END_INDEX;
            }
            LAST_POSITION = contents.length();
            H_END_INDEX = 0;
            H_START_INDEX = 0;
        }
        
        else if(pos >= 0 && pos <= LAST_POSITION &&
                contents.length() > pos){
            contents = contents.substring(0, pos) +
                    contents.substring((pos + 1), (LAST_POSITION));
            LAST_POSITION--;
        }
        
        frameCount = -5;
    }
    
    
    //returns the containing text inside the field:
    public String getText(){
        return contents;
    }
    
    
    //returns the part of the text that is highlighted:
    public String getHighlightedText(){
        return contents.substring(H_START_INDEX, H_END_INDEX);
    }
    
    
    //sets containing text to the passed in string, and
    // sets cursor index and last position to the end.
    public void setText(String newText){
        contents = newText;
        CURSOR_INDEX = newText.length();
        LAST_POSITION = CURSOR_INDEX;
        if(!selected && contents.length() == 0)
            initTextOn = true;
        else
            initTextOn = false;
    }
    
    
    //sets this as selected
    public void setSelected(boolean selected){
        this.selected = selected;
        if(!selected && contents.length() == 0)
            this.initTextOn = true;
        else
            this.initTextOn = false;
        frameCount = 0;
    }
    
    
    //ANIMATED update frame (cursor blink):
    // Call at 2 (TWO) frames per second.
    @Override
    public void updateFrame(){
        if(selected){
            frameCount++;
            if(frameCount >= 30)
                frameCount = 0;
            if(frameCount < 15)
                cursorOn =  true;
            else
                cursorOn = false;
        }
    }
}

package rcc.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author Richard
 */
public class PasswordField extends TextField{
    
    private String starredContents;
    
    public PasswordField(String label, int x, int y, String initText){
        super(label, x, y, initText);
        starredContents = "";
    }
    
    //Draw to the passed-in graphics
    @Override
    public void draw(Graphics g){
        String originalContents = contents;
        contents = starredContents;
        super.draw(g);
        contents = originalContents;
        /*
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
            g.setColor(Color.cyan);
            g.fillRect(xpos + 4 + (H_START_INDEX * characterSpace),
                    ypos + 2,
                    (H_END_INDEX - H_START_INDEX) * characterSpace,
                    height - 4);
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
        //draw the starredContents instead of contents:
        Label.drawCenteredLabelOnY(g, starredContents, xpos + 5,
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
            cursorPos = CURSOR_INDEX * 8;
            g.drawLine(xpos + 4 + cursorPos, ypos + 2,
                    xpos + 4 + cursorPos, ypos + height - 4);
        }
         * */
    }

    
    //insert a letter here to the given position,
    // positions are indexed from 0 to length - 1.
    @Override
    public void insert(int pos, char letter){
        super.insert(pos, letter);
        fillStars();
    }
    
    //delete a letter from given position,
    // position are indexed from 0 to lenght - 1:
    @Override
    public void backspace(int pos){
        super.backspace(pos);
        fillStars();
    }
    
    //delete a letter from given position,
    // position are indexed from 0 to lenght - 1:
    @Override
    public void delete(int pos){
        super.delete(pos);
        fillStars();
    }

    //sets containing text to the passed in string, and
    // sets cursor index and last position to the end.
    @Override
    public void setText(String newText){
        super.setText(newText);
        fillStars();
    }
    
    //Fills out stars based on contents length of string
    private void fillStars(){
        starredContents = "";
        for(int i=0; i<contents.length(); i++){
            starredContents += "*";
        }
    }
}

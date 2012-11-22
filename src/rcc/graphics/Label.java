package rcc.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;


public class Label implements Graphic{
    
    //standard label font format:
    public static final Font defaultFont =
            new Font("Ariel", Font.BOLD, 14);
    //large font format:
    public static final Font largeFont =
            new Font("Ariel", Font.BOLD, 20);
    
    protected int xpos;
    protected int ypos;
    protected String contents;
    protected Color color;
    protected Font font;
    
    //CONSTRUCTOR: it's own method drawable format!
    // This constructor sets the label at dead-center
    // of the X and Y.
    public Label(int x, int y, String contents, Font font,
            Color color){
        xpos = x;
        ypos = y;
        this.contents = contents;
        this.font = font;
        this.color = color;
    }
    
    
    //Draw to the given Graphics object (draws itself!)
    @Override
    public void draw(Graphics g){
        if(contents.length() == 0)
            return;
        g.setFont(font);
        g.setColor(color);
        drawCenteredLabel(g, contents, xpos, ypos);
    }
    
    
    //sets text of this label:
    public void setText(String contents){
        this.contents = contents;
    }
    
    
    //mouse action checks (overrides, not actually used):
    // ALL RETURN FALSE
    @Override
    public boolean mouseOverCheck(int x, int y){
        return false;
    }
    @Override
    public boolean mouseClickCheck(int x, int y){
        return false;
    }
    @Override
    public boolean mouseReleaseCheck(int x, int y){
        return false;
    }
    
    
    //draws given text from the given graphics distinctly
    // centered on the given x and y coordinates.
    public static void drawCenteredLabel(Graphics g, String text,
            int x, int y){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        int textWidthPlus = (int)(rect.getWidth() / 2);
        int textHeightPlus = (int)(rect.getHeight() / 3);
        g.drawString(text, x - textWidthPlus, y + textHeightPlus);
    }
    
    
    //draws the given text inside a gray box, the rectangled
    // text drawn inside the rectangle scaled to the text's size
    public static void drawBoxedLabel(Graphics g,
            Color rectColor, Color stringColor,
            String text,
            int x, int y){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        g.setColor(rectColor);
        g.fillRect(x, y, (int)rect.getWidth() + 10,
                (int)rect.getHeight());
        g.setColor(stringColor);
        g.drawString(text, x + 5, y + (int)(rect.getHeight() / 1.2));
    }
    
    
    //draws the given string on the given graphics, but only
    // centered upon the Y-axis (starting exactly at the given x):
    public static void drawCenteredLabelOnY(Graphics g, String text,
            int x, int y){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        int textHeightPlus = (int)(rect.getHeight() / 3);
        g.drawString(text, x, y + textHeightPlus);
    }
    
    
    //returns the width of the given string of text:
    public static int getTextWidth(Graphics g, String text){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        return (int)rect.getWidth();
    }
    
    //returns the height  of the given string of text:
    public static int getTextHeight(Graphics g, String text){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, g);
        return (int)rect.getHeight();
    }
    
}

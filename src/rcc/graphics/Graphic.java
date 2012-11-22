package rcc.graphics;

import java.awt.Graphics;


public interface Graphic {
    
    //draw to graphics:
    public void draw(Graphics g);
    
    //mouse action checks
    public boolean mouseOverCheck(int x, int y);
    public boolean mouseClickCheck(int x, int y);
    public boolean mouseReleaseCheck(int x, int y);
}

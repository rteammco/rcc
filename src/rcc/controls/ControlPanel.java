package rcc.controls;

import java.awt.event.KeyEvent;


public interface ControlPanel {
    
    //key buttons:
    public void handleKeyPress(KeyEvent e);
    public void handleKeyRelease(KeyEvent e);
    
    //tab-cycle components:
    public void nextComponent();
}

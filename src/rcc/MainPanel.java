package rcc;
import rcc.controls.ConfigControls;
import rcc.controls.ControlPanel;
import rcc.controls.InitControls;
import rcc.controls.MainControls;
import rcc.controls.SelectionControls;
import rcc.controls.WaitControls;
import rcc.video.Screen;

import javax.swing.JSplitPane;


public class MainPanel extends JSplitPane{
    
    public RCC rcc;
    public Screen screen;
    
    public int height;
    
    public ControlPanel curPanel;
    
    public MainPanel(RCC rcc, Screen screen, final int height){
        super(JSplitPane.VERTICAL_SPLIT);
        
        this.rcc = rcc;
        this.screen = screen;
        this.height = height;
        
        setDividerSize(2);
        setEnabled(false);
        //double divloc = 100.0 / height
        //setDividerLocation(height / 4 * 3);
        //setDividerLocation(0.25);
        setResizeWeight(0.66);
        
        setTopComponent(screen);
        
        //setToInitControls();
        //setToMainControls();
        
        /*Connector conn = new Connector(rcc, "", "", "", 0);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Robot1");
        list.add("1234567890123456789012345");
        list.add("iRobot_by_APPLE");
        setToSelectionControls(new SelectionControls(conn, list));*/
    }
    
    //sets the control panel to main robot controls
    public void setToInitControls(){
        InitControls initCtrls = new InitControls(this);
        setBottomComponent(initCtrls);
        curPanel = initCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    //sets the control panel to main robot controls, WITH error message
    // constructor:
    public void setToInitControls(String error){
        InitControls initCtrls = new InitControls(this, error);
        setBottomComponent(initCtrls);
        curPanel = initCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    //set to configure options (ip and port setup)
    public void setToConfigControls(){
        ConfigControls configCtrls = new ConfigControls(this);
        setBottomComponent(configCtrls);
        curPanel = configCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    //sets the control panel to wait controls (trying to connect)
    public void setToWaitControls(int status){
        WaitControls waitCtrls = new WaitControls(this, status);
        setBottomComponent(waitCtrls);
        curPanel = waitCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    //set robot to selection controls (given a pre-made panel)
    public void setToSelectionControls(SelectionControls selCtrls){
        setBottomComponent(selCtrls);
        curPanel = selCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    //sets the control panel to initial (pre-connection) controls
    public void setToMainControls(){
        MainControls mainCtrls = new MainControls(this);
        setBottomComponent(mainCtrls);
        curPanel = mainCtrls;
        //setDividerLocation(height / 4 * 3);
    }
    
    /*
    //OVERRIDES: Divider location, so it stops messing up:
    @Override
    public int getMinimumDividerLocation(){
        return (height / 4 * 3);
    }
    @Override
    public int getMaximumDividerLocation(){
        return (height / 4 * 3);
    }*/
}

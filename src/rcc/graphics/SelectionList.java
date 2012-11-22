package rcc.graphics;

import java.awt.Graphics;
import java.util.ArrayList;


public class SelectionList implements Graphic {
    
    
    private ArrayList<InteractiveLabel> list;
    private int listSize;
    
    private int xpos; //centered on X
    private int ypos; //starting with y as top
    
    private int spacing;
    
    public int lastSelectionIndex;
    
    
    public SelectionList(int x, int y, ArrayList<String> stringList,
            int spacing){
        
        this.list = new ArrayList<InteractiveLabel>();
        listSize = 0;
        
        this.spacing = spacing;
        this.xpos = x;
        this.ypos = y;
        
        lastSelectionIndex = 0;
        
        changeList(stringList);
    }
    
    
    @Override
    //Draw itself
    public void draw(Graphics g){
        for(int i=0; i<listSize; i++){
            list.get(i).draw(g);
        }
    }
    
    
    //update the status of the contents of this list:
    public void changeList(ArrayList<String> newList){
        this.list.clear();
        int curY = 0;
        for(int i=0; i<newList.size(); i++){
            this.list.add(new InteractiveLabel(
                    xpos, ypos + curY,
                    newList.get(i), Label.defaultFont));
            curY += spacing;
        }
        listSize = list.size();
    }
    

    @Override
    public boolean mouseOverCheck(int x, int y) {
        boolean retval = false;
        for(int i=0; i<listSize; i++){
            retval = retval || list.get(i).mouseOverCheck(x, y);
        }
        return retval;
    }

    @Override
    public boolean mouseClickCheck(int x, int y) {
        for(int i=0; i<listSize; i++){
            if(list.get(i).mouseClickCheck(x, y)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleaseCheck(int x, int y) {
        for(int i=0; i<listSize; i++){
            if(list.get(i).mouseReleaseCheck(x, y)){
                lastSelectionIndex = i;
                return true;
            }
        }
        return false;
    }
}

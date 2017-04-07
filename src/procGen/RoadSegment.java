
package procGen;

import ai.Path.PathNode;
import static main.Game.path;
import processing.core.PGraphics;
import util.interfaces.*;

/**
 *
 * @author Nithin
 */
public final class RoadSegment implements Drawable, Disposable {
    
    private int i0, j0, i1, j1;
    
    private final int dir;
    private final float cellWidth, cellHeight;
    private boolean dead = false;
    
    private static final int
            DIR_RIGHT = 0,
            DIR_DOWN = 1,
            DIR_LEFT = 2,
            DIR_UP = 3;
    
    public RoadSegment(int i0, int j0, int i1, int j1, PathNode p, PathNode p1, float cellWidth, float cellHeight) {
        
        //path.createRawSegment(p, p1, true);
        
        this.i0 = i0;
        this.j0 = j0;
        this.i1 = i1;
        this.j1 = j1;
        
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        
        int difI = i1-i0, difJ = j1-j0;
        
        if(difI == 1) dir = DIR_RIGHT;
        else if(difI == -1) dir = DIR_LEFT;
        else if(difJ == 1) dir = DIR_DOWN;
        else dir = DIR_UP;
    }
    
    public boolean containsPoint(int i, int j) {
        return !dead && ((i==i0 && j==j0) || (i==i1 && j==j1));
    }
    
    public boolean withinBounds(int iStart, int jStart, int iEnd, int jEnd) {
        return  !dead && (
                (i0 >= iStart && i0 < iEnd && j0 >= jStart && j0 < jEnd) ||
                (i1 >= iStart && i1 < iEnd && j1 >= jStart && j1 < jEnd));
    }
    
    public boolean isSame(int i2, int j2, int i3, int j3) {
        return  !dead && (
                (i0==i2 && j0==j2 && i1==i3 && j1==j3) ||
                (i1==i2 && j1==j2 && i0==i3 && j0==j3));
    }
    
    public void translate(int ti, int tj) {
        
        i0 += ti;
        j0 += tj;
        
        i1 += ti;
        j1 += tj;
    }
    
    @Override
    public void render(PGraphics g) {
        if(dead) return;
        switch(dir) {
            case DIR_RIGHT: 
                g.rect((i0-0.4f)*cellWidth, (j0+0.4f)*cellHeight, (i1+0.4f)*cellWidth, (j0-0.4f)*cellHeight);
                break;
            case DIR_LEFT: 
                g.rect((i0+0.5f+0.4f)*cellWidth, (j0+0.2f)*cellHeight, (i0-1.0f-0.4f)*cellWidth, (j0+0.8f)*cellHeight);
                break;
            case DIR_DOWN: 
                g.rect((i0+0.4f)*cellWidth, (j0-0.4f)*cellHeight, (i0-0.4f)*cellWidth, (j0+1.0f+0.4f)*cellHeight);
                break;
            case DIR_UP: g.rect((i0+0.2f)*cellWidth, (j0+0.5f+0.4f)*cellHeight, (i0+0.8f)*cellWidth, (j0-1.0f-0.4f)*cellHeight);
        }
    }
    
    @Override
    public void dispose() {
        dead = true;
    }
    
    @Override
    public boolean isDead() {
        return dead;
    }
}

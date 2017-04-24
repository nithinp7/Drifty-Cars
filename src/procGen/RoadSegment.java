
package procGen;

import procGen.MapGen.MapCoord;
import processing.core.PGraphics;
import util.interfaces.*;

/**
 *
 * @author Nithin
 */
public final class RoadSegment implements Drawable, Disposable {
    
    protected MapCoord a, b;
    
    private final int dir;
    private final float cellWidth, cellHeight;
    private boolean dead = false;
    
    private static final int
            DIR_RIGHT = 0,
            DIR_DOWN = 1,
            DIR_LEFT = 2,
            DIR_UP = 3;
    
    public RoadSegment(MapCoord a, MapCoord b, float cellWidth, float cellHeight) {
        
        this.a = a;
        this.b = b;
        
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        
        int difI = b.i()-a.i(), difJ = b.j()-a.j();
        
        if(difI == 1) dir = DIR_RIGHT;
        else if(difI == -1) dir = DIR_LEFT;
        else if(difJ == 1) dir = DIR_DOWN;
        else dir = DIR_UP;
    }
    
    public void fix() {
        a.fix();
        b.fix();
    }
    
    public boolean containsPoint(MapCoord point) {
        return !dead && (a.isSame(point) || b.isSame(point));
    }
    
    public boolean withinBounds(int iStart, int jStart, int iEnd, int jEnd) {
        return  !dead && 
                a.withinBounds(iStart, jStart, iEnd, jEnd) &&
                b.withinBounds(iStart, jStart, iEnd, jEnd);
    }
    
    public boolean withinMap() {
        return !dead &&
               a.withinMap() &&
               b.withinMap();
    }
    
    public boolean isSame(MapCoord a_, MapCoord b_) {
        return  !dead &&
                a.isSame(a_) &&
                b.isSame(b_);
    }
    
    @Override
    public void render(PGraphics g) {
        if(dead) return;
        
        a.fix();
        b.fix();
        
        switch(dir) {
            case DIR_RIGHT: 
                g.rect((a.i()-0.4f)*cellWidth, (a.j()+0.4f)*cellHeight, (b.i()+0.4f)*cellWidth, (a.j()-0.4f)*cellHeight);
                break;
            case DIR_LEFT: 
                g.rect((a.i()+0.5f+0.4f)*cellWidth, (a.j()+0.2f)*cellHeight, (a.i()-1.0f-0.4f)*cellWidth, (a.j()+0.8f)*cellHeight);
                break;
            case DIR_DOWN: 
                g.rect((a.i()+0.4f)*cellWidth, (a.j()-0.4f)*cellHeight, (a.i()-0.4f)*cellWidth, (a.j()+1.0f+0.4f)*cellHeight);
                break;
            case DIR_UP: g.rect((a.i()+0.2f)*cellWidth, (a.j()+0.5f+0.4f)*cellHeight, (a.i()+0.8f)*cellWidth, (a.j()-1.0f-0.4f)*cellHeight);
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

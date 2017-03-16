
package entities.surface;

import static main.Game.*;
import static main.Main.c;
import static util.Constants.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import static processing.core.PApplet.abs;
import static processing.core.PConstants.CENTER;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 *
 * @author Nithin
 */
public final class Floor {
    
    public final Body floor;
    private final PGraphics floorLayer;
    public final int w, h;
    
    private final Vec2 floorTrans;// = new Vec2(0, 0);
    
    public Floor(Vec2 loc, Vec2 size, Vec2 tileSize) {
        
        floorTrans = new Vec2(0, 0);
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(0, 0);
        bd.angularDamping = 1f;
        bd.linearDamping = 1f;
        
        floor = box2d.createBody(bd);
        
        w = 1200;
        h = 1200;
        
        floorLayer = c.createGraphics(w, h);
        floorLayer.beginDraw();
        
        floorLayer.fill(200, 220, 240, 255);
        floorLayer.noStroke();
        
        floorLayer.rect(0, 0, w, h);
        floorLayer.endDraw();
    }
    
    public void update() {
        float cx = getCamTransX(), cy = getCamTransY();
        Vec2 dif = new Vec2(cx-floorTrans.x, cy-floorTrans.y);
        
        if(abs(dif.x) < w/4 && abs(dif.y) < h/4) return;
        
        PImage temp = floorLayer.get((int)dif.x, (int)dif.y, w, h);
        floorLayer.rect(0, 0, w, h);
        floorLayer.image(temp, 0, 0, w, h);
        
        floorTrans.set(cx, cy);
    }
    
    public void render() {
        int imMode = c.g.imageMode;
        c.imageMode(CENTER);
        c.image(floorLayer, floorTrans.x+WIDTH/2, floorTrans.y+HEIGHT/2, w, h);
        c.imageMode(imMode);
    }
    
    public PGraphics getFloorLayer() {
        return floorLayer;
    }
    
    public float getTransX() {
        return floorTrans.x;
    }
    
    public float getTransY() {
        return floorTrans.y;
    }
}

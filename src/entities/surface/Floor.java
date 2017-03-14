
package entities.surface;

import static main.Game.*;
import static main.Main.c;
import static util.Constants.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import static processing.core.PApplet.abs;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 *
 * @author Nithin
 */
public final class Floor {
    
    public final Body floor;
    private final PGraphics floorLayer;
    
    private final Vec2 floorTrans;// = new Vec2(0, 0);
    
    public Floor(Vec2 loc, Vec2 size, Vec2 tileSize) {
        
        floorTrans = new Vec2(0, (HEIGHT-WIDTH)*0.5f);
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(0, 0);
        bd.angularDamping = 1f;
        bd.linearDamping = 1f;
        
        floor = box2d.createBody(bd);
        
        floorLayer = c.createGraphics(WIDTH, WIDTH);
        floorLayer.beginDraw();
        
        floorLayer.fill(200, 220, 240, 255);
        floorLayer.noStroke();
        floorLayer.rect(0, 0, WIDTH, WIDTH);
        floorLayer.endDraw();
//        
//        asph = getTextureImage(ASPHALT_TEX);
//        
//        asph.loadPixels();
//        for(int i=0; i<asph.pixels.length; i++) {
//            int pix = asph.pixels[i];
//            //asph.pixels[i] = c.color(2f*c.red(pix), 1.0f*c.green(pix), 0.6f*c.blue(pix));
//            asph.pixels[i] = c.color(1.2f*c.red(pix), 1.2f*c.green(pix), 1.2f*c.blue(pix));
//        }
//        asph.updatePixels();
        //asph.filter(DILATE);
//        /trackMarksLayer.image(asph, 0, 0, 2*WIDTH, 2*HEIGHT);
//        for(int i=0; i<20; i++) for(int j=0; j<20; j++) trackMarksLayer.image(asph, i*WIDTH/20, j*HEIGHT/20, WIDTH/20, HEIGHT/20);
    }
    
    public void update() {
        float cx = getCamTransX(), cy = getCamTransY()+(HEIGHT-WIDTH)*0.5f;
        Vec2 dif = new Vec2(cx-floorTrans.x, cy-floorTrans.y);
        
        if(abs(dif.x) < WIDTH/10 && abs(dif.y) < WIDTH/10) return;
        
        PImage temp = floorLayer.get((int)dif.x, (int)dif.y, WIDTH, WIDTH);
        floorLayer.rect(0, 0, WIDTH, WIDTH);
        //floorLayer.image(temp, floorTrans.x-dif.x, floorTrans.y-dif.y, WIDTH, HEIGHT);
        floorLayer.image(temp, 0, 0, WIDTH, WIDTH);
        
        floorTrans.set(cx, cy);
    }
    
    public void render() {
        c.image(floorLayer, floorTrans.x, floorTrans.y, WIDTH, WIDTH);
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

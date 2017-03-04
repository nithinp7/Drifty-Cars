
package entities.building;

import static main.Main.*;
import static main.Game.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import processing.core.PGraphics;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public final class Building implements Drawable {
    
    private final Body body;
    
    private final float x, y, theta, l, l_pixels, w, w_pixels, h, h_pixels;
    private final Vec2 pos_pix;
    
    public Building(float x, float y, float theta, float l, float w, float h) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.l = l;
        this.w = w;
        this.h = h;
        
        pos_pix = box2d.coordWorldToPixels(x, y);
        l_pixels = box2d.scalarWorldToPixels(l);
        w_pixels = box2d.scalarWorldToPixels(w);
        h_pixels = box2d.scalarWorldToPixels(h);
        
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(l/2, w/2);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.restitution = 0.5f;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(x, y);
        bd.angle = theta;
        
        body = box2d.createBody(bd);
        body.createFixture(fd);
    }
    
    public Building(Vec2 pos, float theta, float l, float w, float h) {
        this(pos.x, pos.y, theta, l, w, h);
    }
    
    public void update() {
        
    }
    
    @Override
    public void render(PGraphics g) {
        Vec2 pos = box2d.coordWorldToPixels(x, y);
        g.pushMatrix();
            g.translate(pos.x, pos.y, h_pixels/2);
            g.rotate(-theta);
            g.fill(110, 130, 140);
            g.box(l_pixels, w_pixels, h_pixels);
        g.popMatrix();
    }
}

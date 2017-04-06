
package entities.building;

import java.util.HashMap;
import static main.Game.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.FrictionJointDef;
import processing.core.PGraphics;
import static util.Constants.*;
import util.interfaces.Disposable;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public final class Block implements Drawable, Disposable {
    
    private final Body body;
    
    private final float x, y, theta, l, l_pixels, w, w_pixels, h, h_pixels;
    private final Vec2 pos_pix;
    
    private boolean dead = false;
    
    public Block(float x, float y, float theta, float l, float w, float h, boolean fixed) {
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
        if(!fixed) fd.density = 1000/(l*w*h);
        fd.restitution = 0.5f;
        
        BodyDef bd = new BodyDef();
        bd.type = fixed ? BodyType.STATIC : BodyType.DYNAMIC;
        bd.position.set(x, y);
        bd.angle = theta;
        
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("TYPE", TYPE_BUILDING);
        
        body = box2d.createBody(bd);
        body.createFixture(fd).setUserData(userData);
        
        FrictionJointDef fric = new FrictionJointDef();
        fric.bodyA = body;
        fric.bodyB = floor.floor;
        fric.localAnchorA.set(0, 0);
        fric.localAnchorB.set(0, 0);
        fric.maxForce = 3400;
        fric.maxTorque = 3000;
        
        box2d.createJoint(fric);
    }
    
    public Block(Vec2 pos, float theta, float l, float w, float h, boolean fixed) {
        this(pos.x, pos.y, theta, l, w, h, fixed);
    }
    
    public void update() {
        
    }
    
    @Override
    public void render(PGraphics g) {
        Vec2 pos = box2d.coordWorldToPixels(body.getPosition());
        g.pushStyle();
        g.pushMatrix();
            //g.noStroke();
            g.translate(pos.x, pos.y, h_pixels/2);
            g.rotate(-body.getAngle());
            //g.fill(176, 80, 180);
            g.fill(90, 60, 70);
            g.box(l_pixels, w_pixels, h_pixels);
        g.popMatrix();
        g.popStyle();
    }
    
    @Override
    public void dispose() {
        box2d.world.destroyBody(body);
        dead = true;
    }
    
    @Override
    public boolean isDead() {
        return dead;
    }
}

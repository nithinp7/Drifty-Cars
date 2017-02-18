
package entities.car;

import java.awt.Color;
import static main.Game.*;
import static main.Main.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public abstract class Car implements Drawable {
    
    public float turn = 0,
                 throttle = 0,
                 maxTurnTorque = 10000;
    
    public boolean reverse = false;
    
    public final Body chasis;
    public final Axle frontAxle, rearAxle;
    
    private final float l, l_pixels, w, w_pixels, h, h_pixels;
    
    private final Color color;
    
    public Car(float x, float y, float theta, float l, float w, float h) {
        
        this.l = l;
        this.w = w;
        this.h = h;
        
        l_pixels = box2d.scalarWorldToPixels(l);
        w_pixels = box2d.scalarWorldToPixels(w);
        h_pixels = box2d.scalarWorldToPixels(h);
        
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(l/2, w/2);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = 1000/(l*w*h);
        fd.friction = 3;
        fd.restitution = 0.5f;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, y);
        bd.angle = theta;
        
        chasis = box2d.createBody(bd);
        chasis.createFixture(fd);
        
        frontAxle = new Axle(1.1f*w, l/8, 0.5f, theta, chasis, l/4, 0, PI/6);
        rearAxle = new Axle(1.5f*w, l/7, 0.6f, theta, chasis, -l/4, 0, 0);
        
        color = new Color((int)c.random(150, 250), (int)c.random(150, 250), (int)c.random(150, 250));
    }
    
    public Car(Vec2 loc, float theta, float l, float w, float h) {
        this(loc.x, loc.y, theta, l, w, h);
    }
    
    public void update() {
        updateSteering();
        drive();
        frontAxle.update();
        rearAxle.update();
    }
    
    private void drive() {
        throttle = constrain(throttle, 0, 1);
        float theta = frontAxle.axle.getAngle();
        frontAxle.axle.applyForceToCenter(new Vec2(cos(theta), sin(theta)).mul(22000 * throttle * (reverse ? -1 : 1)));
    }
    
    private void updateSteering() {
        turn = constrain(turn, -1, 1);
        frontAxle.axle.applyTorque(turn*maxTurnTorque);
    }
    
    @Override
    public void render() {
        frontAxle.render();
        rearAxle.render();
        c.pushMatrix();
            Vec2 pos = box2d.coordWorldToPixels(chasis.getPosition());
            c.translate(pos.x, pos.y, h+box2d.scalarWorldToPixels(0.1f));
            c.rotate(-chasis.getAngle());
            //c.fill(180, 120, 110);
            c.fill(color.getRed(), color.getGreen(), color.getBlue());
            c.strokeWeight(1.5f);
            c.stroke(30, 40, 30);
            c.box(l_pixels, w_pixels, h_pixels);
        c.popMatrix();
    }
}


package entities.car;

import static main.Main.*;
import static main.Game.*;
import static util.Constants.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.FrictionJoint;
import org.jbox2d.dynamics.joints.FrictionJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import processing.core.PGraphics;
import processing.core.PShape;
import static util.geometry.Shapes.createWheel;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public final class Axle implements Drawable {
    
    public final Vec2 prevPosL = new Vec2(0, 0), prevPosR = new Vec2(0, 0);
    
    protected final Body axle, chasis;
    protected final RevoluteJoint chasisConnector; 
    
    protected final FrictionJoint frictionJointA, frictionJointB;
    
    public final Vec2 wheelA, wheelB;
    
    final float spacing, spacing_pixels, radius, radius_pixels, thickness, thickness_pixels;
    
    private float slideSpeed = 0, forwardSpeed = 0;
    
    private final PShape wheelShapeA, wheelShapeB;
    
    public Axle(float spacing, float radius, float thickness, float theta, Body chasis, Vec2 chasisConnectionLocal, float maxTurn) {
        this.spacing = spacing;
        this.radius = radius;
        this.thickness = thickness;
        
        spacing_pixels = box2d.scalarWorldToPixels(spacing);
        radius_pixels = box2d.scalarWorldToPixels(radius);
        thickness_pixels = box2d.scalarWorldToPixels(thickness);
        
        wheelShapeA = createWheel(radius_pixels, thickness_pixels);
        wheelShapeB = createWheel(radius_pixels, thickness_pixels);
        
        this.chasis = chasis;
        
        wheelA = new Vec2(0, spacing/2);
        wheelB = new Vec2(0, -spacing/2);
        PolygonShape sd_a = new PolygonShape(), sd_b = new PolygonShape();
        sd_a.setAsBox(radius, thickness/2, wheelA, 0);
        sd_b.setAsBox(radius, thickness/2, wheelB, 0);
        
        FixtureDef fd_a = new FixtureDef(), fd_b = new FixtureDef();
        fd_a.shape = sd_a; fd_b.shape = sd_b;
        fd_a.density = 50/(PI*radius*radius*thickness); fd_b.density = 50/(PI*radius*radius*thickness);
        fd_a.friction = 1f; fd_b.friction = 1f;
        fd_a.restitution = 0.6f; fd_b.restitution = 0.6f;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(chasis.getWorldPoint(chasisConnectionLocal));
        bd.angle = theta;
        
        axle = box2d.createBody(bd);
        axle.createFixture(fd_a);
        axle.createFixture(fd_b);
        
        RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = chasis;
        rjd.bodyB = axle;
        rjd.enableLimit = true;
        rjd.referenceAngle = 0;
        rjd.lowerAngle = -maxTurn;
        rjd.upperAngle = maxTurn;
        rjd.localAnchorA = chasisConnectionLocal;
        rjd.localAnchorB = new Vec2(0, 0);
        rjd.enableMotor = false;
        
        chasisConnector = (RevoluteJoint)box2d.createJoint(rjd);
        
        FrictionJointDef fd = new FrictionJointDef();
        fd.bodyA = axle;
        fd.bodyB = asphalt.floor;
        fd.localAnchorA.set(wheelA);
        fd.localAnchorB.set(0, 0);
        fd.maxForce = 1000;
        fd.maxTorque = 500;
        
        frictionJointA = (FrictionJoint)box2d.createJoint(fd);
        
        fd.localAnchorA.set(wheelB);
        
        frictionJointB = (FrictionJoint)box2d.createJoint(fd);
    }
    
    public Axle(float spacing, float radius, float thickness, float theta, Body chasis, float chasisConnectionX, float chasisConnectionY, float maxTurn) {
        this(spacing, radius, thickness, theta, chasis, new Vec2(chasisConnectionX, chasisConnectionY), maxTurn);
    }
    
    protected void update(boolean brake) {
        drive();
        updateFriction(brake);
    }
    
    @Override
    public void render(PGraphics g) {
        //float angle = axle.getAngle();
        //float forwardSpeed = Vec2.dot(new Vec2(cos(angle), sin(angle)), axle.getLinearVelocity());
        float pixSpeed = box2d.scalarWorldToPixels(forwardSpeed)/FPS;
        float omega = pixSpeed/radius_pixels;
        g.pushMatrix();
            Vec2 pos = box2d.coordWorldToPixels(axle.getPosition());
            g.translate(pos.x, pos.y, radius_pixels);
            float theta = axle.getAngle();
            g.rotate(-theta);
            
            g.fill(50);
            g.strokeWeight(1.5f);
            g.stroke(20, 20, 40);
            g.box(thickness_pixels, spacing_pixels, thickness_pixels);
            
            g.translate(0, spacing_pixels/2);
            //g.box(2*radius_pixels, thickness_pixels, 2*radius_pixels);
            wheelShapeA.rotateY(omega);
            g.shape(wheelShapeA);
            
            g.translate(0, -spacing_pixels);
            //g.box(2*radius_pixels, thickness_pixels, 2*radius_pixels);
            wheelShapeB.rotateY(omega);
            g.shape(wheelShapeB);
        g.popMatrix();
    }
    
    private void drive() {
       
    }
    
    private void updateFriction(boolean brake) {
        
        float theta = axle.getAngle(), massTotal = axle.getMass() + chasis.getMass(), massOnAxle = axle.getMass() + chasis.getMass()*0.5f;
        
        Vec2 dir = new Vec2(cos(theta), sin(theta)),
             v = axle.getLinearVelocity();
        
        float vp = Vec2.dot(dir, v);
        
        forwardSpeed = vp;
        
        Vec2 vP = new Vec2(dir.x*vp, dir.y*vp),
             vL = new Vec2(v.x-vP.x, v.y-vP.y);
        
        float vl = vL.length(),
              fn = GRAVITY*massOnAxle,
              sf = RUBBER_ASPHALT_SF*fn,
              kf = RUBBER_ASPHALT_KF*fn;
        
        slideSpeed = vl;
        
        if(massTotal*vl < sf*TIMESTEP) {
            axle.applyForceToCenter(vL.mul(-massTotal*FPS));
            //axle.(vL.mul(-massTotal), new Vec2(0, 0), true);
        } else {
            axle.applyForceToCenter(vL.mul(-kf/vl));
        }
        
        if(brake) {
            if(massTotal*vp < sf*0.25f*TIMESTEP) {
                axle.applyForceToCenter(vP.mul(-massTotal*FPS));
            //axle.(vL.mul(-massTotal), new Vec2(0, 0), true);
            } else {
                axle.applyForceToCenter(vP.mul(-0.25f*kf/vp));
            }
        }
    }
    
    protected float getForwardSpeed() {
        return forwardSpeed;
    }
    
    protected float getSlideSpeed() {
        return slideSpeed;
    }
    
    protected void updateTrackMarks(PGraphics g, boolean brake) {
        g.pushStyle();
            g.strokeWeight(1.5f);
            //g.stroke(110, 90, 140, 35);
            //g.stroke(110, 90, 140, 235);
            g.stroke(45, 38, 45, 60);
            float angle = axle.getAngle(), sin = sin(angle), cos = cos(angle);
            Vec2 pos = axle.getPosition(), 
                 posR = pos.add(new Vec2(wheelA.x*cos - wheelA.y*sin, wheelA.x*sin + wheelA.y*cos)), 
                 posL = pos.add(new Vec2(wheelB.x*cos - wheelB.y*sin, wheelB.x*sin + wheelB.y*cos)),
                 pixPosR = box2d.coordWorldToPixels(posR),
                 pixPosL = box2d.coordWorldToPixels(posL);
            
            if(!(slideSpeed < 5.52f || prevPosR.sub(pixPosR).length() > 8 || prevPosL.sub(pixPosL).length() > 8) || (brake && chasis.getLinearVelocity().length() > 55)) {
                float camTransX = 0/*getCameraTranslationX()*/, camTransY = 0;//getCameraTranslationY();
                g.line(prevPosR.x-camTransX, prevPosR.y-camTransY, pixPosR.x-camTransX, pixPosR.y-camTransY);
                g.line(prevPosL.x-camTransX, prevPosL.y-camTransY, pixPosL.x-camTransX, pixPosL.y-camTransY);
            }
            
            prevPosR.set(pixPosR);
            prevPosL.set(pixPosL);
        g.popStyle();
    }
}

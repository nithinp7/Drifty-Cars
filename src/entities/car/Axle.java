
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

/**
 *
 * @author Nithin
 */
public final class Axle {
    
    public final Body axle, chasis;
    public final RevoluteJoint chasisConnector; 
    public final FrictionJoint frictionJointA, frictionJointB;
    
    public final Vec2 wheelA, wheelB;
    
    final float spacing, spacing_pixels, radius, radius_pixels, thickness, thickness_pixels;
    
    public Axle(float spacing, float radius, float thickness, float theta, Body chasis, Vec2 chasisConnectionLocal, float maxTurn) {
        this.spacing = spacing;
        this.radius = radius;
        this.thickness = thickness;
        
        spacing_pixels = box2d.scalarWorldToPixels(spacing);
        radius_pixels = box2d.scalarWorldToPixels(radius);
        thickness_pixels = box2d.scalarWorldToPixels(thickness);
        
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
    
    protected void update() {
        drive();
        updateFriction();
    }
    
    protected void render() {
        c.pushMatrix();
            Vec2 pos = box2d.coordWorldToPixels(axle.getPosition());
            c.translate(pos.x, pos.y);
            float theta = axle.getAngle();
            c.rotate(-theta);
            
            c.fill(50);
            c.strokeWeight(1.5f);
            c.stroke(20, 20, 40);
            c.box(thickness_pixels, spacing_pixels, thickness_pixels);
            
            c.translate(0, spacing_pixels/2);
            c.box(2*radius_pixels, thickness_pixels, 2*radius_pixels);
            
            c.translate(0, -spacing_pixels);
            c.box(2*radius_pixels, thickness_pixels, 2*radius_pixels);
        c.popMatrix();
    }
    
    private void drive() {
       
    }
    
    private void updateFriction() {
        
        float theta = axle.getAngle(), massTotal = axle.getMass() + chasis.getMass(), massOnAxle = axle.getMass() + chasis.getMass()*0.5f;
        
        Vec2 dir = new Vec2(cos(theta), sin(theta)),
             v = axle.getLinearVelocity();
        
        float vp = Vec2.dot(dir, v);
        
        Vec2 vP = new Vec2(dir.x*vp, dir.y*vp),
             vL = new Vec2(v.x-vP.x, v.y-vP.y);
        
        float vl = vL.length(),
              fn = GRAVITY*massOnAxle,
              sf = RUBBER_ASPHALT_SF*fn,
              kf = RUBBER_ASPHALT_KF*fn;
        
        if(massTotal*vl < sf*TIMESTEP) {
            axle.applyForceToCenter(vL.mul(-massTotal*FPS));
            //axle.(vL.mul(-massTotal), new Vec2(0, 0), true);
        } else {
            axle.applyForceToCenter(vL.mul(-kf/vl));
        }
    }
}

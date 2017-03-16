
package ai;

import static main.Main.c;
import static main.Game.box2d;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;

/**
 *
 * @author nithin
 */
public final class FrontObstacleDetector implements RayCastCallback {
    
    private static final float 
            sideAngle = PI/2.0f,
            shortAngle = PI/6.5f,
            midAngle = PI/16.0f,
            farAngle = PI/35.0f,
            
            sideLength = 14,
            shortLength = 14,
            midLength = 65,
            farLength = 85,
            frontLength = 65;
    
    private static final float[] relAngles = { sideAngle, shortAngle, midAngle, farAngle, 0, -farAngle, -midAngle, -shortAngle, -sideAngle },
                                 lengths = { sideLength, shortLength, midLength, farLength, frontLength, farLength, midLength, shortLength, sideLength },
                                 weights = { 0.25f, 0.4f, 0.5f, 0.6f, 0.7f, -0.6f, -0.5f, -0.4f, -0.25f };
    
    private float angle = 0;
    private final Vec2 pos = new Vec2();
    private final Vec2[] rays;
    private final float[] rayHitFractions = new float[] { -1, -1, -1, -1, -1, -1, -1, -1, -1 };
            
    private final Body parentBody;
    
    private float targetSteerAngle = 0;
    
    public FrontObstacleDetector(Body parentBody) {
        this.parentBody = parentBody;
        
        rays = new Vec2[9];
        for(int i=0; i<9; i++) rays[i] = new Vec2();
        
        update();
    }
    
    public void clear() {
        for(int i=0; i<9; i++) rayHitFractions[i] = -1;
    }
    
    public void setTargetSteerAngle(float a) {
        targetSteerAngle += (a-targetSteerAngle)*0.5f;
    }
    
    public void update() {
        
        pos.set(parentBody.getPosition());
        angle = parentBody.getAngle() + targetSteerAngle;
        
        for(int i=0; i<9; i++) {
            float len = lengths[i], ang = angle+relAngles[i];
            Vec2 ray = rays[i];
            ray.set(len*cos(ang), len*sin(ang));
            raycast(pos, ray.add(pos));
        }
    }
    
    private void raycast(Vec2 p1, Vec2 p2) {
        box2d.world.raycast(this, p1, p2);//copy(p1), copy(p2));
    }

    @Override
    public float reportFixture(Fixture fxtr, Vec2 point, Vec2 norm, float frac) {
        Vec2 dif = point.sub(pos);
        float dist = dif.length();
        
        if(dist < 3) return -1;
        
        dif.normalize();

        float cos = cos(angle), sin = -sin(angle);
        
        float relRayAngle = atan2(dif.y*cos + dif.x*sin, dif.x*cos - dif.y*sin);
        
        int index = -1;
        float smallestAngleDif = 2*PI;//abs(shortAngle-relRayAngle);
        
        for(int i=0; i<9; i++) {
            float relAngle = relAngles[i], angleDif = abs(relRayAngle-relAngle);
            if(angleDif < smallestAngleDif) {
                smallestAngleDif = angleDif;
                index = i;
            }
        }
        
        rayHitFractions[index] = frac;
        
        return 0;
    }
    
    public void render() {
        Vec2 pixPos = box2d.coordWorldToPixels(pos);
        
        c.pushStyle();
            c.strokeWeight(2.5f);
            
            for(int i=0; i<9; i++) {
                Vec2 ray = rays[i], pixRay = box2d.vectorWorldToPixels(ray);
                boolean rayBlocked = rayHitFractions[i] != -1;
                if(rayBlocked) c.stroke(255, 100, 100);
                else c.stroke(100, 240, 50);
                
                c.line(pixPos.x, pixPos.y, pixPos.x+pixRay.x, pixPos.y+pixRay.y);
            }
        c.popStyle();
    }
    
    public float getRecommendedDeviation() {
        float res = 0;
        for(int i=0; i<9; i++) {
            float frac = rayHitFractions[i];
            res += frac != -1? -weights[i]*lengths[i]*(1-frac) : 0;
        }
        return res;
    }
    
    public float getRecommendedThrottle() {
        float res = 0;
        int hits = 0;
        for(int i=0; i<9; i++) {
            float frac = rayHitFractions[i];
            if(frac != -1) {
                hits++;
                res += frac;
            }
        }
        return hits > 0? res/hits : 1;
    }
}


package entities.car;

import ai.FrontObstacleDetector;
import ai.PID;
import ai.Path.PathSegment;
import java.util.Optional;
import static main.Game.*;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.*;
import processing.core.PGraphics;

/**
 *
 * @author Nithin
 */
public abstract class AI_Car extends Car {
    
    public PathSegment currentSegment = null;
    
    private final PID steeringControl;
    
    private float targetSteerAngle = 0;
    
    private int lowSpeedCounter = 0;
    private int reverseCounter = 0;
    
    private final FrontObstacleDetector frontObstacleDetector;
    
    public AI_Car(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h);
        this.steeringControl = steeringControl;
        throttle = 0.6f;
        
        frontObstacleDetector = new FrontObstacleDetector(chasis);
    }
    
    public void checkFront() {
        frontObstacleDetector.update();
    }
    
    @Override 
    public void update() {
        
        Vec2 pos = chasis.getPosition();
        
        if(getDistanceToCameraTarget(chasis.getPosition()) > 300) {
            dispose();
            return;
        }
        
        Vec2 vel = chasis.getLinearVelocity();
        float speed = vel.length();
        
        if(reverse) {
            reverseCounter++;
            if(reverseCounter >= 80) {
                reverseCounter = 0;
                lowSpeedCounter = 0;
                reverse = false;
            }
        } else {
            if(speed < 1) lowSpeedCounter++;
            else lowSpeedCounter = 0;
            
            if(lowSpeedCounter >= 155) {
                reverseCounter = 0;
                reverse = true;
            }
        }
        
        
        float angle = frontAxle.axle.getAngle();
        float steerAngle = angle-chasis.getAngle();
        
        Vec2 carDir = new Vec2(cos(angle), sin(angle));
        
        
        //turn = steeringControl.update(segment.getPerpendicularDeviation(pos), segment.getAngularDeviation(angle));
        if(currentSegment != null) {   
            
            float recommendedDeviation = 4*frontObstacleDetector.getRecommendedDeviation(),
                  recommendedThrottle = 0.6f*frontObstacleDetector.getRecommendedThrottle();
            
            float propDev = currentSegment.getPerpendicularDeviation(pos, carDir) - recommendedDeviation,
                  angDev = currentSegment.getAngularDeviation(carDir);
            targetSteerAngle = constrain(steeringControl.update(propDev, angDev),
                                         frontAxle.chasisConnector.getLowerLimit(), 
                                         frontAxle.chasisConnector.getUpperLimit());//
                                         //+ recommendedDeviation;
                                
            turn = constrain(targetSteerAngle - steerAngle, -1, 1);
            frontObstacleDetector.setTargetSteerAngle(targetSteerAngle);
            //throttle = !reverse && abs(recommendedDeviation) > 0 ? map(abs(recommendedDeviation), 0, 0.7f, 0.25f, 0.6f) : 0.6f;
            //throttle = !reverse ? constrain(1.4f*recommendedThrottle*recommendedThrottle+0.3f-getSlideSpeed()*0.5f, 0, 1f) : 1f;
            throttle = getThrottle(recommendedThrottle);

            //throttle = !reverse ? constrain(1.4f*recommendedThrottle-getSlideSpeed()*0.2f-0.03f*getForwardSpeed(), 0, 1f) : 1f;

        } else {
            turn = 0;
            throttle = 0;
        }
        
        super.update();
    }
    
    protected abstract float getThrottle(float recommendedThrottle);
    
    @Override
    public void render(PGraphics g) {
        super.render(g);
        //frontObstacleDetector.render();
        frontObstacleDetector.clear();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        path.removeTemporarySegment(currentSegment);
    }
}


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
public final class AI_Car extends Car {
    
    private final PID steeringControl, obstaclePID;
    private PathSegment currentSegment = null;
    
    private float targetSteerAngle = 0;
    
    private int lowSpeedCounter = 0;
    private int reverseCounter = 0;
    
    private final FrontObstacleDetector frontObstacleDetector;
    
    public AI_Car(float x, float y, float theta, float l, float w, float h, PID steeringControl, PID obstaclePID) {
        super(x, y, theta, l, w, h);
        this.steeringControl = steeringControl;
        throttle = 0.6f;
        
        frontObstacleDetector = new FrontObstacleDetector(chasis);
        this.obstaclePID = obstaclePID;// = new PID(0.1f, 0.1f, 0f);
    }
    
    public void checkFront() {
        frontObstacleDetector.update();
    }
    
    @Override 
    public void update() {
        
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
        
        Vec2 pos = chasis.getPosition();
        float angle = frontAxle.axle.getAngle();
        float steerAngle = angle-chasis.getAngle();
        
        Vec2 carDir = new Vec2(cos(angle), sin(angle));
        
        path.removeTemporarySegment(currentSegment);
        
        Optional<PathSegment> seg = path.getClosestSegment(pos, 6);
        
        if(seg.isPresent()) {
            PathSegment segment = seg.get();
            //if(segment != currentSegment) steeringControl.clearIntegralAccumulation();
            currentSegment = segment;
        } else {
            currentSegment = path.createTemporarySegment(pos);
        }
        
        //turn = steeringControl.update(segment.getPerpendicularDeviation(pos), segment.getAngularDeviation(angle));
        if(currentSegment != null) {   
            
            float recommendedDeviation = frontObstacleDetector.getRecommendedDeviation(),//obstaclePID.update(0.5f*frontObstacleDetector.getRecommendedDeviation()),
                  recommendedThrottle = frontObstacleDetector.getRecommendedThrottle();
            
            float propDev = currentSegment.getPerpendicularDeviation(pos, carDir) - recommendedDeviation,
                  angDev = currentSegment.getAngularDeviation(carDir);
            targetSteerAngle = constrain(steeringControl.update(propDev, angDev),
                                         frontAxle.chasisConnector.getLowerLimit(), 
                                         frontAxle.chasisConnector.getUpperLimit());//
                                         //+ recommendedDeviation;
                                
            turn = constrain(targetSteerAngle - steerAngle, -1, 1);
            frontObstacleDetector.setTargetSteerAngle(targetSteerAngle);
            //throttle = !reverse && abs(recommendedDeviation) > 0 ? map(abs(recommendedDeviation), 0, 0.7f, 0.25f, 0.6f) : 0.6f;
            throttle = !reverse ? constrain(0.4f*recommendedThrottle*recommendedThrottle+0.3f-getSlideSpeed()*0.5f, 0, 0.7f) : 0.7f;
            //throttle = 0.5f;
        } else {
            turn = 0;
            throttle = 0;
        }
        
        super.update();
    }
    
    @Override
    public void render(PGraphics g) {
        super.render(g);
        //frontObstacleDetector.render();
        frontObstacleDetector.clear();
    }
}

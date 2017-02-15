
package entities.car;

import ai.PID;
import ai.Path.PathSegment;
import java.util.Optional;
import static main.Game.path;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.*;

/**
 *
 * @author Nithin
 */
public final class AI_Car extends Car {
    
    private final PID steeringControl;
    private PathSegment currentSegment = null;
    
    private float targetSteerAngle = 0;
    
    public AI_Car(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h);
        this.steeringControl = steeringControl;
        throttle = 0.6f;
    }
    
    @Override 
    public void update() {
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
            float propDev = currentSegment.getPerpendicularDeviation(pos, carDir),
                  angDev = currentSegment.getAngularDeviation(carDir);
            targetSteerAngle = constrain(steeringControl.update(propDev, angDev),
                                         frontAxle.chasisConnector.getLowerLimit(), 
                                         frontAxle.chasisConnector.getUpperLimit());

            turn = constrain(targetSteerAngle - steerAngle, -1, 1);
            throttle = 0.3f;
        } else {
            turn = 0;
            throttle = 0;
        }
        
        super.update();
    }
}

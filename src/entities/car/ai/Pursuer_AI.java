
package entities.car.ai;

import ai.PID;
import ai.Path;
import ai.Path.PathNode;
import entities.car.AI_Car;
import java.util.Optional;
import static main.Game.getCameraTarget;
import static main.Game.path;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.constrain;

/**
 *
 * @author nithin
 */
public final class Pursuer_AI extends AI_Car {

    public Pursuer_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h, steeringControl);
    }

    @Override
    protected float getThrottle(float recommendedThrottle) {
        return !reverse ? constrain(1.4f*recommendedThrottle*recommendedThrottle+0.3f-getSlideSpeed()*0.5f, 0, 1f) : 1f;
    }
    
    @Override
    public void update() {
        Vec2 pos = chasis.getPosition();
        
        path.removeTemporarySegment(currentSegment);
        
        Optional<Path.PathSegment> seg = path.getClosestSegment(pos, 6);
        
        if(seg.isPresent()) {
            Path.PathSegment segment = seg.get();
            //if(segment != currentSegment) steeringControl.clearIntegralAccumulation();
            currentSegment = segment;
        } else {
            currentSegment = path.createTemporarySegment(pos, getCameraTarget());//path.createTemporarySegment(pos);
        }
        super.update();
    }
}

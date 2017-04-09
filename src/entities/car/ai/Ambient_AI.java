
package entities.car.ai;

import ai.PID;
import entities.car.AI_Car;
import static java.lang.Math.random;
import java.util.List;
import java.util.stream.Collectors;
import static main.Game.map;
import static main.Game.path;
import org.jbox2d.common.Vec2;
import procGen.MapGen.MapCoord;
import static procGen.MapGen.TYPE_ROAD;
import static processing.core.PApplet.*;

/**
 *
 * @author nithin
 */
public final class Ambient_AI extends AI_Car {
    
    private MapCoord current = null, target = null;
    
    public Ambient_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h, steeringControl);
    }
    
    @Override
    protected float getThrottle(float recommendedThrottle) {
        return !reverse ? constrain(1.4f*recommendedThrottle+0.3f-getSlideSpeed()*0.5f-getForwardSpeed()*0.03f, 0, 1f) : 1f;
    }
    
    @Override
    public void update() {
        Vec2 pos = chasis.getPosition();
        
        path.removeTemporarySegment(currentSegment);
        
        MapCoord closest = map.getClosestMapCoordOfType(pos, TYPE_ROAD);
        
        if(closest != null) {
            if(closest.isSame(target)) {
                List<MapCoord> possible = closest.
                        getAdjacentCoords().
                        stream().
                        filter(coord -> !coord.isSame(current) && 
                                         coord.getTileType()==TYPE_ROAD).
                        collect(Collectors.toList());
                if(!possible.isEmpty()) {
                    MapCoord mc = possible.get(round((float)random()*possible.size()));
                    current = target;
                    target = mc;
                } else {
                    current = null;
                    target = null;
                }
            }
        }
        
        if(current!=null && target!=null) {
            currentSegment = path.createRawSegment(current.getNode(), target.getNode(), true);
        }
    }
}


package entities.car.ai;

import ai.FrontObstacleDetector;
import ai.PID;
import entities.car.AI_Car;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static main.Game.*;
import org.jbox2d.common.Vec2;
import procGen.MapGen.MapCoord;
import static procGen.MapGen.TYPE_ROAD;
import static processing.core.PApplet.*;
import static util.Constants.MODEL_FUEL_TRUCK;
import static util.Constants.MODEL_RAND_CAR;

/**
 *
 * @author nithin
 */
public final class Ambient_AI extends AI_Car {
    
    private MapCoord current = null, target = null;
    
    public Ambient_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl, int modelType) {
        super(x, y, theta, l, w, h, steeringControl, 0, modelType);
        //frontObstacleDetector.addCheck(ac);
        if(modelType==MODEL_FUEL_TRUCK) setImpactResistance(0.0025f);
        frontObstacleDetector.setScale(0.3f);
        setDeleteDistance(300);
    }
    
    public Ambient_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        this(x, y, theta, l, w, h, steeringControl, MODEL_RAND_CAR);
    }
    
    @Override
    protected float getThrottle(float recommendedThrottle) {
        return !reverse ? constrain(1.4f*recommendedThrottle+0.7f-getSlideSpeed()*0.5f-getForwardSpeed()*0.1f, 0, 1f) : 1f;
    }
    
    @Override
    public void update() {
        Vec2 pos = chasis.getPosition();
        if(target!=null) target.fix();
        if(current!=null) current.fix();
        MapCoord closest = map.getClosestMapCoordOfType(pos, TYPE_ROAD);
        
        if(closest != null) {
            if(current==null || target==null || current.distanceTo(pos) > 150) {
                current = closest;
                ArrayList<MapCoord> adj = closest.getAdjacentCoords();
                if(!adj.isEmpty()) {
                    List<MapCoord> possible = 
                            adj
                            .stream()
                            .filter(coord -> coord.getTileType()==TYPE_ROAD)
                            .collect(Collectors.toList());

                    target = possible.isEmpty()? null : possible.get((int)(random()*possible.size()));
                }
            }

            if(target!=null && closest.isSame(target)) {
                ArrayList<MapCoord> adj = closest.getAdjacentCoords();
                if(!adj.isEmpty()) {
                    List<MapCoord> possible =
                            adj
                            .stream()
                            .filter(coord -> !coord.isSame(current) && coord.getTileType()==TYPE_ROAD)
                            .collect(Collectors.toList());
                    
                    current = closest;
                    target = possible.isEmpty()? null : possible.get((int)(random()*possible.size()));
                } else {
                    //target = current;
                    //current = closest;
                }
            }
        }
        
        path.removeTemporarySegment(currentSegment);
        if(current!=null && target!=null) currentSegment = path.createTemporarySegment(pos, target.getPosition(), true);
        
        super.update();
    }
}

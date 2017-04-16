
package entities.spawners;

import ai.PID;
import entities.car.AI_Car;
import entities.car.ai.Pursuer_AI;
import static main.Game.getCameraTarget;
import static main.Game.getTimeElapsed;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.atan2;
import static processing.core.PApplet.constrain;

/**
 *
 * @author nithin
 */
public class PursuerSpawner extends Spawner {

    public PursuerSpawner() {
        super(55, 90, 8);
    }
    
    @Override
    protected AI_Car createCar(Vec2 pos) {
        Vec2 targ = getCameraTarget();
        return new Pursuer_AI(pos.x, pos.y, atan2(targ.y-pos.y, targ.x-pos.x), 3.5f, 1.23f, 0.67f, new PID(-0.2f, -1.4f, 0f));
    }
    
    @Override
    protected void updateSubClass() {
        setTargetNumberOfCars(constrain(8+(int)(getTimeElapsed()/30), 0, 20));
    }
}

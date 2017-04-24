
package entities.spawners;

import ai.PID;
import entities.car.AI_Car;
import entities.car.ai.Pursuer_AI;
import static main.Game.getCameraTarget;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.atan2;
import static processing.core.PApplet.constrain;
import static util.Constants.FPS;

/**
 *
 * @author nithin
 */
public class PursuerSpawner extends Spawner {
    
    private int counter = 0;

    public PursuerSpawner() {
        super(55, 90, 0);
    }
    
    @Override
    protected AI_Car createCar(Vec2 pos) {
        Vec2 targ = getCameraTarget();
        return new Pursuer_AI(pos.x, pos.y, atan2(targ.y-pos.y, targ.x-pos.x), 3.5f, 1.23f, 0.67f, new PID(-1.2f, -1.8f, 0f));
    }
    
    @Override
    protected void updateSubClass() {
        setTargetNumberOfCars(constrain(2+3*(int)(counter++/10.0f/FPS), 0, 12));
    }
    
    @Override
    public void restart() {
        counter = 0;
        super.restart();
    }
}

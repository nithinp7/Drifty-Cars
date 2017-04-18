
package entities.spawners;

import ai.PID;
import entities.car.AI_Car;
import entities.car.ai.Ambient_AI;
import org.jbox2d.common.Vec2;

/**
 *
 * @author nithin
 */
public class AmbientCarSpawner extends Spawner {

    public AmbientCarSpawner() {
        super(35, 300, 20);
    }

    @Override
    protected AI_Car createCar(Vec2 pos) {
        return new Ambient_AI(pos.x, pos.y, 0, 2.8f, 0.7f, 0.4f, new PID(-0.1f, -1.3f, 0f));
    }
}

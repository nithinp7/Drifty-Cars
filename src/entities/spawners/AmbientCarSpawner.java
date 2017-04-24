
package entities.spawners;

import ai.PID;
import entities.car.AI_Car;
import entities.car.ai.Ambient_AI;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import static util.Constants.MODEL_FUEL_TRUCK;

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
        int modelType = (int)c.random(2, 7);
        boolean isTruck = modelType==MODEL_FUEL_TRUCK;
        return new Ambient_AI(pos.x, pos.y, 0, (isTruck?1.4f:1)*2.8f, (isTruck?1.5f:1)*0.7f, (isTruck?1.2f:1)*0.4f, new PID(-0.1f, -1.3f, 0f), modelType);
    }
}

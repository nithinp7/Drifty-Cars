
package entities.spawners;

import entities.car.AI_Car;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import static main.Game.*;
import org.jbox2d.common.Vec2;
import procGen.MapGen.MapCoord;
import static procGen.MapGen.TYPE_EMPTY;
import static procGen.MapGen.TYPE_ROAD;
import static processing.core.PApplet.map;
import util.interfaces.Restartable;

/**
 *
 * @author nithin
 */
public abstract class Spawner implements Restartable {
    
    private final float minRadius, maxRadius;
    private int targetNumberOfCars;
    
    private final Random r;
    
    private final ArrayList<AI_Car> spawnedCars = new ArrayList<>();
    
    public Spawner(float minRadius, float maxRadius, int targetNumberOfCars) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        
        this.targetNumberOfCars = targetNumberOfCars;
        
        r = new Random();
    }
    
    public final void update() {
        
        updateSubClass();
        
        spawnedCars.removeIf(AI_Car::isDead);
        int carsLeft = spawnedCars.size();
        Vec2 target = getCameraTarget();
        
        ArrayList<MapCoord> usedCoords = new ArrayList<>();
        
        spawn_loop: for(int i=0; i<targetNumberOfCars-carsLeft; i++) {
            Vec2 relCoord = new Vec2(map(r.nextFloat(), 0, 1, minRadius, maxRadius)*(r.nextBoolean()?1:-1),
                                     map(r.nextFloat(), 0, 1, minRadius, maxRadius)*(r.nextBoolean()?1:-1));
            
            MapCoord coord = map.getClosestMapCoordOfType(target.add(relCoord), TYPE_ROAD);
            if(coord==null) continue;
            MapCoord temp = coord;
            if(coord.getTileType()!=TYPE_ROAD || usedCoords.stream().anyMatch(c -> temp.isSame(c))) 
                for(int j=0;;j++) {
                    List<MapCoord> adj = coord.getAdjacentCoords()
                            .stream()
                            .filter(c -> c.getTileType()==TYPE_ROAD || c.getTileType()==TYPE_EMPTY)
                            .collect(Collectors.toList());
                    if(adj.isEmpty() || j>3) {
                        //i--;
                        continue spawn_loop;
                    }
                    coord = adj.get(r.nextInt(adj.size()));
                    MapCoord temp2 = coord;
                    if(usedCoords.stream().noneMatch(c -> temp2.isSame(c))) break;
                }
            usedCoords.add(coord);
            AI_Car car = createCar(coord.getPosition());
            aiCars.add(car);
            spawnedCars.add(car);
            cars.add(car);
        }
    }
    
    protected void updateSubClass() {}
    
    protected abstract AI_Car createCar(Vec2 pos);

    public void setTargetNumberOfCars(int targetNumberOfCars) {
        this.targetNumberOfCars = targetNumberOfCars;
    }
    
    @Override
    public void restart() {
        spawnedCars.forEach(AI_Car::dispose);
        spawnedCars.clear();
    }
}

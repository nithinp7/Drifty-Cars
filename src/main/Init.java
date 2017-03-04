
package main;

import entities.building.Building;
import entities.car.UserCar;
import entities.surface.Asphalt;
import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import static processing.core.PConstants.PI;
import shiffman.box2d.Box2DProcessing;
import ai.Path;
import entities.car.Car;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class Init {
    
    public static void initAll() {
        initTextures();
        initPhysics();
        initAI();
        initMap();
        initVehicles();
        initBuildings();
    }
    
    private static void initAI() {
        path = new Path();
    }
    
    private static void initPhysics() {
        box2d = new Box2DProcessing(c);
        box2d.setScaleFactor(2);
        box2d.createWorld(new Vec2(0, 0));
    }
    
    private static void initMap() {
        asphalt = new Asphalt(new Vec2(-150, -150), new Vec2(300, 300), new Vec2(20, 20));
    }
    
    private static void initVehicles() {
        Car car = new UserCar(0, 0, -PI/4, 6, 1.8f, 1.2f);
        cars.add(car);
        cameraTarget = car.chasis;
    }
    
    private static void initBuildings() {
        buildings.add(new Building(0, 150, 0, 280, 20, 10));
        buildings.add(new Building(0, -150, 0, 280, 20, 10));
        buildings.add(new Building(-150, 0, 0, 20, 300, 10));
        buildings.add(new Building(150, 0, 0, 20, 300, 10));
    }
}

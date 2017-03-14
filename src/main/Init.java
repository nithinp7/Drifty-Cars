
package main;

import entities.building.Block;
import entities.car.UserCar;
import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import static processing.core.PConstants.PI;
import shiffman.box2d.Box2DProcessing;
import ai.Path;
import beads.AudioContext;
import beads.Gain;
import entities.car.Car;
import entities.surface.Floor;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class Init {
    
    public static void initAll() {
        initSounds();
        initTextures();
        initSkyboxes();
        initPhysics();
        initAI();
        initMap();
        initVehicles();
        //initBuildings();
    }
    
    private static void initSounds() {
        
        ac = new AudioContext();
        gain = new Gain(ac, 2, 0.2f);
        ac.out.addInput(gain);
        ac.start();
        
        initSamples();
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
        floor = new Floor(new Vec2(-150, -150), new Vec2(300, 300), new Vec2(20, 20));
    }
    
    private static void initVehicles() {
        Car car = new UserCar(0, 0, -PI/4, 6, 1.8f, 1.2f);
        cars.add(car);
        cameraTarget = car.chasis;
    }
    
    private static void initBuildings() {
        
        blocks.add(new Block(0, 280, 0, 660, 20, 25, true));
        blocks.add(new Block(0, -280, 0, 660, 20, 25, true));
        blocks.add(new Block(-340, 0, 0, 20, 620, 25, true));
        blocks.add(new Block(340, 0, 0, 20, 620, 25, true));
    }
}

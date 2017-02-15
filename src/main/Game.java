
package main;

import ai.PID;
import ai.Path;
import entities.building.Building;
import entities.car.*;
import entities.surface.Asphalt;
import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import static main.Main.*;
import static util.Constants.*;
import static main.Init.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import shiffman.box2d.*;
import static util.input.Input.consumeInput;

/**
 *
 * @author Nithin
 */
public final class Game {
    
    public static Box2DProcessing box2d;
    public static Path path;
    
    public static Asphalt asphalt;
    
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<Building> buildings = new ArrayList<>();
    
    protected static Body cameraTarget;
    private static final Vec2 cameraTranslation = new Vec2(0, 0);
    
    private static boolean tiltCamera = false;
    private static int cameraZ = 0;
    
    protected static void init() {
        initPhysics();
        initAI();
        initMap();
        initVehicles();
        initBuildings();
    }
    
    protected static void tick() {
        
        updatePathDebug();
        updateAI_Debug();
        updateCamera();
        cars.forEach(car -> car.update());
        box2d.step(TIMESTEP, 8, 3);
    }
        
    protected static void render() {
        c.background(120, 160, 180);
        c.ambientLight(100, 100, 100);
        c.directionalLight(255, 255, 255, -1, 1, -1);
        cameraZ = constrain(cameraZ, 20, 1000);
        c.translate(-cameraTranslation.x, -cameraTranslation.y, cameraZ);
        if(tiltCamera) {
            c.translate(0, HEIGHT/2);
            c.rotateX(PI/4);
            c.translate(0, -HEIGHT/2);
        }
        cars.forEach(car -> car.render());
        buildings.forEach(b -> b.render());
        c.fill(0);
        path.render();
    }
    
    private static void updatePathDebug() {
        if(consumeInput(VK_N)) {
            Vec2 pos = coordPixelsToWorld(c.mouseX, c.mouseY);
            path.addNode(pos.x, pos.y, consumeInput(VK_SPACE));
        }
        if(consumeInput(VK_C)) {
            path.clear();
        }
        if(consumeInput(VK_S)) {
            path.savePath(AI_PATH_URL);
        }
        if(consumeInput(VK_L)) {
            path.initFromJSON(AI_PATH_URL);
        }
    }
    
    private static void updateAI_Debug() {
        if(consumeInput(VK_M)) {
            Vec2 pos = coordPixelsToWorld(c.mouseX, c.mouseY);
            cars.add(new AI_Car(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 6, 1.8f, 1.2f, new PID(-0.1f, -1.3f, -0f)));
        }
    }
    
    private static void updateCamera() {
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition());
        cameraTranslation.set(pos.x-WIDTH/2, pos.y-HEIGHT/2);
        if(consumeInput(VK_Z)) cameraZ += 40;
        if(consumeInput(VK_X)) cameraZ -= 40;
        if(consumeInput(VK_T)) tiltCamera = !tiltCamera;
    }
    
    public static Vec2 coordPixelsToWorld(Vec2 pixels) {
        return box2d.coordPixelsToWorld(pixels.add(cameraTranslation));
    }
    
    public static Vec2 coordPixelsToWorld(float pixelX, float pixelY) {
        return coordPixelsToWorld(new Vec2(pixelX, pixelY));
    }
}

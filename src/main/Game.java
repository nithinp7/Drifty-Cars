
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
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import processing.core.PGraphics;
import processing.core.PImage;
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
    
    private static PGraphics trackMarksLayer;
    
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<AI_Car> aiCars = new ArrayList<>();
    public static ArrayList<Building> buildings = new ArrayList<>();
    
    protected static Body cameraTarget;
    private static final Vec2 cameraTranslation = new Vec2(0, 0);
    private static float cameraAngle = 0;
    
    private static boolean tiltCamera = false;
    private static int cameraZ = 0;
    
    private static PGraphics g;
    
    protected static void init() {
        g = c.g;
        trackMarksLayer = c.createGraphics(WIDTH, HEIGHT);
        initAll();
    }
    
    protected static void tick() {
        
        for(AI_Car ai : aiCars) ai.checkFront();
        updatePathDebug();
        updateAI_Debug();
        updateCamera();
        cars.forEach(car -> car.update());
        box2d.step(TIMESTEP, 8, 3);
    }
        
    protected static void render() {
        
        trackMarksLayer.beginDraw();
        
        transformCamera(g);

        asphalt.render();
        cars.forEach(car -> car.render(g));

        cars.forEach(car -> car.updateTrackMarks(trackMarksLayer));

        buildings.forEach(b -> b.render(g));
        c.fill(0);
        path.render();

        c.stroke(0);

        c.image(trackMarksLayer, 0, 0, WIDTH, HEIGHT);
        
        trackMarksLayer.endDraw();
    }
    
    private static void transformCamera(PGraphics g) {
        g.frustum(-5, 5, -5, 5, 3, 500);

        cameraZ = constrain(cameraZ, 300, 1000);
        
        g.translate(WIDTH/2, HEIGHT/2, cameraZ);
        
        if(tiltCamera) c.rotateX(PI/3f);
        g.rotateZ(cameraAngle);
        g.translate(-WIDTH/2, -HEIGHT/2);
        g.translate(-cameraTranslation.x, -cameraTranslation.y);
        
        g.background(120, 160, 180);
        g.ambientLight(100, 100, 100);
        g.directionalLight(255, 255, 255, -1, 1, -1);
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
            //path.savePath(AI_PATH_URL);
        }
        if(consumeInput(VK_L)) {
            path.initFromJSON(AI_PATH_URL);
        }
    }
    
    private static void updateAI_Debug() { 
        Vec2 pos = coordPixelsToWorld(c.mouseX, c.mouseY);
        if(consumeInput(VK_M)) {
            AI_Car aiCar = new AI_Car(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 6, 1.8f, 1.2f, new PID(-0.1f, -1.3f, 0f), new PID(0.4f, 0f, 0f));
            aiCars.add(aiCar);
            cars.add(aiCar);
        }
        
        if(consumeInput(VK_K)) {
            Building b = new Building(pos.x, pos.y, 0, 10, 10, 10);
            buildings.add(b);
        }
    }
    
    private static void updateCamera() {
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition());
        cameraAngle += (cameraTarget.getAngle()-PI/2 - cameraAngle)/20.;
        cameraTranslation.set(pos.x-WIDTH/2, pos.y-HEIGHT/2);
        if(consumeInput(VK_Z)) cameraZ += 20;
        if(consumeInput(VK_X)) cameraZ -= 20;
        if(consumeInput(VK_T)) tiltCamera = !tiltCamera;
    }
    
    public static Vec2 coordPixelsToWorld(Vec2 pixels) {
        float cos = cos(cameraAngle), sin = -sin(cameraAngle);
        
        Vec2 res = new Vec2(pixels.x-WIDTH/2, pixels.y-HEIGHT/2);
        res.set(res.x*cos - res.y*sin + WIDTH/2, res.x*sin + res.y*cos + HEIGHT/2);
        
        return box2d.coordPixelsToWorld(res.add(cameraTranslation));
    }
    
    public static Vec2 coordPixelsToWorld(float pixelX, float pixelY) {
        return coordPixelsToWorld(new Vec2(pixelX, pixelY));
    }
    
    public static Vec3 coordWorldToPixels(Vec3 coord) {
        Vec2 xy = box2d.coordWorldToPixels(new Vec2(coord.x, coord.y));
        float z = box2d.scalarWorldToPixels(coord.z);
        return new Vec3(xy.x, xy.y, z);
    }
    
    public static Vec3 vectorWorldToPixels(Vec3 vec) {
        Vec2 xy = box2d.vectorWorldToPixels(new Vec2(vec.x, vec.y));
        float z = box2d.scalarWorldToPixels(vec.z);
        return new Vec3(xy.x, xy.y, z);
    }
}

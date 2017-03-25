
package main;

import ai.PID;
import ai.Path;
import beads.AudioContext;
import beads.Gain;
import entities.building.Block;
import entities.car.*;
import entities.surface.Floor;
import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import static main.Main.*;
import static util.Constants.*;
import static main.Init.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import processing.core.PGraphics;
import shiffman.box2d.*;
import util.Skybox;
import util.audio.CollisionSounds;
import static util.input.Input.*;

/**
 *
 * @author Nithin
 */
public final class Game {
    
    public static Box2DProcessing box2d;
    public static Path path;
    
    public static Floor floor;
    public static Skybox skybox;
    
    public static AudioContext ac;
    public static Gain gain;
    
    private static PGraphics floorLayer;
    
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<AI_Car> aiCars = new ArrayList<>();
    public static ArrayList<Block> blocks = new ArrayList<>();
    
    protected static Body cameraTarget;
    private static final Vec2 cameraTranslation = new Vec2(0, 0);
    private static float cameraAngle = 0;
    
    private static final Vec3 audioListener = new Vec3(0, 0, 0);
    
    public static final CollisionSounds collisionSounds = new CollisionSounds();
    
    private static boolean tiltCamera = false;
    private static int cameraZ = 0;
    
    private static PGraphics g;
    
    protected static void init() {
        g = c.g;
        initAll();
        c.perspective(PI/3.0f, 1.f*WIDTH/HEIGHT, 3, 2000);
        skybox = getSkybox(FLATLAND_SKYBOX);
        
        floorLayer = floor.getFloorLayer();
        initialized = true;
    }
    
    protected static void tick() {
        
        floorLayer.beginDraw();
        
        Vec2 cPos = cameraTarget.getPosition();
        audioListener.set(cPos.x, cPos.y, box2d.scalarPixelsToWorld(800 - cameraZ));
        
        aiCars.forEach(ai -> ai.checkFront());
        updatePathDebug();
        updateAI_Debug();
        updateCamera();
        cars.forEach(car -> car.update());
        box2d.step(TIMESTEP, 8, 3);
        
        floor.update();
    }
        
    protected static void render() {
        c.background(255, 0, 0);
        cameraZ = constrain(cameraZ, -300, 800);
        
//        skybox.render(g);
        
        //if(true) return;
        c.pushMatrix();
        
        c.translate(WIDTH/2, HEIGHT/2, cameraZ);
        
        if(tiltCamera) c.rotateX(PI/2.3f);
        else c.rotateX(PI/6f);
        
        c.rotateZ(cameraAngle);
        skybox.render(g);
        
        c.translate(-WIDTH/2, -HEIGHT/2, -cameraZ);
        
        
        //if(true) return;
        c.translate(-cameraTranslation.x, -cameraTranslation.y, cameraZ);
        
        
        //c.background(120, 160, 180);
        c.ambientLight(100, 100, 100);
        c.directionalLight(220, 220, 220, -1, 1, -1);

        cars.forEach(car -> car.render(g));

        cars.forEach(car -> car.updateTrackMarks());

        blocks.forEach(b -> b.render(g));
        c.fill(0);
        path.render();
        c.stroke(0);

        floor.render();
        
        floorLayer.endDraw();
        c.popMatrix();
        c.fill(0);
        c.stroke(0);
        c.text(""+c.frameRate, 50, 50, 150, 150);
        //System.out.println(c.frameRate);
    }
    
    private static void updatePathDebug() {
        if(isKeyPressed(VK_CONTROL)) {
            boolean directed = consumeMousePress(LEFT),
                    undirected = directed? false : consumeMousePress(RIGHT);
            
            Vec2 pos = coordPixelsToWorld(c.mouseX, c.mouseY);
            if(directed || undirected) path.addNode(pos.x, pos.y, directed, consumeInput(VK_SPACE));
            //path.addNode(pos.x, pos.y, false, false);
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
            Block b = new Block(pos.x, pos.y, 0, 10, 10, 10, false);
            blocks.add(b);
        }
    }
    
    private static void updateCamera() {
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition());
        cameraAngle += (cameraTarget.getAngle()-PI/2 - cameraAngle)/20.;
        Vec2 newCamTrans = new Vec2(pos.x-WIDTH/2, pos.y-HEIGHT/2), deltaCamTrans = newCamTrans.sub(cameraTranslation);
        cameraTranslation.set(newCamTrans);
        if(consumeInput(VK_Z)) cameraZ += 60;
        if(consumeInput(VK_X)) cameraZ -= 60;
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
    
    public static float getDistanceToAudioListener(Vec3 coord) {
        Vec3 dif = coord.sub(audioListener);
        return sqrt(dif.x*dif.x + dif.y*dif.y + dif.z*dif.z);
    }
    
    public static float getDistanceToAudioListener(float cx, float cy, float cz) {
        return getDistanceToAudioListener(new Vec3(cx, cy, cz));
    }
    
    public static float getCamAngle() {
        return cameraAngle;
    }
    
    public static float getCamTransX() {
        return cameraTranslation.x;
    }
    
    public static float getCamTransY() {
        return cameraTranslation.y;
    }
    
    public static float getCamTransZ() {
        return cameraZ;
    }
}

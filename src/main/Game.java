
package main;

import entities.car.ai.Ambient_AI;
import ai.PID;
import ai.Path;
import beads.AudioContext;
import beads.Gain;
import entities.building.Block;
import entities.car.*;
import entities.car.ai.Pursuer_AI;
import entities.spawners.AmbientCarSpawner;
import entities.spawners.PursuerSpawner;
import entities.spawners.Spawner;
import entities.surface.Floor;
import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static main.Main.*;
import static util.Constants.*;
import static main.Init.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import particles.explosions.Explosion;
import procGen.MapGen;
import static processing.core.PConstants.PI;
import processing.core.PGraphics;
import shiffman.box2d.*;
import util.Skybox;
import util.audio.sounds.CarSounds;
import util.audio.sounds.CollisionSounds;
import util.audio.sounds.ExplosionSounds;
import util.audio.sounds.SirenSounds;
import util.audio.sounds.SkidSounds;
import util.audio.sounds.Sound;
import static util.input.Input.*;

/**
 *
 * @author Nithin
 */
public final class Game {
    
    public static Box2DProcessing box2d;
    public static Path path;
    
    public static Floor floor;
    public static MapGen map;
    
    public static Skybox skybox;
    
    public static AudioContext ac;
    public static Gain gain;
    
    private static PGraphics floorLayer;
    
    protected static UserCar user;
    public static CarRemnants userRemnants;
    
    public static final CollisionSounds collisionSounds = new CollisionSounds();
    public static final CarSounds carSounds = new CarSounds();
    public static final SkidSounds skidSounds = new SkidSounds();
    public static final SirenSounds sirenSounds = new SirenSounds();
    public static final ExplosionSounds explosionSounds = new ExplosionSounds();
    
    public static final List<Sound> sounds = Arrays.asList(collisionSounds, carSounds, skidSounds, sirenSounds, explosionSounds);
    
    public static final ArrayList<Car> cars = new ArrayList<>();
    public static final ArrayList<AI_Car> aiCars = new ArrayList<>();
    public static final ArrayList<Block> blocks = new ArrayList<>();
    
    public static final ArrayList<CarRemnants> carRemnants = new ArrayList<>();
    
    public static final ArrayList<Explosion> explosions = new ArrayList<>();
    
    public static final ArrayList<Spawner> spawners = new ArrayList<>();
    
    public static final PursuerSpawner pursuerSpawner = new PursuerSpawner();
    public static final AmbientCarSpawner ambientCarSpawner = new AmbientCarSpawner();
    
    protected static Body cameraTarget;
    private static final Vec2 cameraTranslation = new Vec2(0, 0);
    private static float cameraAngle = 0;
    
    private static final Vec3 audioListener = new Vec3(0, 0, 0);
    
    private static boolean tiltCamera = false;
    private static int cameraZ;
    
    private static boolean mute = false;
    
    private static long startTime;
    
    private static PGraphics g;
    
    protected static void init() {
        g = c.g;
        initAll();
        c.perspective(PI/3.0f, 1.f*WIDTH/HEIGHT, 3, 2000);
        skybox = getSkybox(GHOST_TOWN_SKYBOX);
        //skybox = getSkybox(FLATLAND_SKYBOX);
        cameraZ = WIDTH/3;
        
        floorLayer = floor.getFloorLayer();
        ac.out.setGain(mute? 0:0.8f);
        startTime = System.currentTimeMillis();
        
        c.sphereDetail(5);
        
        initialized = true;
    }
    
    protected static void tick() {
        floorLayer.beginDraw();
        
        Vec2 cPos = cameraTarget.getPosition();
        audioListener.set(cPos.x, cPos.y, box2d.scalarPixelsToWorld(WIDTH*0.4f - cameraZ));
        
        updatePathDebug();
        updateAI_Debug();
        updateCamera();
        map.update();
            
        spawners.forEach(Spawner::update);
        aiCars.forEach(AI_Car::checkFront);
        cars.forEach(Car::update);
        
        carRemnants.forEach(CarRemnants::update);
        
        box2d.step(TIMESTEP, 8, 3);
        
        floor.update();
        
        updateMute();
        sounds.forEach(Sound::update);

        aiCars.removeIf(Car::isDead);
        cars.removeIf(Car::isDead);
        
        blocks.removeIf(Block::isDead);
        
        explosions.forEach(Explosion::update);
        explosions.removeIf(Explosion::isDead);
        
        carRemnants.removeIf(CarRemnants::isDead);
        
        if(user.isDead() && userRemnants.isDead()) initUserVehicle();
    }
        
    protected static void render() {
        c.background(255, 0, 0);
        cameraZ = constrain(cameraZ, -WIDTH/5, WIDTH*3/5);
        //cameraZ = 2*WIDTH/5;
//        skybox.render(g);
        
        //if(true) return;
        c.pushMatrix();
        
        c.translate(WIDTH/2, HEIGHT/2, cameraZ);
        
        if(tiltCamera) c.rotateX(PI/2.3f);
        else c.rotateX(PI/6f);
        
        c.rotateZ(-cameraAngle);
        skybox.render(g);
        
        c.translate(-WIDTH/2, -HEIGHT/2, -cameraZ);
        
        //if(true) return;
        c.translate(-cameraTranslation.x, -cameraTranslation.y, cameraZ);
        
        
        //c.background(120, 160, 180);
        c.ambientLight(100, 100, 100);
        c.directionalLight(220, 220, 220, -1, 1, -1);
        
        map.render();
        
        cars.forEach(car -> {
            car.render(g);
            car.updateTrackMarks();
        });

        blocks.forEach(b -> b.render(g)); 
        
        carRemnants.forEach(cr -> cr.render(g));
        
        c.fill(0);
        path.render();
        c.stroke(0);

        floor.render();
        
        floorLayer.endDraw();
        
        cars.forEach(car -> car.postRender(g));
        
        explosions.forEach(exp -> exp.postRender(g));
        
        c.popMatrix();
        c.fill(0, 100);
        c.stroke(0);
        
        c.hint(DISABLE_DEPTH_TEST);
        c.noLights();
        float fps = c.frameRate;
        String text = "FPS "+fps;
        c.rect(50, 50, c.textWidth(text), 50);
        if(fps>FPS*5/6) c.fill(0, 255, 0);
        else if(fps>FPS*2/3) c.fill(220, 220, 0);
        else c.fill(255, 0, 0);
        c.text(text, 50, 50, 150, 150);
        c.fill(255);
        c.text("Health: "+user.getHealth(), 50, 70, 150, 150);
        c.hint(ENABLE_DEPTH_TEST);
        //System.out.println(c.frameRate);
    }
    
    private static void updateMute() {
        if(consumeInput(VK_SPACE)) ac.out.setGain((mute=!mute)? 0:0.8f);
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
            //AI_Car aiCar = new Ambient_AI(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 6, 1.8f, 1.2f, new PID(-0.1f, -1.3f, 0f));
            AI_Car aiCar = new Ambient_AI(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 2.5f, 0.6f, 0.4f, new PID(-0.1f, -1.3f, 0f));
            aiCars.add(aiCar);
            cars.add(aiCar);
            //cameraTarget = aiCar.chasis;
        }
        
        if(consumeInput(VK_N)) {
            //AI_Car aiCar = new Pursuer_AI(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 6, 2.5f, 2f, new PID(-0.1f, -0.7f, 0f));
            AI_Car aiCar = new Pursuer_AI(pos.x, pos.y, atan2(c.pmouseY-c.mouseY, c.mouseX-c.pmouseX), 2.5f, 0.83f, 0.67f, new PID(-0.1f, -0.4f, 0f));
            aiCars.add(aiCar);
            cars.add(aiCar);
            //cameraTarget = aiCar.chasis;
        }
        
        if(consumeInput(VK_K)) {
            Block b = new Block(pos.x, pos.y, 0, 10, 10, 10, false);
            blocks.add(b);
        }
    }
    
    private static void updateCamera() {
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition()), targetDir = cameraTarget.getWorldVector(new Vec2(0, -1)), camDir = new Vec2(cos(cameraAngle), sin(cameraAngle));
        cameraAngle -= atan2(targetDir.x*camDir.y + targetDir.y*camDir.x, targetDir.x*camDir.x - targetDir.y*camDir.y)/40f;
        Vec2 newCamTrans = new Vec2(pos.x-WIDTH/2, pos.y-HEIGHT/2), deltaCamTrans = newCamTrans.sub(cameraTranslation);
        cameraTranslation.set(newCamTrans);
        if(consumeInput(VK_Z)) cameraZ += 60;
        if(consumeInput(VK_X)) cameraZ -= 60;
        if(consumeInput(VK_T)) tiltCamera = !tiltCamera;
    }
    
    public static Vec2 coordPixelsToWorld(Vec2 pixels) {
        float cos = cos(cameraAngle), sin = sin(cameraAngle);
        
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
    
    public static float getDistanceToAudioListener(float cx, float cy) {
        return getDistanceToAudioListener(cx, cy, 0);
    }
    
    public static float getDistanceToAudioListener(Vec2 coord) {
        return getDistanceToAudioListener(coord.x, coord.y, 0);
    }
    
    public static float getDistanceToCameraTarget(Vec2 pos) {
        return pos.sub(cameraTarget.getPosition()).length();
    }
    
    public static float getDistanceToCameraTarget(float x, float y) {
        return getDistanceToCameraTarget(new Vec2(x, y));
    }
    
    public static Vec2 getCameraTarget() {
        return cameraTarget.getPosition();
    }
    
    public static Vec2 getCameraTargetVelocity() {
        return cameraTarget.getLinearVelocity();
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
    
    public static double getTimeElapsed() {
        return (System.currentTimeMillis()-startTime)*0.001;
    }
}

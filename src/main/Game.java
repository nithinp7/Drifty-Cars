
package main;

import ai.Path;
import beads.AudioContext;
import beads.Gain;
import entities.building.Block;
import entities.car.*;
import entities.spawners.*;
import entities.surface.Floor;
import static java.awt.event.KeyEvent.*;
import java.io.File;
import java.util.*;
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
import processing.data.JSONObject;
import shiffman.box2d.*;
import util.Skybox;
import util.audio.sounds.*;
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
    
    private static boolean mute = false, debug = false;
    protected static boolean restartFlag = false;
    
    private static long startTime;
    
    private static int view = CLOSE_ABOVE_VIEW;
    
    private static long highscore;
    
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
        
        readScore();
        
        initialized = true;
    }
    
    protected static void tick() {
        floorLayer.beginDraw();
        
        Vec2 cPos = cameraTarget.getPosition();
        audioListener.set(cPos.x, cPos.y, box2d.scalarPixelsToWorld(WIDTH*0.4f - cameraZ));
        
        updateDebugInput();
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
        
        if(restartFlag) restartGame();// || (user.isDead() && userRemnants.isDead())) restartGame();
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
        
        renderHUD();
        //System.out.println(c.frameRate);
    }
    
    private static void renderHUD() {
        c.fill(0, 100);
        c.stroke(0);
        
        c.hint(DISABLE_DEPTH_TEST);
        c.noLights();
        c.textSize(14);
        c.textAlign(CENTER, CENTER);
        float fps = c.frameRate, health = constrain(user.getHealth(), 0, 100), throttlePercent = user.getThrottlePercent();
        long score = user.getScore();
        String textFps = "FPS: "+(int)fps, textHealth = "Health: "+(int)health+"%";
        c.rect(50, 50, 150, 60);
        if(fps>FPS*5/6) c.fill(0, 255, 0);
        else if(fps>FPS*2/3) c.fill(220, 220, 0);
        else c.fill(255, 0, 0);
        c.text(textFps, 50, 50, 150, 20);
        c.fill(255f);
        c.text("Highscore: "+highscore, 50, 70, 150, 20);
        c.text("Score: "+score, 50, 90, 150, 20);
        
        c.fill(0, 100);
        c.rect(WIDTH-200, 50, 150, 60);
        if(health>75) c.fill(255f);
        else if(health>30) c.fill(220, 220, 0);
        else c.fill(255, 0, 0);
        c.text(textHealth, WIDTH-200, 50, 150, 20);
        c.fill(255f);
        c.text("Throttle: "+(int)throttlePercent+"%", WIDTH-200, 70, 150, 20);
        c.fill(255, 0, 0);
        if(user.brake) c.text("BRAKING", WIDTH-200, 90, 150, 20);
        
        if(user.isDead()) {
            c.fill(0, 100);
            c.rectMode(CENTER);
            c.rect(WIDTH/2, HEIGHT/4, WIDTH/4, 120);
            c.rectMode(CORNER);
            c.textSize(24);
            c.fill(255f);
            c.text(score>highscore?"New Highscore!":"Highscore: "+highscore, 3*WIDTH/8, HEIGHT/4-60, WIDTH/4, 40);
            c.text("Score: "+score, 3*WIDTH/8, HEIGHT/4-20, WIDTH/4, 40);
            c.text("Press R to restart game", 3*WIDTH/8, HEIGHT/4+20, WIDTH/4, 40);
        }
        c.hint(ENABLE_DEPTH_TEST);
    }
    
    private static void updateMute() {
        if(consumeInput(VK_M)) ac.out.setGain((mute=!mute)? 0:0.8f);
    }
    
    private static void updateDebugInput() {
        if(consumeInput(VK_R)) restartFlag = true;
        //if(consumeInput(VK_D)) debug = !debug;
    }
    
    private static void updateCamera() {
        if(consumeInput(VK_V)) view = (view+1)%3;
        float invChaseSensitivity = view<2?100:20;
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition()), targetDir = cameraTarget.getWorldVector(new Vec2(0, -1)), camDir = new Vec2(cos(cameraAngle), sin(cameraAngle));
        cameraAngle -= atan2(targetDir.x*camDir.y + targetDir.y*camDir.x, targetDir.x*camDir.x - targetDir.y*camDir.y)/invChaseSensitivity;
        Vec2 newCamTrans = new Vec2(pos.x-WIDTH/2, pos.y-HEIGHT/2), deltaCamTrans = newCamTrans.sub(cameraTranslation);
        cameraTranslation.set(newCamTrans);
        if(debug) {
            if(consumeInput(VK_Z)) cameraZ += 60;
            if(consumeInput(VK_X)) cameraZ -= 60;
            if(consumeInput(VK_T)) tiltCamera = !tiltCamera;
        } else {
            tiltCamera = view==2;
            cameraZ = (int)(WIDTH*(view==0?0.3:view==1?0.43f:0.44f));
        }
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
    
    private static void readScore() {
        try {
            JSONObject jo = loadJSONObject(new File(SCORE_SAVE_URL));
            highscore = jo.getLong("highscore", 0);
        } catch(Exception e) {
            highscore = 0;
            saveScore();
        }
    }
    
    protected static void saveScore() {
        long score = user.getScore();
        if(score <= highscore) return;
        JSONObject jo = new JSONObject();
        jo.setLong("highscore", score);
        highscore = score;
        
        jo.save(new File(SCORE_SAVE_URL), "");
    }
}

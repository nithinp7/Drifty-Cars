
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
import entities.spawners.Spawner;
import entities.surface.Floor;
import procGen.MapGen;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.DISABLE_DEPTH_TEST;
import static processing.core.PConstants.ENABLE_DEPTH_TEST;
import processing.core.PImage;
import processing.core.PShape;
import static util.Constants.*;
import util.audio.sounds.Sound;
import static util.geometry.Models.createVehicleModel;

/**
 *
 * @author Nithin
 */
public final class Init {
    
    private static PShape loadingShape;
    private static PImage loadingScreen;
    
    public static void initAll() {
        initSounds();
        initTextures();
        initSkyboxes();
        initPhysics();
        initAI();
        initMap();
        initUserVehicle();
        initSpawners();
        //initBuildings();
    }
    
    protected static void initLoadingScreen() {
        loadingShape = createVehicleModel(MODEL_POLICE_CAR, WIDTH*0.05f*3.5f, WIDTH*0.05f*1.23f, WIDTH*0.05f*0.67f, false);
        loadingScreen = c.loadImage(LOADING_SCREEN_URL);
    }
    
    protected static void tickLoadingScreen(boolean initialized) {
        
        c.background(CYBER_FLOOR_COLOR.getRGB());
        
        c.hint(DISABLE_DEPTH_TEST);
        c.image(loadingScreen, 0, 0, WIDTH, HEIGHT);
        c.fill(0, 100);
        c.rect(0, 0, WIDTH, HEIGHT);
        c.rect(WIDTH/4, 3*HEIGHT/4, WIDTH/2, HEIGHT/8);
        c.textAlign(CENTER, CENTER);
        c.textSize(24);
        c.fill(255);
        c.text(initialized?"Press Space to Start":"Loading...", WIDTH/4, 3*HEIGHT/4, WIDTH/2, HEIGHT/8);
        
        c.hint(ENABLE_DEPTH_TEST);
        
        c.translate(WIDTH/2, HEIGHT/2);
        c.rotateX(PI*0.4f);
        c.translate(0, -HEIGHT/8f);
        c.directionalLight(255, 255, 255, 1, -1, -1);
        c.ambientLight(100, 100, 100);
        c.rotate(c.millis()/1300.f);
        c.shape(loadingShape);
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
        sounds.forEach(Sound::init);
        box2d.world.setContactListener(collisionSounds);
    }
    
    private static void initMap() {
        floor = new Floor(1200, 1200);
        map = new MapGen(1600, 1600, 8);
    }
    
    protected static void initUserVehicle() {
        initUserVehicle(new Vec2(0, 0));
    }
    
    private static void initUserVehicle(Vec2 pos) {
        //Car car = new UserCar(0, 0, -PI/4, 6, 1.8f, 1.2f);
        user = new UserCar(pos, -PI/4, 2.8f, 0.7f, 0.4f);
        cars.add(user);
        cameraTarget = user.chasis;
    }
    
    protected static void restartGame() {
        saveScore();
        if(!user.isDead()) user.dispose();
        
        if(userRemnants!=null && !userRemnants.isDead()) {
            userRemnants.dispose();
            userRemnants = null;
        }
        
        restartFlag = false;
        spawners.forEach(Spawner::restart);
        sounds.forEach(Sound::restart);
        //map.restart();
        initUserVehicle(user.chasis.getPosition());
    }
    
    private static void initSpawners() {
        spawners.add(pursuerSpawner);
        spawners.add(ambientCarSpawner);
    }
    
    private static void initBuildings() {
        blocks.add(new Block(0, 280, 0, 660, 20, 25, true));
        blocks.add(new Block(0, -280, 0, 660, 20, 25, true));
        blocks.add(new Block(-340, 0, 0, 20, 620, 25, true));
        blocks.add(new Block(340, 0, 0, 20, 620, 25, true));
    }
}

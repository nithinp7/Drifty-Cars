
package main;

import ai.PID;
import ai.Path;
import beads.AudioContext;
import beads.Gain;
import entities.building.Block;
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
import util.Skybox;
import static util.input.Input.consumeInput;

/**
 *
 * @author Nithin
 */
public final class Game {
    
    public static Box2DProcessing box2d;
    public static Path path;
    
    public static Asphalt asphalt;
    public static Skybox skybox;
    
    private static PGraphics trackMarksLayer;
    private static PImage trackMarksImg, asph;
    
    public static AudioContext ac;
    public static Gain gain;
    
    public static ArrayList<Car> cars = new ArrayList<>();
    public static ArrayList<AI_Car> aiCars = new ArrayList<>();
    public static ArrayList<Block> blocks = new ArrayList<>();
    
    protected static Body cameraTarget;
    private static final Vec2 cameraTranslation = new Vec2(0, 0);
    private static float cameraAngle = 0;
    
    private static final Vec2 audioListener = new Vec2(0, 0);
    
    private static boolean tiltCamera = false;
    private static int cameraZ = 0;
    
    private static PGraphics g;
    
    protected static void init() {
        g = c.g;
        trackMarksLayer = c.createGraphics(WIDTH, HEIGHT);
        trackMarksLayer.beginDraw();
        //trackMarksImg = trackMarksLayer.get();
        initAll();
        c.perspective(PI/3.0f, 1.f*WIDTH/HEIGHT, 3, 2000);
        skybox = getSkybox(SUNNY_SKYBOX);
        
//        trackMarksLayer.fill(120, 160, 180, 255);
        trackMarksLayer.fill(200, 220, 240, 255);

        
        trackMarksLayer.rect(0, 0, WIDTH, HEIGHT);
        
//        trackMarksLayer.beginShape();
//        
//        trackMarksLayer.vertex(0, 0);
//        trackMarksLayer.vertex(WIDTH, 0);
//        trackMarksLayer.vertex(WIDTH, HEIGHT);
//        trackMarksLayer.vertex(0, 2*HEIGHT);
//        
//        trackMarksLayer.beginShape();
//        
//        asph = getTextureImage(ASPHALT_TEX);
//        
//        asph.loadPixels();
//        for(int i=0; i<asph.pixels.length; i++) {
//            int pix = asph.pixels[i];
//            //asph.pixels[i] = c.color(2f*c.red(pix), 1.0f*c.green(pix), 0.6f*c.blue(pix));
//            asph.pixels[i] = c.color(1.2f*c.red(pix), 1.2f*c.green(pix), 1.2f*c.blue(pix));
//        }
//        asph.updatePixels();
        //asph.filter(DILATE);
//        /trackMarksLayer.image(asph, 0, 0, 2*WIDTH, 2*HEIGHT);
//        for(int i=0; i<20; i++) for(int j=0; j<20; j++) trackMarksLayer.image(asph, i*WIDTH/20, j*HEIGHT/20, WIDTH/20, HEIGHT/20);
    }
    
    protected static void tick() {
        trackMarksLayer.beginDraw();
        
        audioListener.set(cameraTarget.getPosition());
        
        aiCars.forEach(ai -> ai.checkFront());
        updatePathDebug();
        updateAI_Debug();
        updateCamera();
        cars.forEach(car -> car.update());
        box2d.step(TIMESTEP, 8, 3);
    }
        
    protected static void render() {
        
        cameraZ = constrain(cameraZ, 300, 1000);
        
        c.translate(WIDTH/2, HEIGHT/2, cameraZ);
        
        if(tiltCamera) c.rotateX(PI/2.3f);
        else c.rotateX(PI/7f);
        
        c.rotateZ(cameraAngle);
        c.translate(-WIDTH/2, -HEIGHT/2, -cameraZ);
        
        skybox.render(g);
        
        //if(true) return;
        c.translate(-cameraTranslation.x, -cameraTranslation.y, cameraZ);
        
        //c.background(120, 160, 180);
        c.ambientLight(100, 100, 100);
        c.directionalLight(220, 220, 220, -1, 1, -1);
        //c.directionalLight(255, 255, 255, -1, 1, -1);

        cars.forEach(car -> car.render(g));

        cars.forEach(car -> car.updateTrackMarks(trackMarksLayer));

        blocks.forEach(b -> b.render(g));
        c.fill(0);
        //path.render();

        c.stroke(0);

        c.image(trackMarksLayer, 0, 0, WIDTH, HEIGHT);//-WIDTH/2, -HEIGHT/2, 2*WIDTH, 2*HEIGHT);
        
        trackMarksLayer.endDraw();
        
        //System.out.println(c.frameRate);
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
            Block b = new Block(pos.x, pos.y, 0, 10, 10, 10, false);
            blocks.add(b);
        }
    }
    
    private static void updateCamera() {
        Vec2 pos = box2d.coordWorldToPixels(cameraTarget.getPosition());
        cameraAngle += (cameraTarget.getAngle()-PI/2 - cameraAngle)/20.;
        Vec2 newCamTrans = new Vec2(pos.x-WIDTH/2, pos.y-HEIGHT/2), deltaCamTrans = newCamTrans.sub(cameraTranslation);
        //trackMarksLayer.image(trackMarksImg, deltaCamTrans.x, deltaCamTrans.y);
        //trackMarksLayer.translate(deltaCamTrans.x, deltaCamTrans.y);
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
    
    public static float getDistanceToAudioListener(Vec2 coord) {
        return coord.sub(audioListener).length();
    }
}

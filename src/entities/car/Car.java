
package entities.car;

import beads.SamplePlayer;
import java.awt.Color;
import java.util.HashMap;
import static main.Game.*;
import static main.Main.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import processing.core.PGraphics;
import static util.Constants.*;
import static util.Constants.createSound;
import util.audio.SampleControls;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public abstract class Car implements Drawable {
    
    public float turn = 0,
                 throttle = 0,
                 maxTurnTorque = 10000;
    
    public boolean reverse = false, brake = false;
    
    public final Body chasis;
    public final Axle frontAxle, rearAxle;
    
    private final float l, l_pixels, w, w_pixels, h, h_pixels;
    
    private final Color color;
    private final SampleControls engine, skidSound, crashSound;
    
    public Car(float x, float y, float theta, float l, float w, float h) {
        
        this.l = l;
        this.w = w;
        this.h = h;
        
        l_pixels = box2d.scalarWorldToPixels(l);
        w_pixels = box2d.scalarWorldToPixels(w);
        h_pixels = box2d.scalarWorldToPixels(h);
        
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(l/2, w/2);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = 1000/(l*w*h);
        fd.friction = 3;
        fd.restitution = 0.5f;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, y);
        bd.angle = theta;
        
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("TYPE", TYPE_CAR);
        
        chasis = box2d.createBody(bd);
        chasis.createFixture(fd).setUserData(userData);
        
        frontAxle = new Axle(1.1f*w, l/8, 0.5f, theta, chasis, l/4, 0, PI/6);
        rearAxle = new Axle(1.5f*w, l/7, 0.6f, theta, chasis, -l/4, 0, 0);
        
        color = new Color((int)c.random(150, 250), (int)c.random(150, 250), (int)c.random(150, 250));
        
        engine = createSound(CAR_ENGINE, true);
        skidSound = createSound(CAR_SKID, true);
        
        skidSound.pitchGlide.setValue(1f);
        
        crashSound = createSound(CAR_CRASH, false);
        crashSound.gainGlide.setValue(0);
        crashSound.pitchGlide.setValue(1);
        crashSound.player.setKillOnEnd(false);
        
        userData.put("CRASH_SOUND", crashSound);
    }
    
    public Car(Vec2 loc, float theta, float l, float w, float h) {
        this(loc.x, loc.y, theta, l, w, h);
    }
    
    public void update() {
        updateSteering();
        drive();
        frontAxle.update(brake);
        rearAxle.update(brake);
    }
    
    private void drive() {
        throttle = constrain(throttle, 0, 1);
        if(reverse) throttle = 0.2f;
        float speed = getForwardSpeed();//chasis.getLinearVelocity().length();
        Vec2 pos = chasis.getPosition();
        
        updateSounds(pos, speed);
        
        float theta = frontAxle.axle.getAngle();
        frontAxle.axle.applyForceToCenter(new Vec2(cos(theta), sin(theta)).mul(72000 * throttle * (reverse ? -1 : 1)));
    }
    
    private void updateSounds(Vec2 pos, float speed) {
        float dist = getDistanceToAudioListener(pos.x, pos.y, 0);
        float vol = 1-pow(norm(constrain(dist, 0, 300), 0, 300), 0.25f);
        float pitch = (throttle*0.2f+constrain(speed, 0, 20)*0.7f*0.05f)*3.5f + 0.1f*sin(c.millis()/450.0f)*sin(c.millis()/250.f)+0.3f;
        engine.gainGlide.setGlideTime(50);
        engine.gainGlide.setValue(vol*0.45f);
        engine.pitchGlide.setGlideTime(1800);
        //engine.pitchGlide.setValue(map(speed, 0, 10, 0.8f, 1.6f));
        engine.pitchGlide.setValue(pitch);
        
        float slideSpeed = getSlideSpeed();
        float skidVolume = slideSpeed < 5.52 ? 0 : constrain(slideSpeed/14.0f, 0, 0.1f);
        skidSound.gainGlide.setGlideTime(400);
        skidSound.gainGlide.setValue(skidVolume*vol);
        skidSound.pitchGlide.setGlideTime(250);
        skidSound.pitchGlide.setValue(skidVolume*vol*0.6f+0.85f);
    }
    
    private void updateSteering() {
        turn = constrain(turn, -1, 1);
        frontAxle.axle.applyTorque(turn*maxTurnTorque);
    }
    
    public float getForwardSpeed() {
        return frontAxle.getForwardSpeed();
    }
    
    public float getSlideSpeed() {
        return min(frontAxle.getSlideSpeed(), rearAxle.getSlideSpeed());
    }
    
    @Override
    public void render(PGraphics g) {
        frontAxle.render(g);
        rearAxle.render(g);
        g.pushMatrix();
        g.pushStyle();
            Vec2 pos = box2d.coordWorldToPixels(chasis.getPosition());
            g.translate(pos.x, pos.y, h+box2d.scalarWorldToPixels(0.5f));
            g.rotate(-chasis.getAngle());
            //c.fill(180, 120, 110);
            g.fill(color.getRed(), color.getGreen(), color.getBlue());
            g.strokeWeight(1.5f);
            g.stroke(30, 40, 30);
            g.box(l_pixels, w_pixels, h_pixels);
//            g.translate(l_pixels, w_pixels/4);
//            g.emissive(150, 20, 20);
//            g.fill(200, 20, 20);
//            g.noStroke();
//            g.sphere(w_pixels);
//            g.emissive(0);
//            g.translate(0, -w_pixels/2);
//            g.sphere(w_pixels);
        g.popStyle();
        g.popMatrix();
    }
    
    public void updateTrackMarks() {
        frontAxle.updateTrackMarks(brake);
        rearAxle.updateTrackMarks(brake);
    }
}

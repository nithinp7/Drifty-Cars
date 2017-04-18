
package entities.car.ai;

import ai.PID;
import entities.car.AI_Car;
import java.awt.Color;
import static main.Game.*;
import org.jbox2d.common.Vec2;
import particles.LightParticle;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.norm;
import static processing.core.PApplet.pow;
import processing.core.PGraphics;
import static util.Constants.FPS;
import static util.Constants.MODEL_POLICE_CAR;
import util.audio.AudioRequest;

/**
 *
 * @author nithin
 */
public final class Pursuer_AI extends AI_Car {
    
    private final LightParticle redLight, blueLight;
    
    private final AudioRequest<Float> sirenSound;
    
    private int lightCounter = 0;

    public Pursuer_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h, steeringControl, 0, 2, MODEL_POLICE_CAR);
        setImpactResistance(0.15f);
        setDeleteDistance(110);
        
        redLight = new LightParticle(chasis, 0, -0.4f*w, h, 3.5f, new Color(255, 0, 0, 100));
        blueLight = new LightParticle(chasis, 0, 0.4f*w, h, 3.5f, new Color(0, 0, 255, 100));
        
        sirenSound = new AudioRequest<>(0, 0, 1, 1, Float.MAX_VALUE);
        sirenSounds.addRequest(sirenSound);
    }

    @Override
    protected float getThrottle(float recommendedThrottle) {
        return !reverse ? constrain(recommendedThrottle-getSlideSpeed(), 0, 8f)+0.3f : 1f;
    }
    
    @Override
    public void update() {
        //brake = getSlideSpeed() > 4;
        
        redLight.update();
        blueLight.update();
        Vec2 pos = chasis.getPosition(), vel = chasis.getLinearVelocity(), target = getCameraTarget(), targetVel = getCameraTargetVelocity(), dif = pos.sub(target);
        
        float dist = dif.length();
        float speed = 60;
        checkFront = dist>30;
        
//        if(dist>30 && dist<60) target.addLocal(targetVel);//target.addLocal(targetVel.mul((1-dist/30)*0.15f));
        
        path.removeTemporarySegment(currentSegment);
        
        currentSegment = path.createTemporarySegment(pos, target, true);
        
        updateSound();
        
        super.update();
    }
    
    private void updateSound() {
        float dist = getDistanceToAudioListener(chasis.getPosition()),
              vol = dist > 200 ? 0 : 1-pow(norm(constrain(dist, 0, 300), 0, 300), 0.25f);
        
        sirenSound.setGainTime(40);
        sirenSound.setGainValue(vol*0.15f);
    }
    
    @Override
    public void postRender(PGraphics g) {
        super.postRender(g);
        if(lightCounter > FPS) lightCounter%=FPS;
        
        if(lightCounter < FPS/2) {
            redLight.postRender(g);
            if(lightCounter < FPS/4) blueLight.postRender(g);
        } else {
            blueLight.postRender(g);
            if(lightCounter < 3*FPS/4) redLight.postRender(g);
        }
        
        lightCounter++;
    }
    
    @Override
    public void dispose() {
        sirenSounds.removeRequest(sirenSound);
        super.dispose();
    }
}

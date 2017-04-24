
package particles.explosions;

import static java.lang.Math.random;
import java.util.ArrayList;
import static main.Game.box2d;
import static main.Game.explosionSounds;
import static main.Game.getDistanceToAudioListener;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import particles.SmokeParticle;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.norm;
import static processing.core.PApplet.pow;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.PI;
import processing.core.PGraphics;
import static util.Constants.*;
import static util.Constants.FPS;
import util.audio.AudioRequest;
import util.interfaces.Disposable;
import util.interfaces.PostDraw;

/**
 *
 * @author nithin
 */
public final class Explosion implements RayCastCallback, PostDraw, Disposable {
    
    private static final float SMOKE_FADE_TIME = 0.5f;
    private static final int SMOKE_PRTS = 5, ANIM_SPACING = (int)(SMOKE_FADE_TIME*FPS/SMOKE_PRTS);
    
    private final ArrayList<SmokeParticle> prts = new ArrayList<>();
    
    private final AudioRequest<Float> explosionSound;
    
    private final Body parent;
    private final Vec2 pos = new Vec2();
    private final float strength, radius, fireTime, smokeTime;
    
    private boolean dead = false;
    
    private int animCounter = 0;
    
    public Explosion(Body parent, Vec2 pos, float strength, float radius, int rays, float fireTime, float smokeTime) {
        this.parent = parent;
        this.pos.set(parent==null?pos:parent.getPosition());
        this.strength = constrain(strength, 0, 1);
        this.radius = radius;
        this.fireTime = fireTime;
        this.smokeTime = smokeTime;
        
        pos = this.pos;
        
        for(int i=0; i<SMOKE_PRTS; i++) prts.add(new SmokeParticle());
        float angleStep = 2*PI/rays;
        for(int i=0; i<rays; i++) box2d.world.raycast(this, pos, new Vec2(pos.x+radius*cos(angleStep*i), pos.y+radius*sin(angleStep*i)));
        
        float dist = getDistanceToAudioListener(pos.x, pos.y, 0),
              vol = 1-pow(norm(constrain(dist, 0, 300), 0, 300), 2f);
        
        vol *= 0.4f*strength;
        if(dist<WIDTH*0.5f) {
            explosionSound = new AudioRequest<>(vol, 0, 1f, 20, dist);
            explosionSounds.addRequest(explosionSound);
        } else explosionSound = null;
    }
    
    public Explosion(Body parent, float strength, float radius, int rays, float fireTime, float smokeTime) {
        this(parent, null, strength, radius, rays, fireTime, smokeTime);
    }
    
    public Explosion(Vec2 pos, float strength, float radius, int rays, float fireTime, float smokeTime) {
        this(null, pos, strength, radius, rays, fireTime, smokeTime);
    }
    
    @Override
    public float reportFixture(Fixture fxtr, Vec2 point, Vec2 norm, float frac) {
        Body body = fxtr.getBody();
        Vec2 dif = point.sub(pos);
        float dist = dif.length();
        
        if(parent==body) return -1;
        
        float currentStrength = strength;//*(0.4f*(1-dist/radius)+0.6f);
        fxtr.getBody().applyLinearImpulse(body.getPosition(), dif.mul(currentStrength*MAX_EXPLOSION_IMPULSE/dist), true);
        return 0;
    }
    
    public void update() {
        if(parent!=null) pos.set(parent.getPosition());
        updateAnim();
        if(!dead) prts.forEach(SmokeParticle::update);
    }
    
    private void updateAnim() {
        if(animCounter%ANIM_SPACING==0) 
            prts
            .stream()
            .filter(SmokeParticle::ended)
            .findAny()
            .ifPresent(sp -> sp.set(pos.x+(float)(random()*2-1)*radius/6, pos.y+(float)(random()*2-1)*radius/6, 
                    radius/5, 12, SMOKE_FADE_TIME, animCounter<(int)(FPS*fireTime)?EXPLOSION_FIRE_COLOR:DAMAGE_SMOKE_COLOR));
        animCounter++;
        if(animCounter>=FPS*smokeTime) dispose();
    }
    
    @Override
    public void postRender(PGraphics g) {
        prts.forEach(sp -> sp.postRender(g));
    }
    
    @Override
    public void dispose() {
        dead = true;
    }
    
    @Override
    public boolean isDead() {
        return dead;
    }
}

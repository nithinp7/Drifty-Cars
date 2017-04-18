
package entities.car;

import java.util.ArrayList;
import static main.Game.*;
import static main.Main.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.ContactEdge;
import particles.SmokeParticle;
import particles.explosions.Explosion;
import processing.core.PGraphics;
import processing.core.PShape;
import static util.Constants.DAMAGE_SMOKE_COLOR;
import static util.Constants.FPS;
import util.audio.AudioRequest;
import util.audio.sounds.SkidParam;
import static util.geometry.Models.createVehicleModel;
import util.interfaces.*;

/**
 *
 * @author Nithin
 */
public abstract class Car implements Drawable, Disposable, PostDraw {
    
    private static final float SMOKE_FADE_TIME = 0.8f;
    private static final int DAMAGE_SMOKE_SIZE = 5, ANIM_SPACING = (int)(SMOKE_FADE_TIME*FPS/DAMAGE_SMOKE_SIZE);
    
    public float turn = 0,
                 throttle = 0,
                 maxTurnTorque = 10000;
    
    public boolean reverse = false, brake = false;
    
    public final Body chasis;
    public final Axle frontAxle, rearAxle;
    
    protected final float l, l_pixels, w, w_pixels, h, h_pixels;
    
    private final AudioRequest<Float> engineSound;
    private final AudioRequest<SkidParam> skidSound;
    
    private final ArrayList<SmokeParticle> damageSmoke = new ArrayList<>();
    
    private final float dragConst;
    
    private boolean dead = false;
    
    private float health = 100, impactResistance = 1;
    
    private int damageAnimCounter = 0;
    
    private final PShape model;
    private final int modelType;
    
    public Car(float x, float y, float theta, float l, float w, float h, float dragConst, float densityMultiplier, int modelType) {
        
        this.l = l;
        this.w = w;
        this.h = h;
        
        this.dragConst = dragConst;
        
        l_pixels = box2d.scalarWorldToPixels(l);
        w_pixels = box2d.scalarWorldToPixels(w);
        h_pixels = box2d.scalarWorldToPixels(h);
        
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(l/2, w/2);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = densityMultiplier*1000/(l*w*h);
        fd.friction = 3;
        fd.restitution = 0.5f;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, y);
        bd.angle = theta;
        bd.angularDamping = 2;
        
        //HashMap<String, Object> userData = new HashMap<>();
        //userData.put("TYPE", TYPE_CAR);
        
        chasis = box2d.createBody(bd);
        chasis.createFixture(fd);
        //chasis.setUserData(userData);
        
        frontAxle = new Axle(1.1f*w, l/8, 0.5f, theta, chasis, l/4, 0, PI/6);
        rearAxle = new Axle(1.5f*w, l/7, 0.6f, theta, chasis, -l/4, 0, 0);
        
        
        
        engineSound = new AudioRequest<>(0, 0, 0, 1, Float.MAX_VALUE);
        carSounds.addRequest(engineSound);
        
        skidSound = new AudioRequest<>(0, 0, 0, 1, new SkidParam(Float.MAX_VALUE, false));
        skidSounds.addRequest(skidSound);
        
        for(int i=0; i<DAMAGE_SMOKE_SIZE; i++) damageSmoke.add(new SmokeParticle());
        
        model = createVehicleModel(modelType, l_pixels, w_pixels, h_pixels, false);
        this.modelType = modelType;
    }
    
    public Car(float x, float y, float theta, float l, float w, float h, float dragConst, int modelType) {
        this(x, y, theta, l, w, h, dragConst, 1, modelType);
    }
    
    public Car(Vec2 loc, float theta, float l, float w, float h, float dragConst, float densityMultiplier, int modelType) {
        this(loc.x, loc.y, theta, l, w, h, dragConst, densityMultiplier, modelType);
    }
    
    public Car(Vec2 loc, float theta, float l, float w, float h, float dragConst, int modelType) {
        this(loc.x, loc.y, theta, l, w, h, dragConst, 1, modelType);
    }
    
    public void update() {
        updateContacts();
        if(health<60 && damageAnimCounter++%ANIM_SPACING==0) stepSmokeAnimation();
        damageSmoke.forEach(SmokeParticle::update);
        updateSteering();
        drive();
        frontAxle.update(brake);
        rearAxle.update(brake);
    }
    
    private void drive() {
        //throttle = constrain(throttle, 0, 2.6f);
        //throttle = constrain(throttle, 0, 1);
        if(reverse) throttle = 0.2f;
        float speed = getForwardSpeed();
        Vec2 pos = chasis.getPosition();
        
        updateSounds(pos, speed);
        
        float theta = frontAxle.axle.getAngle();
        frontAxle.axle.applyForceToCenter(new Vec2(cos(theta), sin(theta)).mul(72000*throttle*(reverse? -1 : 1) - (reverse? 0 : dragConst*pow(getForwardSpeed(), 2))));
    }
    
    private void updateSounds(Vec2 pos, float speed) {
        float dist = getDistanceToAudioListener(pos.x, pos.y, 0),
              vol = dist > 250 ? 0 : 1-pow(norm(constrain(dist, 0, 300), 0, 300), 0.25f),
              pitch = (throttle*0.2f+constrain(speed, 0, 20)*0.7f*0.05f)*3.5f + 0.1f*sin(c.millis()/450.0f)*sin(c.millis()/250.f)+0.3f;
        
        engineSound.setGainTime(50);
        engineSound.setGainValue(vol*0.45f);
        engineSound.setPitchTime(1800);
        engineSound.setPitchValue(pitch);

        float slideSpeed = getSlideSpeed();
        float skidVolume = slideSpeed < 5.52f ? 0 : constrain(slideSpeed/14.0f, 0, 0.1f);
        
        skidSound.setGainTime(400);
        skidSound.setGainValue(skidVolume*vol);
        skidSound.setPitchTime(250);
        skidSound.setPitchValue(skidVolume*vol*0.6f+0.85f);
    }
    
    private void updateSteering() {
        turn = constrain(turn, -1, 1);
        frontAxle.axle.applyTorque(turn*maxTurnTorque);
    }
    
    public final float getForwardSpeed() {
        return frontAxle.getForwardSpeed();
    }
    
    public final float getSlideSpeed() {
        return min(frontAxle.getSlideSpeed(), rearAxle.getSlideSpeed());
    }
    
    @Override
    public final void render(PGraphics g) {
        frontAxle.render(g);
        rearAxle.render(g);
        Vec2 pos = box2d.coordWorldToPixels(chasis.getPosition());
        g.pushMatrix();
            g.translate(pos.x, pos.y);
            g.rotate(-chasis.getAngle());
            g.shape(model);
        g.popMatrix();
        renderAdditional(g);
    }
    
    protected void renderAdditional(PGraphics g) {}
    
    public final void updateTrackMarks() {
        frontAxle.updateTrackMarks(brake);
        rearAxle.updateTrackMarks(brake);
    }
    
    @Override
    public void dispose() {
        chasis.setUserData(null);
        if(health <= 0) {
            explosions.add(new Explosion(chasis.getPosition(), 1, 40, 40, 0.4f, 3));
            carRemnants.add(getRemnants());
        } else {
            frontAxle.dispose();
            rearAxle.dispose();
            box2d.world.destroyBody(chasis);
        }
        carSounds.removeRequest(engineSound);
        skidSounds.removeRequest(skidSound);
        dead = true;
    }
    
    public CarRemnants getRemnants() {
        return new CarRemnants(chasis, frontAxle, rearAxle, modelType, l_pixels, w_pixels, h_pixels, 10);
    }
    
    @Override
    public final boolean isDead() {
        return dead;
    }
    
    @Override
    public void postRender(PGraphics g) {
        frontAxle.postRender(g);
        rearAxle.postRender(g);
        if(health<60) damageSmoke.forEach(smoke -> smoke.postRender(g));
    }
    
    private void updateContacts() {
        ContactEdge first = chasis.getContactList();
        for(ContactEdge current=first;current != null; current=current.next) {
            if(!current.contact.isTouching()) continue;
            Body body = current.other;
            Vec2 norm = chasis.getWorldVector(current.contact.getManifold().localNormal);
            float impulse = abs(Vec2.dot(chasis.getLinearVelocity(), norm)*chasis.getMass() - Vec2.dot(body.getLinearVelocity(), norm)*body.getMass());
            health -= impulse/(50000*impactResistance);
        }
        if(health<=0) dispose();
    }
    
    private void stepSmokeAnimation() {
        Vec2 pos = chasis.getPosition();
        damageSmoke
                .stream()
                .filter(SmokeParticle::ended)
                .findAny()
                .ifPresent(sp -> sp.set(pos.x, pos.y, 1.4f, 10, SMOKE_FADE_TIME, DAMAGE_SMOKE_COLOR, norm(100-0.25f*health, 0, 100)));
    }
    
    public final float getHealth() {
        return health;
    }
    
    protected void setImpactResistance(float impactResistance) {
        this.impactResistance = constrain(impactResistance, 0, 1);
    }
}


package util.audio.sounds;

import java.util.ArrayList;
import static main.Game.getDistanceToAudioListener;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import util.audio.AudioRequest;
import util.audio.SoundManager;
import static processing.core.PApplet.*;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class CollisionSounds extends Sound<Float> implements ContactListener {
    
    private final ArrayList<CollisionEntry> collisions = new ArrayList<>();
    
    @Override
    public SoundManager<Float> createSoundManager() {
        return new SoundManager<>(CAR_CRASH, 8, false, distSoundComparator);
    }
    
    @Override
    public void update() {
        super.update();
        
        long time = System.currentTimeMillis();
        collisions.removeIf(ce -> abs(time-ce.timeStamp) > 3000);
    }
    
    @Override
    public void beginContact(Contact cntct) {}

    @Override
    public void endContact(Contact cntct) {}

    @Override
    public void preSolve(Contact cntct, Manifold mnfld) {}

    @Override
    public void postSolve(Contact cntct, ContactImpulse ci) {
        
        Fixture a = cntct.getFixtureA(), b = cntct.getFixtureB();
        CollisionEntry collision = new CollisionEntry(a, b);
        
        if(collisions.stream().anyMatch(ce -> ce.isSame(collision))) return;
        
        collisions.add(collision);
        
        WorldManifold wm = new WorldManifold();
        
        cntct.getWorldManifold(wm);
        
        Vec2 pos = new Vec2(0, 0);
        
        for(int i=0; i<ci.count; i++) pos.addLocal(wm.points[i]);
        
        pos.mulLocal(1.0f/ci.count);
        
        float dist = getDistanceToAudioListener(pos.x, pos.y, 0),
              vol = 1-pow(norm(constrain(dist, 0, 300), 0, 300), 1f);
        
        if(dist > 400) return;
        
        float largestImpulse = 0;
        
        for(float imp : ci.normalImpulses) if(imp > largestImpulse) largestImpulse = imp;
        for(float imp : ci.tangentImpulses) if(imp*0.7f > largestImpulse) largestImpulse = 0.7f*imp;
        
        float impNorm = 2*pow(norm(constrain(largestImpulse, 0, 4500), 300, 4500), 4);
        
        vol *= impNorm;
        addRequest(new AudioRequest<>(vol*0.005f, 0, 1, 0, dist));
    }
    
    private class CollisionEntry {
        private final Fixture a, b;
        private final long timeStamp;
        
        private CollisionEntry(Fixture a, Fixture b) {
            this.a = a;
            this.b = b;
            
            timeStamp = System.currentTimeMillis();
        }
        
        private boolean isSame(CollisionEntry ce) {
            return ((a==ce.a && b==ce.b) || (a==ce.b && b==ce.a)) && abs(ce.timeStamp-timeStamp)<3500;
        }
    }
}

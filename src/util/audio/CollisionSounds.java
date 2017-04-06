
package util.audio;

import static main.Game.getDistanceToAudioListener;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.contacts.Contact;
import static processing.core.PApplet.*;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class CollisionSounds implements ContactListener {
    
    private SoundManager<Float> sm;
    
    public void init() {
        sm = new SoundManager<>(CAR_CRASH, 15, false, distSoundComparator);
    }
    
    public void update() {
        sm.update();
    }
    
    @Override
    public void beginContact(Contact cntct) {}

    @Override
    public void endContact(Contact cntct) {}

    @Override
    public void preSolve(Contact cntct, Manifold mnfld) {}

    @Override
    public void postSolve(Contact cntct, ContactImpulse ci) {
        
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
        sm.addRequest(new AudioRequest(vol*0.025f, 0, 1, 0, dist));
    }
    
}

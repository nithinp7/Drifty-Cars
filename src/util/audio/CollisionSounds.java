
package util.audio;

import java.util.HashMap;
import static main.Game.getDistanceToAudioListener;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import static processing.core.PApplet.constrain;
import static processing.core.PApplet.norm;
import static processing.core.PApplet.pow;

/**
 *
 * @author Nithin
 */
public class CollisionSounds implements ContactListener {
    
    public void init() {
        
    }
    
    @Override
    public void beginContact(Contact cntct) {}

    @Override
    public void endContact(Contact cntct) {}

    @Override
    public void preSolve(Contact cntct, Manifold mnfld) {}

    @Override
    public void postSolve(Contact cntct, ContactImpulse ci) {
        Fixture a = cntct.getFixtureA(),
                b = cntct.getFixtureB();
        
        HashMap usrDataA = (HashMap) a.getUserData(),
                usrDataB = (HashMap) b.getUserData();
        
        //if((Integer)usrDataA.get("TYPE") == TYPE_CAR || (Integer)usrDataB.get("TYPE") == TYPE_CAR) {
        
        WorldManifold wm = new WorldManifold();
        
        cntct.getWorldManifold(wm);
        
        Vec2[] points = wm.points;
        Vec2 pos = new Vec2(0, 0);
        
        for(Vec2 p : points) pos.addLocal(p);
        
        pos.mulLocal(1.0f/points.length);
        
        float dist = getDistanceToAudioListener(pos.x, pos.y, 0),
              vol = 1-pow(norm(constrain(dist, 0, 300), 0, 300), 1f);
        
        if(dist > 400) return;
        
        float largestImpulse = 0;
        
        for(float imp : ci.normalImpulses) if(imp > largestImpulse) largestImpulse = imp;
        
        float impNorm = 2*pow(norm(constrain(largestImpulse, 0, 4500), 300, 4500), 4);
        
        vol *= impNorm;
        
        SampleControls sound,
                       soundA = usrDataA.containsKey("CRASH_SOUND")? (SampleControls) usrDataA.get("CRASH_SOUND") : null,
                       soundB = usrDataB.containsKey("CRASH_SOUND")? (SampleControls) usrDataB.get("CRASH_SOUND") : null;
    
//        Boolean soundA_playing = soundA==null? null : soundA.player.getPosition() != soundA.player.getSample().getLength(),
//                soundB_playing = soundB==null? null : soundB.player.getPosition() != soundB.player.getSample().getLength();
//        
//        sound = (soundA_playing == null || soundA_playing == true)? (soundB_playing == null || soundB_playing == true)? null : soundB : soundA;
//        if(sound == null) return;

        sound = soundA != null? soundA : soundB;
        
        if(sound == null) return;
        
        sound.gainGlide.setValue(vol*0.05f);
        
        sound.player.reset();
        sound.player.start();
    }
    
}

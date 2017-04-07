
package util.audio.sounds;

import util.audio.AudioRequest;
import util.audio.SoundManager;
import static util.Constants.*;

/**
 *
 * @author nithin
 */
public final class CarSounds {
    
    private SoundManager<Float> sm;
    
    public void init() {
        sm = new SoundManager<>(CAR_ENGINE, 8, true, distSoundComparator);
    }
    
    public void addRequest(AudioRequest<Float> req) {
        sm.addRequest(req);
    }
    
    public void update() {
        sm.update();
    }
    
    public void removeRequest(AudioRequest<Float> req) {
        sm.removeRequest(req);
    }
}

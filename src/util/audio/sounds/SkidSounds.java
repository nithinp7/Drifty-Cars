
package util.audio.sounds;

import util.audio.AudioRequest;
import util.audio.SoundManager;
import static util.Constants.*;

/**
 *
 * @author nithin
 */
public class SkidSounds {
    
    private SoundManager<SkidParam> sm;
    
    public void init() {
        sm = new SoundManager<>(CAR_SKID, 12, true, skidSoundComparator);
    }
    
    public void addRequest(AudioRequest<SkidParam> req) {
        sm.addRequest(req);
    }
    
    public void update() {
        sm.update();
    }
    
    public void removeRequest(AudioRequest<SkidParam> req) {
        sm.removeRequest(req);
    }
}

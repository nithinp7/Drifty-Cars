
package util.audio.sounds;

import static util.Constants.*;
import util.audio.SoundManager;

/**
 *
 * @author nithin
 */
public final class ExplosionSounds extends Sound<Float> {
    
    @Override
    public SoundManager<Float> createSoundManager() {
        SoundManager<Float> sm = new SoundManager<>(EXPLOSION, 8, false, distSoundComparator, Float.MAX_VALUE);
        sm.setBounds(0.9f, 1);
        return sm;
    }
}

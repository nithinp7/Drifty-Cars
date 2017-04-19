 
package util.audio.sounds;

import static util.Constants.*;
import util.audio.SoundManager;

/**
 *
 * @author nithin
 */
public final class SirenSounds extends Sound<Float> {
    
    @Override
    public SoundManager<Float> createSoundManager() {
        return new SoundManager<>(POLICE_SIREN, 5, true, distSoundComparator, Float.MAX_VALUE);
    }
}

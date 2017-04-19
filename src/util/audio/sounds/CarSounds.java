
package util.audio.sounds;

import util.audio.SoundManager;
import static util.Constants.*;

/**
 *
 * @author nithin
 */
public final class CarSounds extends Sound<Float> {
    
    @Override
    public SoundManager<Float> createSoundManager() {
        return new SoundManager<>(CAR_ENGINE, 8, true, distSoundComparator, Float.MAX_VALUE);
    }
}

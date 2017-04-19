
package util.audio.sounds;

import util.audio.SoundManager;
import static util.Constants.*;

/**
 *
 * @author nithin
 */
public class SkidSounds extends Sound<SkidParam> {
    
    @Override
    public SoundManager<SkidParam> createSoundManager() {
        return new SoundManager<>(CAR_SKID, 12, true, skidSoundComparator, new SkidParam(Float.MAX_VALUE, false));
    }
}


package util.audio.sounds;

import util.audio.AudioRequest;
import util.audio.SoundManager;

/**
 *
 * @author nithin
 * @param <T>
 */
public abstract class Sound<T> {
    
    private SoundManager<T> sm;
    
    public void init() {
        sm = createSoundManager();
    }
    
    protected abstract SoundManager<T> createSoundManager();
    
    public void update() {
        sm.update();
    }
    
    public void addRequest(AudioRequest<T> req) {
        sm.addRequest(req);
    }
    
    public void removeRequest(AudioRequest<T> req) {
        sm.removeRequest(req);
    }
}

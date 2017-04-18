
package util.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import static util.Constants.createSound;

/**
 *
 * @author nithin
 * @param <T> comparison type
 */
public final class SoundManager<T> {
    
    private final int tracks, type;
    private final boolean loop;
    
    private final ArrayList<SampleControls> sampleControls = new ArrayList<>();
    private final ArrayList<AudioRequest<T>> requests = new ArrayList<>();
    private final Comparator<AudioRequest<T>> priorityComparator;
    
    private final AudioRequest<T> clearReq = new AudioRequest<>(0, 1, 0, 1, null);
    
    public SoundManager(int type, int tracks, boolean loop, Comparator<AudioRequest<T>> priorityComparator) {
        for(int i=0; i<tracks; i++) sampleControls.add(createSound(type, loop));
        this.priorityComparator = priorityComparator;
        this.tracks = tracks;
        this.type = type;
        this.loop = loop;
    }
    
    public void setBounds(float start, float end) {
        sampleControls.forEach(sc -> sc.setLoopBounds(start, end));
    }
    
    public void update() {
        requests.removeAll(Collections.singleton(null));
        requests.sort(priorityComparator);
        for(int i=0; i<requests.size(); i++) {
            if(i<tracks) {
                AudioRequest<T> req = requests.get(i);
                SampleControls sc = sampleControls.get(i);
                if(loop) req.updateSound(sc, null);
                else if(!req.isPlaying()) req.playSound(sc, null);
            } else if(!loop) requests.remove(tracks);
        }
        
        if(!loop) for(int i=0, j=0; i<tracks && i-j<requests.size(); i++) if(sampleControls.get(i).isEnded()) requests.remove(i-j++);  
    }
    
    public void addRequest(AudioRequest<T> request) {
        requests.add(request);
    }
    
    public void removeRequest(AudioRequest<T> request) {
        int indexOf = requests.indexOf(request);
        if(indexOf<tracks) clearReq.playSound(sampleControls.get(indexOf), null);
        requests.remove(request);
    }
}


package util.audio;

import java.util.ArrayList;
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
    
    public SoundManager(int type, int tracks, boolean loop, Comparator<AudioRequest<T>> priorityComparator) {
        for(int i=0; i<tracks; i++) sampleControls.add(createSound(type, loop));
        this.priorityComparator = priorityComparator;
        this.tracks = tracks;
        this.type = type;
        this.loop = loop;
    }
    
    public void update() {
        requests.sort(priorityComparator);
        for(int i=0; i<requests.size(); i++) {
            if(i<tracks) {
                AudioRequest req = requests.get(i);
                SampleControls sc = sampleControls.get(i);
                if(loop) req.updateSound(sc, null);
                else if(!req.isPlaying()) req.playSound(sc, null);
            } else if(!loop) requests.remove(tracks);
        }
        
        if(!loop) for(int i=0, j=0; i<tracks && i-j<requests.size(); i++) if(sampleControls.get(i).isEnded()) requests.remove(i-j++);  
    }
    
    public void addRequest(AudioRequest request) {
        requests.add(request);
    }
    
    public void removeRequest(AudioRequest request) {
        int indexOf = requests.indexOf(request);
        if(indexOf < tracks) {
            SampleControls s = sampleControls.get(indexOf);
            s.stop();
        }
        requests.remove(request);
    }
}

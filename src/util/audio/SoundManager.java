
package util.audio;

import java.util.ArrayList;
import java.util.Comparator;
import static util.Constants.createSound;
import util.interfaces.Restartable;

/**
 *
 * @author nithin
 * @param <T> comparison type
 */
public final class SoundManager<T> implements Restartable {
    
    private final int tracks, type;
    private final boolean loop;
    
    private final ArrayList<SampleControls> sampleControls = new ArrayList<>();
    private final ArrayList<AudioRequest<T>> requests = new ArrayList<>();
    private final Comparator<AudioRequest<T>> priorityComparator;
    
    private final AudioRequest<T> clearReq;// = new AudioRequest<>(0, 1, 0, 1, null);
    
    private final T max;
    
    public SoundManager(int type, int tracks, boolean loop, Comparator<AudioRequest<T>> priorityComparator, T max) {
        clearReq = new AudioRequest<>(0, 1, 0, 1, max);
        
        this.max = max;
        
        for(int i=0; i<tracks; i++) {
            SampleControls sc = createSound(type, loop);
            clearReq.playSound(sc, max);
            sampleControls.add(sc);
        }
        this.priorityComparator = priorityComparator;
        this.tracks = tracks;
        this.type = type;
        this.loop = loop;
    }
    
    public void setBounds(float start, float end) {
        sampleControls.forEach(sc -> sc.setLoopBounds(start, end));
    }
    
    public void update() {
        //requests.removeAll(Collections.singleton(null));
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
        //TODO: Find actual solution
        int indexOf = requests.indexOf(request);
        if(indexOf>-1&&indexOf<tracks) clearReq.updateSound(sampleControls.get(indexOf), max);
        requests.remove(request);
    }
    
    @Override
    public void restart() {
        requests.clear();
        sampleControls.forEach(sc -> clearReq.playSound(sc, max));
    }
}

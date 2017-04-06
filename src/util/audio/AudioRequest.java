
package util.audio;

/**
 *
 * @author nithin
 * @param <T>
 */
public class AudioRequest<T> {
    
    private float gainValue, gainTime, pitchValue, pitchTime;
    private boolean playing = false;
    
    public T comparisonValue;

    public AudioRequest(float gainValue, float gainTime, float pitchValue, float pitchTime, T comparisonValue) {
        this.gainValue = gainValue;
        this.gainTime = gainTime;
        this.pitchValue = pitchValue;
        this.pitchTime = pitchTime;
        
        this.comparisonValue = comparisonValue;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public void playSound(SampleControls sc, T comparisonValue) {
        playing = true;
        updateSound(sc, comparisonValue);
        sc.setKillOnEnd(false);
        sc.replay();
    }
    
    public void updateSound(SampleControls sc, T comparisonValue) {
        if(pitchValue >= 0) sc.pitchGlide.setValue(pitchValue);
        if(pitchTime >= 0) sc.pitchGlide.setGlideTime(pitchTime);
        if(gainValue >= 0) sc.gainGlide.setValue(gainValue);
        if(gainTime >= 0) sc.gainGlide.setGlideTime(gainTime);
        
        if(comparisonValue != null) this.comparisonValue = comparisonValue;
    }
    
    public void setGainValue(float gainValue) {
        this.gainValue = gainValue;
    }

    public void setGainTime(float gainTime) {
        this.gainTime = gainTime;
    }

    public void setPitchValue(float pitchValue) {
        this.pitchValue = pitchValue;
    }

    public void setPitchTime(float pitchTime) {
        this.pitchTime = pitchTime;
    }
}

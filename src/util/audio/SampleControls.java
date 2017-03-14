
package util.audio;

import beads.Gain;
import beads.Glide;
import beads.SamplePlayer;

/**
 *
 * @author nithin
 */
public final class SampleControls {
    
    public final Gain volume;
    public final Glide gainGlide, pitchGlide;
    public final SamplePlayer player;
    
    public SampleControls(Gain vol, Glide gGlide, Glide pGlide, SamplePlayer sp) {
        volume = vol;
        gainGlide = gGlide;
        pitchGlide = pGlide;
        player = sp;
    }
}


package util.audio;

import beads.Bead;
import beads.Gain;
import beads.Glide;
import beads.SamplePlayer;
import static processing.core.PApplet.constrain;

/**
 *
 * @author nithin
 */
public final class SampleControls {
    
    public final Gain volume;
    public final Glide gainGlide, pitchGlide;
    
    private final SamplePlayer player;
    
    private boolean ended = false;
    
    private float start = 0, end = 1;
    
    public SampleControls(Gain vol, Glide gGlide, Glide pGlide, SamplePlayer sp) {
        volume = vol;
        gainGlide = gGlide;
        pitchGlide = pGlide;
        player = sp;
        
        player.setEndListener(new Bead() {
           @Override
           public void messageReceived(Bead msg) {
               ended = true;
           }
        });
    }
    
    public void setLoopBounds(float start, float end) {
        this.start = constrain(start, 0, 1);
        this.end = constrain(end, 0, 1);
    }
    
    public void stop() {
        
    }
    
    public boolean isEnded() {
        return ended;
    }
    
    public void replay() {
        ended = false;
        player.reset();
        player.start();
        player.pause(false);
        player.setLoopPointsFraction(start, end);
    }
    
    public void setKillOnEnd(boolean kill) {
        player.setKillOnEnd(kill);
    }
    
    public void kill() {
        player.kill();
    }
}


package util.audio;

import beads.Bead;
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
    
    private final SamplePlayer player;
    
    private boolean ended = false;
    
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
    
    public boolean isEnded() {
        return ended;
    }
    
    public void replay() {
        ended = false;
        player.reset();
        player.start();
    }
    
    public void stop() {
        player.reset();
        player.pause(true);
    }
    
    public void setKillOnEnd(boolean kill) {
        player.setKillOnEnd(kill);
    }
    
    public void kill() {
        player.kill();
    }
}

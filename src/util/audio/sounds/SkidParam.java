
package util.audio.sounds;

/**
 *
 * @author nithin
 */
public class SkidParam {
    public float distance;
    public boolean skidding;
    
    public SkidParam(float distance, boolean skidding) {
        this.distance = distance;
        this.skidding = skidding;
    }
    
    public void update(float distance, boolean skidding) {
        this.distance = distance;
        this.skidding = skidding;
    }
}

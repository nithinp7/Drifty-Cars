
package particles;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

/**
 *
 * @author nithin
 */
public abstract class FixedParticle extends Particle {
    
    private final Body parent;
    private final float rx, ry, rz;
    
    public FixedParticle(Body parent, float rx, float ry, float rz) {
        super(parent.getPosition(), 0);
        this.parent = parent;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }
    
    public FixedParticle(Body parent, Vec3 rPos) {
        this(parent, rPos.x, rPos.y, rPos.z);
    }
    
    @Override
    public void update() {
        Vec2 parentPos = parent.getPosition();
        float angle = parent.getAngle(), cos = cos(angle), sin = sin(angle);
        x = parentPos.x + rx*cos - ry*sin;
        y = parentPos.y + rx*sin + ry*cos;
        z = rz;
    }
}


package entities.surface;

import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import util.Texture;

/**
 *
 * @author Nithin
 */
public final class Asphalt {
    
    public final Body floor;
    public final Texture texture;
    
    public Asphalt(Vec3 loc, Vec3[] bounds) {
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(0, 0);
        bd.angularDamping = 1f;
        bd.linearDamping = 1f;
        
        floor = box2d.createBody(bd);
        
        texture = new Texture("./res/asphalt.jpg", new Vec2[] { new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1), new Vec2(0, 1) }, loc, bounds);
    }
}

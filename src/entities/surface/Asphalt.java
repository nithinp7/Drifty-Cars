
package entities.surface;

import static main.Game.box2d;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

/**
 *
 * @author Nithin
 */
public final class Asphalt {
    
    public final Body floor;
    
    public Asphalt() {
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(0, 0);
        bd.angularDamping = 1f;
        bd.linearDamping = 1f;
        
        floor = box2d.createBody(bd);
    }
}

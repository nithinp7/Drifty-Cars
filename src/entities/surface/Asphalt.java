
package entities.surface;

import static main.Game.box2d;
import static main.Main.c;
import static util.Constants.*;
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
    
    private final Texture[] textures;
    
    public Asphalt(Vec2 loc, Vec2 size, Vec2 tileSize) {
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.position.set(0, 0);
        bd.angularDamping = 1f;
        bd.linearDamping = 1f;
        
        floor = box2d.createBody(bd);
        
        textures = new Texture[(int)(size.x/tileSize.x*size.y/tileSize.y)];
        
        for(int i=0; i<size.x/tileSize.x; i++) {
            for(int j=0; j<size.y/tileSize.y; j++) {
                textures[(int)(i*size.y/tileSize.y+j)] = new Texture(ASPHALT_TEX, new Vec2[] { new Vec2(0, 0), new Vec2(1, 0), new Vec2(1, 1), new Vec2(0, 1) }, new Vec3(loc.x+i*tileSize.x, loc.y+j*tileSize.y, 0), new Vec3[]{ new Vec3(0, 0, 0), new Vec3(tileSize.x, 0, 0), new Vec3(tileSize.x, tileSize.y, 0), new Vec3(0, tileSize.y, 0) });
            }
        }
    }
    
    public void render() {
        for(Texture tex : textures) {
            c.noFill();
            c.noStroke();
            c.specular(3);
            //tex.render();
            c.specular(0);
        }
    }
}

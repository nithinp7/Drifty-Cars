
package particles;

import java.awt.Color;
import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.Body;
import processing.core.PGraphics;

/**
 *
 * @author nithin
 */
public class LightParticle extends FixedParticle {
    
    public final float radius, radius_pix;
    private final Color color;
    
    public LightParticle(Body parent, float rx, float ry, float rz, float radius, Color color) {
        super(parent, rx, ry, rz);
        
        this.radius = radius;
        radius_pix = box2d.scalarWorldToPixels(radius);
        
        this.color = color;
    }
    
    public LightParticle(Body parent, Vec3 relPos, float radius, Color color) {
        this(parent, relPos.x, relPos.y, relPos.z, radius, color);
    }
    
    @Override
    public void postRender(PGraphics g) {
        Vec2 pixPos = box2d.coordWorldToPixels(x, y);
        g.pushMatrix();
        g.pushStyle();
            g.noStroke();
            g.translate(pixPos.x, pixPos.y, box2d.scalarWorldToPixels(z));
            g.fill(color.getRGB(), color.getAlpha());
            g.emissive(color.getRGB());
            g.sphere(radius_pix*0.25f);
            g.fill(color.getRGB(), color.getAlpha()*3/4);
            g.sphere(radius_pix*0.5f);
            g.fill(color.getRGB(), color.getAlpha()/2);
            g.sphere(radius_pix*0.75f);
            g.fill(color.getRGB(), color.getAlpha()/4);
            g.sphere(radius_pix);
        g.popMatrix();
        g.popStyle();
    }
}


package particles;

import java.awt.Color;
import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import processing.core.PGraphics;
import static util.Constants.FPS;
import static util.Constants.TIMESTEP;
import util.interfaces.Disposable;

/**
 *
 * @author nithin
 */
public class SmokeParticle extends Particle {
    
    private boolean ended = true;

    private float radius;
    private float radiusIncrRate, fadeTime;
    private Color color;
    
    private float alphaMultiplier;
    
    private int fadeCounter = 0;
            
    public SmokeParticle() {
        super(0, 0, 0, 0);
    }
    
    public void set(float x, float y, float z, float radius, float radiusIncrRate, float fadeTime, Color color, float alphaMultiplier) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.radiusIncrRate = radiusIncrRate;
        this.fadeTime = fadeTime;
        this.color = color;
        
        this.alphaMultiplier = alphaMultiplier;
        
        fadeCounter = 0;
        ended = false;
    }
    
    public void set(float x, float y, float z, float radius, float radiusIncrRate, float fadeTime, Color color) {
        set(x, y, z, radius, radiusIncrRate, fadeTime, color, 1);
    }
    
    public void set(float x, float y, float radius, float radiusIncrRate, float fadeTime, Color color) {
        set(x, y, 0, radius, radiusIncrRate, fadeTime, color);
    }
    
    public void set(float x, float y, float radius, float radiusIncrRate, float fadeTime, Color color, float alphaMultiplier) {
        set(x, y, 0, radius, radiusIncrRate, fadeTime, color, alphaMultiplier);
    }
    
    @Override
    public void update() {
        if(ended) return;
        super.update();
        radius += radiusIncrRate*TIMESTEP;
        if(fadeCounter++ > fadeTime*FPS) end();
    }

    @Override
    public void postRender(PGraphics g) {
        if(ended) return;
        Vec2 pixPos = box2d.coordWorldToPixels(x, y);
        float radius_pix = box2d.scalarWorldToPixels(radius);
        int currentAlpha = (int)(color.getAlpha()*(1-fadeCounter*TIMESTEP/fadeTime)*alphaMultiplier);
        g.pushMatrix();
        g.pushStyle();
            g.noStroke();
            g.translate(pixPos.x, pixPos.y, box2d.scalarWorldToPixels(z));
            g.fill(color.getRGB(), currentAlpha);
            g.emissive(color.getRGB());
            g.sphere(radius_pix*0.25f);
            g.fill(color.getRGB(), currentAlpha*3/4);
            g.sphere(radius_pix*0.5f);
            g.fill(color.getRGB(), currentAlpha/2);
            g.sphere(radius_pix*0.75f);
            g.fill(color.getRGB(), currentAlpha/4);
            g.sphere(radius_pix);
        g.popMatrix();
        g.popStyle();
    }

    public boolean ended() {
        return ended;
    }
    
    public void end() {
        ended = true;
    }
}

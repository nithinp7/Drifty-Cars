
package entities.building;

import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import processing.core.PGraphics;
import processing.core.PShape;
import static util.geometry.Models.createBuilding;
import util.interfaces.Disposable;
import util.interfaces.Drawable;

/**
 *
 * @author admin
 */
public final class Building implements Drawable, Disposable {
    
    private final PShape model;
    private boolean dead = false;
    
    private final Block block;
    private final float x, y, theta;
    
    public Building(float x, float y, float theta, float l, float w, float h) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        block = new Block(x, y, theta, l, w, h, true);
        model = createBuilding(block.l_pixels, block.w_pixels, block.h_pixels);
    }
    
    @Override
    public void render(PGraphics g) {
        Vec2 pos = box2d.coordWorldToPixels(x, y);
        g.pushMatrix();
            g.translate(pos.x, pos.y);
            g.rotate(-theta);
            g.shape(model);
        g.popMatrix();
    }
    
    @Override
    public void dispose() {
        block.dispose();
        dead = true;
    }
    
    @Override
    public boolean isDead() {
        return dead;
    }
}

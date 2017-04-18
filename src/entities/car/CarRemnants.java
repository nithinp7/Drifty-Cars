
package entities.car;

import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import processing.core.PGraphics;
import processing.core.PShape;
import static util.Constants.FPS;
import static util.geometry.Models.createVehicleModel;
import util.interfaces.Disposable;
import util.interfaces.Drawable;

/**
 *
 * @author nithin
 */
public class CarRemnants implements Disposable, Drawable {
    
    private final PShape model;
    private final int modelType;
    
    private final float lifeTime;
    
    private final Axle frontAxle, rearAxle;
    
    private boolean dead = false;
    
    private final Body chasis;
    
    private int counter = 0;
    
    public CarRemnants(Body chasis, Axle frontAxle, Axle rearAxle, int modelType, float l_pixels, float w_pixels, float h_pixels, float lifeTime) {
        this.chasis = chasis;
        this.frontAxle = frontAxle;
        this.rearAxle = rearAxle;
        this.modelType = modelType;
        
        this.lifeTime = lifeTime;
        
        model = createVehicleModel(modelType, l_pixels, w_pixels, h_pixels, true);
    }
    
    public void update() {
        frontAxle.update(true);
        rearAxle.update(true);
        
        if(counter++>=lifeTime*FPS) dispose();
    }
    
    @Override
    public void dispose() {
        frontAxle.dispose();
        rearAxle.dispose();
        box2d.world.destroyBody(chasis);
        dead = true;
    }
    
    @Override
    public void render(PGraphics g) {
        Vec2 pos = box2d.coordWorldToPixels(chasis.getPosition());
        g.pushMatrix();
            g.translate(pos.x, pos.y);
            g.rotate(-chasis.getAngle());
            g.shape(model);
        g.popMatrix();
    }
    
    @Override
    public boolean isDead() {
        return dead;
    }
}

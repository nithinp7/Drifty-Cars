
package entities.car.ai;

import ai.PID;
import entities.car.AI_Car;
import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.constrain;
import static processing.core.PConstants.*;
import processing.core.PGraphics;
import processing.core.PShape;
import static util.geometry.Shapes.createBox;

/**
 *
 * @author nithin
 */
public final class Pursuer_AI extends AI_Car {
    
    private final PShape copCar;

    public Pursuer_AI(float x, float y, float theta, float l, float w, float h, PID steeringControl) {
        super(x, y, theta, l, w, h, steeringControl);
        
        copCar = c.createShape(GROUP);
        
        copCar.translate(0, 0, h_pixels/2);
        PShape body = c.createShape();
  
        body.translate(0, 0, 0);
        body.beginShape(QUADS);

        body.fill(20);
        body.noStroke();

        createBox(body, l_pixels, w_pixels, h_pixels);

        body.endShape();

        copCar.addChild(body);

        PShape body2 = c.createShape();

        body2.translate(-0.1f*l_pixels, 0, 0.8f*h_pixels);
        body2.beginShape(QUADS);

        body2.fill(170);
        body2.noStroke();

        createBox(body2, 0.5f*l_pixels, 0.85f*w_pixels, 0.8f*h_pixels);

        body2.endShape();

        copCar.addChild(body2);

        PShape body3 = c.createShape();

        body3.translate(0, 0.2f*w_pixels, h_pixels);
        body3.beginShape(QUADS);

        body3.fill(255, 0, 0);
        body3.noStroke();

        createBox(body3, 0.1f*l_pixels, 0.4f*w_pixels, 0.6f*h_pixels);

        body3.endShape();

        copCar.addChild(body3);

        PShape body4 = c.createShape();

        body4.translate(0, -0.2f*w_pixels, h_pixels);
        body4.beginShape(QUADS);

        body4.fill(0, 0, 255);
        body4.noStroke();

        createBox(body4, 0.1f*l_pixels, 0.4f*w_pixels, 0.6f*h_pixels);

        body4.endShape();

        copCar.addChild(body4);
    }

    @Override
    protected float getThrottle(float recommendedThrottle) {
        return !reverse ? constrain(2.8f*recommendedThrottle+0.6f-getSlideSpeed()*0.2f, 0, 8f) : 1f;
    }
    
    @Override
    public void update() {
        Vec2 pos = chasis.getPosition(), target = getCameraTarget(), targetVel = getCameraTargetVelocity();
        
        float dist = pos.sub(target).length();
        checkFront = dist>30;
        
        if(dist<30) target.addLocal(targetVel.mul((1-dist/30)*0.5f));
        
        path.removeTemporarySegment(currentSegment);
        
        currentSegment = path.createTemporarySegment(pos, target, true);//path.createTemporarySegment(pos);
        
        super.update();
    }
    
    @Override
    protected void renderAiCar(PGraphics g) {
        Vec2 pos = box2d.coordWorldToPixels(chasis.getPosition());
        g.pushMatrix();
            g.translate(pos.x, pos.y);
            g.rotate(-chasis.getAngle());
            g.shape(copCar);
        g.popMatrix();
    }
}

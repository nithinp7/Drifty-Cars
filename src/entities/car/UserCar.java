
package entities.car;

import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import static processing.core.PApplet.*;
import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import particles.explosions.Explosion;
import processing.core.PGraphics;
import static util.input.Input.*;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class UserCar extends Car {
    
    private static final float TERM_SPEED = 40f,
                               DRAG_CONST = MAX_CAR_THRUST*1.4f / TERM_SPEED / TERM_SPEED; 
    
    private float targetSteerAngle = 0;
    
    private final ArrayList<Explosion> explosions = new ArrayList<>();
    
    private long score = 0;
    
    public UserCar(float x, float y, float theta, float l, float w, float h) {
        super(x, y, theta, l, w, h, DRAG_CONST, MODEL_RAND_CAR);
        setImpactResistance(0.25f);
    }
    
    public UserCar(Vec2 pos, float theta, float l, float w, float h) {
        this(pos.x, pos.y, theta, l, w, h);
    }
    
    @Override
    public void update() {
        score++;
        processInput();
        explosions.forEach(Explosion::update);
        explosions.removeIf(Explosion::isDead);
        super.update();
    }
    
    private void processInput() {
        boolean[] inputs = consumeInput(new int[]{VK_UP, VK_DOWN});//, VK_E});
        brake = isMousePressed(LEFT);
        
        if(inputs[0]) throttle += 0.1f;
        if(inputs[1]) throttle -= 0.1f;
        //if(inputs[2]) createExplosion();
        
        reverse = isKeyPressed(VK_R) || isMousePressed(RIGHT);
        float speed = getForwardSpeed();
        
        throttle -= 0.05f*consumeMouseWheel();
        throttle = constrain(throttle, 0, 1.4f);
        
        Vec2 target = coordPixelsToWorld(new Vec2(c.mouseX, c.mouseY)),
             dir = target.sub(frontAxle.axle.getWorldCenter());
        
        float cAng = chasis.getAngle();
        targetSteerAngle = atan2(dir.x*sin(-cAng) + dir.y*cos(-cAng), dir.x * cos(-cAng) - dir.y*sin(-cAng));
        
        targetSteerAngle = constrain(targetSteerAngle, frontAxle.chasisConnector.getLowerLimit(), frontAxle.chasisConnector.getUpperLimit());
        //if(abs(targetSteerAngle) < PI/10) targetSteerAngle = 0;
        
        float theta = frontAxle.axle.getAngle()-chasis.getAngle();
        
        turn = constrain(targetSteerAngle - theta, -1, 1);
    }
    
    public float getThrottlePercent() {
        return 100f*throttle/1.4f;
    }
    
    public long getScore() {
        return score;
    }
    
    public void createExplosion() {
        explosions.add(new Explosion(chasis, 1, 40, 40, 0.4f, 3));
    }
    
    @Override
    public CarRemnants getRemnants() {
        CarRemnants cr = new CarRemnants(chasis, frontAxle, rearAxle, modelType, l_pixels, w_pixels, h_pixels);//super.getRemnants();
        userRemnants = cr;
        return cr;
    }
    
    @Override
    public void postRender(PGraphics g) {
        super.postRender(g);
        explosions.forEach(exp -> exp.postRender(g));
    }
}

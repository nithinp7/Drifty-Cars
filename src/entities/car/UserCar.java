
package entities.car;

import static java.awt.event.KeyEvent.*;
import static main.Main.c;
import static processing.core.PApplet.*;
import static main.Game.*;
import org.jbox2d.common.Vec2;
import static util.input.Input.*;

/**
 *
 * @author Nithin
 */
public final class UserCar extends Car {
    
    public float targetSteerAngle = 0;
    
    public Vec2 dir = new Vec2(0, 0);
    
    public UserCar(float x, float y, float theta, float l, float w, float h) {
        super(x, y, 0, l, w, h);
    }
    
    public UserCar(Vec2 pos, float theta, float l, float w, float h) {
        this(pos.x, pos.y, theta, l, w, h);
    }
    
    @Override
    public void update() {
        processInput();
        super.update();
    }
    
    private void processInput() {
        
        boolean[] inputs = consumeInput(new int[]{VK_UP, VK_DOWN});
        brake = isMousePressed(LEFT);
        
        //System.out.println(brake);
        
        if(inputs[0]) throttle += 0.1f;
        if(inputs[1]) throttle -= 0.1f;
        
        reverse = isKeyPressed(VK_R) || isMousePressed(RIGHT);
        
        throttle -= 0.05f*consumeMouseWheel();
        throttle = constrain(throttle, 0, 1);
        
        Vec2 target = coordPixelsToWorld(new Vec2(c.mouseX, c.mouseY));
        dir = target.sub(frontAxle.axle.getWorldCenter());
        
        float cAng = chasis.getAngle();
        targetSteerAngle = atan2(dir.x*sin(-cAng) + dir.y*cos(-cAng), dir.x * cos(-cAng) - dir.y*sin(-cAng));
        
        targetSteerAngle = constrain(targetSteerAngle, frontAxle.chasisConnector.getLowerLimit(), frontAxle.chasisConnector.getUpperLimit());
        //if(abs(targetSteerAngle) < PI/10) targetSteerAngle = 0;
        
        float theta = frontAxle.axle.getAngle()-chasis.getAngle();
        
        turn = constrain(targetSteerAngle - theta, -1, 1);
    }
    
    
}

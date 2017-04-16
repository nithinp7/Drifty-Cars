
package particles;

import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import static util.Constants.TIMESTEP;
import util.interfaces.*;

/**
 *
 * @author nithin
 */
public abstract class Particle implements PostDraw {
    
    protected float x, y, z, vx = 0, vy = 0, vz;
    
    public final float m;
    
    public Particle(float x, float y, float z, float m) {
        this.x = x;
        this.y = y;
        this.z = z;
        
        this.m = m;
    }
    
    public Particle(Vec3 pos, float m) {
        this(pos.x, pos.y, pos.z, m);
    }
    
    public Particle(Vec2 pos, float m) {
        this(pos.x, pos.y, 0, m);
    }
    
    public void update() {
        x += vx*TIMESTEP;
        y += vy*TIMESTEP;
        z += vz*TIMESTEP;
    }
    
    public float x() {
        return x;
    }
    
    public float y() {
        return y;
    }
    
    public float z() {
        return z;
    }
    
    public Vec3 getPosition() {
        return new Vec3(x, y, z);
    }
    
    public float vx() {
        return vx;
    }
    
    public float vy() {
        return vy;
    }
    
    public float vz() {
        return vz;
    }
    
    public Vec3 getVelocity() {
        return new Vec3(vx, vy, vz);
    }
    
    public void applyImpulse(Vec3 impulse) {
        vx += impulse.x/m;
        vy += impulse.y/m;
        vz += impulse.z/m;
    }
    
    public void applyImpulse(float ix, float iy, float iz) {
        vx += ix/m;
        vy += iy/m;
        vz += iz/m;
    }
    
    public void applyImpulse(Vec2 impulse) {
        vx += impulse.x/m;
        vy += impulse.y/m;
    }
    
    public void applyImpulse(float ix, float iy) {
        vx += ix/m;
        vy += iy/m;
    }
}

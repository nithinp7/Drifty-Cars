
package util.geometry;

import static main.Main.c;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PConstants.PI;
import static processing.core.PConstants.TRIANGLES;
import processing.core.PShape;

/**
 *
 * @author Nithin
 */
public final class Shapes {
    
    public static PShape createWheel(float radius, float thickness) {
        PShape wheel = c.createShape();
        wheel.beginShape(TRIANGLES);
        
        wheel.noStroke();
        wheel.fill(10, 10, 10);
        
        float angleStep = PI/10;
        for(int i=0; i<20; i++) {
            float angle = angleStep*i, angle2 = angleStep*(i+1), cos = cos(angle), sin = sin(angle), cos2 = cos(angle2), sin2 = sin(angle2);
            
            wheel.vertex(0, thickness/2, 0);
            wheel.vertex(radius*cos, thickness/2, radius*sin);
            wheel.vertex(radius*cos2, thickness/2, radius*sin2);
            
            wheel.vertex(0, -thickness/2, 0);
            wheel.vertex(radius*cos, -thickness/2, radius*sin);
            wheel.vertex(radius*cos2, -thickness/2, radius*sin2);
            
            wheel.vertex(radius*cos, -thickness/2, radius*sin);
            wheel.vertex(radius*cos2, -thickness/2, radius*sin2);
            wheel.vertex(radius*cos, thickness/2, radius*sin);
            
            wheel.vertex(radius*cos, thickness/2, radius*sin);
            wheel.vertex(radius*cos2, thickness/2, radius*sin2);
            wheel.vertex(radius*cos, -thickness/2, radius*sin);
        }
        
        wheel.endShape();
        
        return wheel;
    }
    
    public static void createBox(PShape shape, float l, float w, float h) {
        
        l/=2;
        w/=2;
        h/=2;
        
        shape.vertex(-l, -w, -h);
        shape.vertex(l, -w, -h);
        shape.vertex(l, w, -h);
        shape.vertex(-l, w, -h);
        
        shape.vertex(-l, -w, -h);
        shape.vertex(-l, w, -h);
        shape.vertex(-l, w, h);
        shape.vertex(-l, -w, h);
        
        shape.vertex(-l, -w, -h);
        shape.vertex(l, -w, -h);
        shape.vertex(l, -w, h);
        shape.vertex(-l, -w, h);
        
        shape.vertex(-l, -w, h);
        shape.vertex(l, -w, h);
        shape.vertex(l, w, h);
        shape.vertex(-l, w, h);
        
        shape.vertex(l, -w, -h);
        shape.vertex(l, w, -h);
        shape.vertex(l, w, h);
        shape.vertex(l, -w, h);
        
        shape.vertex(-l, w, -h);
        shape.vertex(l, w, -h);
        shape.vertex(l, w, h);
        shape.vertex(-l, w, h);
    }
}


package util.geometry;

import java.util.Random;
import static main.Main.c;
import static processing.core.PApplet.*;
import static processing.core.PConstants.QUADS;
import processing.core.PImage;
import processing.core.PShape;
import static util.geometry.Models.*;

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
    
    public static void createBuildingBox(PShape parent, float l, float w, float h, PImage tex) {
        
        PShape shape = c.createShape(), core = c.createShape();
        
        core.beginShape(QUADS);
        
        core.fill(h>40?getRandomBuildingColor().getRGB():getRandomBuildingColorLight().getRGB());
        createBox(core, l-0.05f, w-0.05f, h-0.05f);
        
        core.endShape();
        
        parent.addChild(core);
        
        PShape vent = c.createShape();
        vent.translate(c.random(-l/4, l/4), c.random(-w/4, w/4), h/2);
        
        Random r = new Random();
        vent.beginShape(QUADS);
        vent.fill(100, 100, 100);
        createBox(vent, r.nextBoolean()?3:6, r.nextBoolean()?3:6, 2.5f);
        vent.endShape();
        
        parent.addChild(vent);
        
        shape.beginShape(QUADS);
        
        shape.textureMode(NORMAL);
        shape.texture(tex);
        
        shape.stroke(0, 0);
        
        l/=2;
        w/=2;
        h/=2;
        
        int l_reps = (int)(l/7), h_reps = (int)(h/7);
        
        for(int i=0; i<l_reps; i++)
            for(int j=0; j<h_reps; j++) {
                float l_i = 2*l*i/l_reps-l, l_i1 = l_i + 2*l/l_reps,
                      w_i = 2*w*i/l_reps-w, w_i1 = w_i + 2*w/l_reps,
                      h_j = 2*h*j/h_reps-h, h_j1 = h_j + 2*h/h_reps;
                shape.vertex(-l, w_i, h_j, 0, 0);
                shape.vertex(-l, w_i1, h_j, 1, 0);
                shape.vertex(-l, w_i1, h_j1, 1, 1);
                shape.vertex(-l, w_i, h_j1, 0, 1);

                shape.vertex(l_i, -w, h_j, 0, 0);
                shape.vertex(l_i1, -w, h_j, 1, 0);
                shape.vertex(l_i1, -w, h_j1, 1, 1);
                shape.vertex(l_i, -w, h_j1, 0, 1);

                shape.vertex(l, w_i, h_j, 0, 0);
                shape.vertex(l, w_i1, h_j, 1, 0);
                shape.vertex(l, w_i1, h_j1, 1, 1);
                shape.vertex(l, w_i, h_j1, 0, 1);

                shape.vertex(l_i, w, h_j, 0, 0);
                shape.vertex(l_i1, w, h_j, 1, 0);
                shape.vertex(l_i1, w, h_j1, 1, 1);
                shape.vertex(l_i, w, h_j1, 0, 1);
            }
        
        shape.endShape();
        
        parent.addChild(shape);
    }
}

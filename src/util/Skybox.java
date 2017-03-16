
package util;

import static main.Game.getCamAngle;
import static main.Main.c;
import processing.core.PGraphics;
import processing.core.PImage;
import static processing.core.PApplet.*;
import static util.Constants.HEIGHT;
import static util.Constants.WIDTH;
import util.interfaces.Drawable;

/**
 *
 * @author Nithin
 */
public final class Skybox implements Drawable {
    
    private final PImage[] imgs;
    
    public Skybox(PImage[] imgs) {
        if(imgs.length != 6) {
            System.err.println("Skybox creation failed");
            c.exit();
        }
        
        this.imgs = imgs;
    }

    @Override
    public void render(PGraphics g) {
        int prevMode = g.imageMode;
        
        g.imageMode(CENTER);
        
        g.hint(DISABLE_DEPTH_MASK);
        g.pushMatrix();
            //g.translate(0, 0, WIDTH/2);
            //g.translate(WIDTH/2, HEIGHT/2, WIDTH/2);
            g.rotateX(-PI/2);
            //g.rotateZ(-getCamAngle());
            
//            g.rotateZ(PI/2);
//            g.rotateX(PI/2);
            
            //g.rotateY(c.millis()/1710.f);
            //g.rotateZ(c.millis()/1310.f);

            for(int i=0; i<4; i++) {
                g.pushMatrix();
                    g.translate(0, 0, -WIDTH/2);
                    g.image(imgs[i], 0, 0, WIDTH, WIDTH);
                g.popMatrix();

                g.rotateY(PI/2);
            }

            g.rotateX(-PI/2);

            g.pushMatrix();
                g.translate(0, 0, -WIDTH/2);
                g.image(imgs[4], 0, 0, WIDTH, WIDTH);
            g.popMatrix();

            g.rotateX(-PI);

            g.pushMatrix();
                g.translate(0, 0, -WIDTH/2);
                g.image(imgs[5], 0, 0, WIDTH, WIDTH);
            g.popMatrix();

        g.popMatrix();
        g.hint(ENABLE_DEPTH_MASK);
        
        g.imageMode(prevMode);
    }
}

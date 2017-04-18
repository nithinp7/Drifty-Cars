
package util.geometry;

import java.awt.Color;
import static main.Main.c;
import static processing.core.PConstants.*;
import processing.core.PShape;
import static util.Constants.*;
import static util.geometry.Shapes.createBox;

/**
 *
 * @author nithin
 */
public final class Models {
    
    public static PShape createVehicleModel(int model, float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        switch(model) {
            case MODEL_POLICE_CAR: return createCopCar(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_CIV_CAR:
            case MODEL_CIV_JEEP: return createJeep(l_pixels, w_pixels, h_pixels, destroyed);
            default: return null;
        }
    }
    
    public static PShape createCopCar(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        PShape copCar = c.createShape(GROUP);
        
        copCar.translate(0, 0, h_pixels/2);
        PShape body = c.createShape();
  
        body.translate(0, 0, 0);
        body.beginShape(QUADS);

        body.fill(destroyed?10f:20f);
        body.noStroke();

        createBox(body, l_pixels, w_pixels, h_pixels);

        body.endShape();

        copCar.addChild(body);

        PShape body2 = c.createShape();

        body2.translate(-0.1f*l_pixels, 0, 0.8f*h_pixels);
        body2.beginShape(QUADS);

        body2.fill(destroyed?50f:170f);
        body2.noStroke();

        createBox(body2, 0.5f*l_pixels, 0.85f*w_pixels, 0.8f*h_pixels);

        body2.endShape();

        copCar.addChild(body2);

        PShape body3 = c.createShape();

        body3.translate(0, 0.2f*w_pixels, h_pixels);
        body3.beginShape(QUADS);

        if(!destroyed) body3.emissive(255, 0, 0);
        body3.fill(destroyed?100:255, 0, 0);
        body3.noStroke();

        createBox(body3, 0.1f*l_pixels, 0.4f*w_pixels, 0.6f*h_pixels);

        body3.endShape();

        copCar.addChild(body3);

        PShape body4 = c.createShape();
        
        body4.translate(0, -0.2f*w_pixels, h_pixels);
        body4.beginShape(QUADS);

        if(!destroyed) body4.emissive(0, 0, 255);
        body4.fill(0, 0, destroyed?100:255);
        body4.noStroke();

        createBox(body4, 0.1f*l_pixels, 0.4f*w_pixels, 0.6f*h_pixels);

        body4.endShape();

        copCar.addChild(body4);
        
        return copCar;
    }
    
    public static PShape createJeep(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        Color color = getRandomCarColor();
        
        PShape car = c.createShape(GROUP);
        
        car.translate(0, 0, 3*h_pixels/2);
        
        PShape body = c.createShape();
        body.beginShape(QUADS);
        
        if(destroyed) body.fill(10);
        else body.fill(color.getRGB());
        body.noStroke();
        
        createBox(body, l_pixels, 1.4f*w_pixels, h_pixels);
        
        body.endShape();
        
        car.addChild(body);
        
        PShape body2 = c.createShape();
        body2.translate(-l_pixels/7f, 0, h_pixels*0.7f);
        body2.beginShape(QUADS);
        
        body2.fill(destroyed?10f:55f);
        body2.noStroke();
        
        createBox(body2, l_pixels*0.7f, w_pixels*1.3f, h_pixels*2.4f);
        
        body2.endShape();
        
        car.addChild(body2);
        
        return car;
    }
    
    private static Color getRandomCarColor() {
        return new Color((int)c.random(150, 250), (int)c.random(150, 250), (int)c.random(150, 250));
    }
}


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
    
    public static PShape createBuilding(float l_pixels, float w_pixels, float h_pixels) {
        PShape building = c.createShape(GROUP), body = c.createShape(), body1 = c.createShape(), body2 = c.createShape(), body3 = c.createShape();
        
        float randHeight = c.random(h_pixels/2, h_pixels);
        body.translate(-l_pixels/4, -w_pixels/4, randHeight/2);
        body.beginShape(QUADS);
        body.fill(getRandomBuildingColor().getRGB());
        createBox(body, l_pixels/2, w_pixels/2, randHeight);
        body.endShape();
        
        building.addChild(body);
        
        randHeight = c.random(h_pixels/2, h_pixels);
        body1.translate(l_pixels/4, -w_pixels/4, randHeight/2);
        body1.beginShape(QUADS);
        body1.fill(getRandomBuildingColor().getRGB());
        createBox(body1, l_pixels/2, w_pixels/2, randHeight);
        body1.endShape();
        
        building.addChild(body1);
        
        randHeight = c.random(h_pixels/2, h_pixels);
        body2.translate(l_pixels/4, w_pixels/4, randHeight/2);
        body2.beginShape(QUADS);
        body2.fill(getRandomBuildingColor().getRGB());
        createBox(body2, l_pixels/2, w_pixels/2, randHeight);
        body2.endShape();
        
        building.addChild(body2);
        
        randHeight = c.random(h_pixels/2, h_pixels);
        body3.translate(-l_pixels/4, w_pixels/4, randHeight/2);
        body3.beginShape(QUADS);
        body3.fill(getRandomBuildingColor().getRGB());
        createBox(body3, l_pixels/2, w_pixels/2, randHeight);
        body3.endShape();
        
        building.addChild(body3);
        
        return building;
    }
    
    public static PShape createVehicleModel(int model, float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        switch(model) {
            case MODEL_POLICE_CAR: return createCopCar(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_CIV_PICKUP: return createCivPickup(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_CIV_JEEP: return createCivJeep(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_CIV_CONV: return createCivConv(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_CIV_CAR: return createCivCar(l_pixels, w_pixels, h_pixels, destroyed);
            case MODEL_RAND_CIV: return createVehicleModel((int)c.random(2, 6), l_pixels, w_pixels, h_pixels, destroyed);
            default: return null;
        }
    }
    
    private static PShape createCopCar(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
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
        
        PShape body5 = c.createShape();
        
        body5.translate(0.15f*l_pixels, 0, 0.8f*h_pixels);
        body5.beginShape(QUADS);
        
        body5.fill(5);
        body5.noStroke();
        
        createBox(body5, l_pixels*0.05f, w_pixels*0.82f, 0.78f*h_pixels);
        
        body5.endShape();
        
        copCar.addChild(body5);
        
        return copCar;
    }
    
    private static PShape createCivJeep(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
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
        body2.translate(-l_pixels/7f, 0, h_pixels*0.8f);
        body2.beginShape(QUADS);
        
        body2.fill(destroyed?10f:55f);
        body2.noStroke();
        
        createBox(body2, l_pixels*0.7f, w_pixels*1.3f, h_pixels*2.2f);
        
        body2.endShape();
        
        car.addChild(body2);
        
        PShape body3 = c.createShape();
        body3.translate(l_pixels*(0.35f-1/7f), 0, h_pixels*0.7f);
        body3.beginShape(QUADS);
        
        body3.fill(5f);
        body3.noStroke();
        
        createBox(body3, l_pixels*0.035f, w_pixels*1.2f, h_pixels*2.2f);
        
        body3.endShape();
        
        car.addChild(body3);
        
        return car;
    }
    
    private static PShape createCivCar(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
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
        body2.translate(-l_pixels*0.05f, 0, h_pixels*0.7f);
        body2.beginShape(QUADS);
        
        if(destroyed) body2.fill(10);
        else body2.fill(color.getRGB());
        body2.noStroke();
        
        createBox(body2, l_pixels*0.35f, w_pixels*1.3f, h_pixels*2.4f);
        
        body2.endShape();
        
        car.addChild(body2);
        
        PShape body3 = c.createShape();
        body3.translate(l_pixels*(0.125f), 0, h_pixels*0.7f);
        body3.beginShape(QUADS);
        
        body3.fill(5f);
        body3.noStroke();
        
        createBox(body3, l_pixels*0.035f, w_pixels*1.2f, h_pixels*2.2f);
        
        body3.endShape();
        
        car.addChild(body3);
        
        return car;
    }
    
    private static PShape createCivConv(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        Color color = getRandomCarColor();
        
        PShape car = c.createShape(GROUP);
        
        car.translate(0, 0, 3*h_pixels/2);
        
        PShape body = c.createShape();
        body.translate(l_pixels/3, 0, h_pixels/2);
        body.beginShape(QUADS);
        
        if(destroyed) body.fill(10);
        else body.fill(color.getRGB());
        body.noStroke();
        
        createBox(body, l_pixels/3, 1.4f*w_pixels, h_pixels);
        
        body.endShape();
        
        car.addChild(body);
        
        PShape body2 = c.createShape();
        body2.translate(l_pixels/5.55f, 0, h_pixels*0.9f);
        body2.beginShape(QUADS);
        
        body2.fill(5f);
        body2.noStroke();
        
        createBox(body2, l_pixels*0.025f, w_pixels*1.3f, h_pixels*2.4f);
        
        body2.endShape();
        
        car.addChild(body2);
        
        PShape body3 = c.createShape();
        body3.beginShape(QUADS);
        
        if(destroyed) body3.fill(10);
        else body3.fill(color.getRGB());
        body3.noStroke();
        
        createBox(body3, l_pixels, 1.4f*w_pixels, h_pixels);
        
        body3.endShape();
        car.addChild(body3);
        
        return car;
    }
    
    private static PShape createCivPickup(float l_pixels, float w_pixels, float h_pixels, boolean destroyed) {
        Color color = getRandomPickupColor();
        
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
        body2.translate(l_pixels/6f, 0, h_pixels*0.7f);
        body2.beginShape(QUADS);
        
        if(destroyed) body2.fill(10);
        else body2.fill(color.getRGB());
        body2.noStroke();
        
        createBox(body2, l_pixels*0.15f, w_pixels*1.3f, h_pixels*1.4f);
        
        body2.endShape();
        
        car.addChild(body2);
        
        PShape body3 = c.createShape();
        body3.translate(l_pixels*(1f/6+0.075f), 0, h_pixels*0.7f);
        body3.beginShape(QUADS);
        
        body3.fill(5f);
        body3.noStroke();
        
        createBox(body3, l_pixels*0.035f, w_pixels*1.2f, h_pixels*1.2f);
        
        body3.endShape();
        
        car.addChild(body3);
        
        PShape body4 = c.createShape();
        body4.translate(-l_pixels*0.23f, 0, h_pixels*0.5f);
        body4.beginShape(QUADS);
        
        body4.fill(destroyed?10f:35f);
        body4.noStroke();
        
        createBox(body4, l_pixels*0.45f, w_pixels*1.2f, h_pixels*0.05f);
        
        body4.endShape();
        
        car.addChild(body4);
        return car;
    }
    
    private static Color getRandomCarColor() {
        return new Color((int)c.random(150, 250), (int)c.random(150, 250), (int)c.random(150, 250));
    }
    
    private static Color getRandomSportsCarColor() {
        return new Color((int)c.random(150, 200), (int)c.random(20, 60), (int)c.random(100, 120));
    }
    
    private static Color getRandomPickupColor() {
        return new Color((int)c.random(40, 90), (int)c.random(30, 80), (int)c.random(20, 75));
    }
    
    private static Color getRandomBuildingColor() {
        return new Color((int)c.random(100, 120), (int)c.random(90, 110), (int)c.random(80, 95));
    }
}

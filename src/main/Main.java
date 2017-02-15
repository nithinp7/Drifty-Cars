
package main;

import processing.core.PApplet;
import static main.Game.*;
import processing.event.KeyEvent;
import static util.Constants.*;
import util.input.Input;

/**
 *
 * @author Nithin
 */
public final class Main extends PApplet {
    
    public static PApplet c /*for context*/;
    
    public static void main(String[] args) {
        PApplet.main("main.Main");
    }
    
    @Override
    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }
    
    @Override
    public void setup() {
        frameRate(FPS);
        c = this;
        init();
    }
    
    @Override
    public void draw() {
        tick();
        render();
    }
    
    @Override
    public void keyPressed(KeyEvent ke) {
        Input.keyPressed(ke);
    }
    
    @Override 
    public void keyReleased(KeyEvent ke) {
        Input.keyReleased(ke);
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
        Input.keyTyped(ke);
    }
}

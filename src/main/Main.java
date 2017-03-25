
package main;

import processing.core.PApplet;
import static main.Game.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import static util.Constants.*;
import util.input.Input;

/**
 *
 * @author Nithin
 */
public final class Main extends PApplet {
    
    public static PApplet c /*for context*/;
    public static boolean initialized = false;
    
    public static void main(String[] args) {
        PApplet.main("main.Main");
    }
    
    @Override
    public void settings() {
        //fullScreen(P3D);
        size(WIDTH, HEIGHT, P3D);
    }
    
    @Override
    public void setup() {
        frameRate(FPS);
        c = this;
        WIDTH = width;
        HEIGHT = height;
        
        new Thread(() -> init()).start();
        
        System.out.println(WIDTH + ", " + HEIGHT);
    }
    
    @Override
    public void draw() {
        if(!initialized) return;
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
    
    @Override
    public void mousePressed(MouseEvent me) {
        Input.mousePressed(me);
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {
        Input.mouseReleased(me);
    }
    
    @Override
    public void mouseWheel(MouseEvent me) {
        Input.mouseWheelMoved(me);
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting");
        closeSounds();
        super.exit();
    }
}


package main;

import static java.awt.event.KeyEvent.VK_SPACE;
import processing.core.PApplet;
import static main.Game.*;
import static main.Init.initLoadingScreen;
import static main.Init.tickLoadingScreen;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import static util.Constants.*;
import util.input.Input;
import static util.input.Input.consumeInput;

/**
 *
 * @author Nithin
 */
public final class Main extends PApplet {
    
    public static PApplet c /*for context*/;
    public static boolean initialized = false;
    
    private static boolean started = false;
    
    public static void main(String[] args) {
        PApplet.main("main.Main");
    }
    
    @Override
    public void settings() {
        fullScreen(P3D);
        //size(WIDTH, HEIGHT, P3D);
    }
    
    @Override
    public void setup() {
        frameRate(FPS);
        c = this;
        WIDTH = width;
        HEIGHT = height;
        
        new Thread(Game::init).start();
        
        initLoadingScreen();
        
        System.out.println(WIDTH + ", " + HEIGHT);
    }
    
    @Override
    public void draw() {
        if(!(initialized && started)) {
            started = consumeInput(VK_SPACE);
            tickLoadingScreen(initialized);
            return;
        }
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
        if(initialized && started) saveScore();
        closeSounds();
        super.exit();
    }
}

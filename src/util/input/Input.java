
package util.input;

import processing.event.KeyEvent;
import java.util.ArrayList;
import processing.event.MouseEvent;

/**
 *
 * @author Nithin
 */
public final class Input {
    
    private final static ArrayList<Integer> consumableInput = new ArrayList<>();
    public final static ArrayList<Integer> pressedKeyCodes = new ArrayList<>();
    
    private final static ArrayList<Integer> consumableMousePresses = new ArrayList<>();
    public final static ArrayList<Integer> pressedMouseButtons = new ArrayList<>();
    
    private final static ArrayList<Integer> consumableMouseWheel = new ArrayList<>();
    
    private Input() {}
    
    public static void init() {
        
    }
    
    public static boolean consumeInput(int keyCode) {
        for(int i=0; i<consumableInput.size(); i++) {
            if(consumableInput.get(i) == keyCode) {
                consumableInput.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public static boolean[] consumeInput(int[] keyCodes) {
        boolean[] res = new boolean[keyCodes.length];
        for(int i=0; i<keyCodes.length; i++) res[i] = consumeInput(keyCodes[i]);
        return res;
    }
    
    public static int consumeMouseWheel() {
        return consumableMouseWheel.isEmpty() ? 0 : consumableMouseWheel.remove(0);
    }
    
    public static boolean consumeMousePress(int mButton) {
        for(int i=0; i<consumableMousePresses.size(); i++) {
            if(consumableMousePresses.get(i) == mButton) {
                consumableMousePresses.remove(i);
                return true;
            }
        }
        return false;
    }
    
    public static boolean[] consumeMousePresses(int[] mButtons) {
        boolean[] res = new boolean[mButtons.length];
        for(int i=0; i<mButtons.length; i++) res[i] = consumeInput(mButtons[i]);
        return res;
    }
    
    public static boolean isKeyPressed(int keyCode) {
        return pressedKeyCodes.contains(keyCode);
    }
    
    public static void keyTyped(KeyEvent e) {}

    public static void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(!isKeyPressed(keyCode)) {
            pressedKeyCodes.add(keyCode);
            consumableInput.add(keyCode);
            if(consumableInput.size() > 40) consumableInput.remove(0);
        }
    }
    
    public static void keyReleased(KeyEvent e) {
        pressedKeyCodes.removeIf(keyCode -> keyCode == e.getKeyCode());
    }
    
    public static boolean isMousePressed(int mouseButton) {
        return pressedMouseButtons.contains(mouseButton);
    }
    
    public static void mousePressed(MouseEvent me) {
        int mButton = me.getButton();
        if(!isKeyPressed(mButton)) {
            pressedMouseButtons.add(mButton);
            consumableMousePresses.add(mButton);
            if(consumableMousePresses.size() > 20) consumableMousePresses.remove(0);
        }
    }
    
    public static void mouseReleased(MouseEvent me) {
        pressedMouseButtons.removeIf(mButton -> mButton == me.getButton());
    }
    
    public static void mouseWheelMoved(MouseEvent me) {
        consumableMouseWheel.add(me.getCount());
        if(consumableMouseWheel.size() > 20) consumableMouseWheel.remove(0);
    }
}


package util.input;

import processing.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author Nithin
 */
public final class Input {
    
    private final static ArrayList<Integer> consumableInput = new ArrayList<>();
    public final static ArrayList<Integer> pressedKeyCodes = new ArrayList<>();
    
    private Input() {}
    
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
}

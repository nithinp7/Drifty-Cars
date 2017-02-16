
package util;

/**
 *
 * @author Nithin
 */
public final class Constants {
    
    public static final int
            WIDTH = 800, 
            HEIGHT = 640,

            FPS = 30;
    
    public static final float
            TIMESTEP = 1.f/FPS,

            GRAVITY = 9.81f,

            EPSILON = 0.001f,
            EPSILON_SQUARED = EPSILON*EPSILON,

            RUBBER_ASPHALT_KF = 7f,
            RUBBER_ASPHALT_SF = 9f;
    
    public static final String
            AI_PATH_URL = "./res/ai_path.json";
            
}

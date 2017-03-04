
package util;

import java.util.ArrayList;
import static main.Main.c;
import processing.core.PImage;
import processing.opengl.PShader;

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
            AI_PATH_URL = "./res/cache/ai_path.json";
    
    public static final int
            ASPHALT_TEX_URL = 0;
    
    private static final String[] TEX_URLS = { 
        "./res/textures/asphalt.jpg" //ASPHALT_TEX_URL
    };
    
    public static final int
            DEF_SHADER = 0,
            SPOTLIGHT_SHADOW_SHADER = 1;
    
    private static final String[] SHADER_URLS = {
        "./res/shaders/frag.glsl ./res/shaders/vert.glsl",
        
        "./res/shaders/shadowFrag.glsl ./res/shaders/shadowVert.glsl"
    };
    
    private static final ArrayList<PImage> textures = new ArrayList<>();
    private static final ArrayList<PShader> shaders = new ArrayList<>();
    
    public static void initTextures() {
        if(textures.isEmpty()) for(String tex_url : TEX_URLS) textures.add(c.loadImage(tex_url));
    }
    
    public static PImage getTextureImage(int index) {
        return textures.get(index);
    }
     
    public static void initShaders() {
        if(shaders.isEmpty()) for(String shader : SHADER_URLS) {
            String[] urls = shader.split(" ");
            shaders.add(c.loadShader(urls[0], urls[1]));
        }
    }
    
    public static PShader getShader(int index) {
        return shaders.get(index);
    }
}

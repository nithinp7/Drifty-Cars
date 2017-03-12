
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
    
    public static int
            WIDTH = 800, 
            HEIGHT = 640,

            FPS = 30;
    
    public static final float
            TIMESTEP = 1.f/FPS,

            GRAVITY = 9.81f,

            EPSILON = 0.001f,
            EPSILON_SQUARED = EPSILON*EPSILON,

            RUBBER_ASPHALT_KF = 4f,//7f,
            RUBBER_ASPHALT_SF = 6f;//9f;
    
    public static final String
            AI_PATH_URL = "./res/cache/ai_path.json";
    
    public static final int
            ASPHALT_TEX = 0;
    
    private static final String[] TEX_URLS = { 
        "./res/textures/asphalt.jpg" //ASPHALT_TEX
    };
    
    public static final int
            CLOUDY_SKYBOX = 0,
            SUNNY_SKYBOX = 1;
    
    private static final String[] SKYBOX_FOLDERS = {
        "./res/skyboxes/cloudy png",
        "./res/skyboxes/sunny png"
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
    
    private static final ArrayList<Skybox> skyboxes = new ArrayList<>();
    
    
    public static void initTextures() {
        if(textures.isEmpty()) for(String tex_url : TEX_URLS) textures.add(c.loadImage(tex_url));
    }
    
    public static PImage getTextureImage(int index) {
        return textures.get(index).copy();
    }
    
    public static void initSkyboxes() {
        for(String skybox_folder : SKYBOX_FOLDERS) {
            String[] split = skybox_folder.split(" ");
            String folderName = split[0], fileType = split[1];

            PImage[] imgs = new PImage[] {
                c.loadImage(folderName+"/front."+fileType),
                c.loadImage(folderName+"/right."+fileType),
                c.loadImage(folderName+"/back."+fileType),
                c.loadImage(folderName+"/left."+fileType),
                c.loadImage(folderName+"/up."+fileType),
                c.loadImage(folderName+"/down."+fileType)
            };
            
            skyboxes.add(new Skybox(imgs));
        }
    }
    
    public static Skybox getSkybox(int index) {
        return skyboxes.get(index);
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

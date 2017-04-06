
package util;

import beads.Gain;
import beads.Glide;
import beads.Sample;
import beads.SampleManager;
import beads.SamplePlayer;
import java.util.ArrayList;
import java.util.Comparator;
import static main.Game.ac;
import static main.Main.c;
import processing.core.PImage;
import processing.opengl.PShader;
import util.audio.AudioRequest;
import util.audio.SampleControls;
import util.audio.SkidParam;

/**
 *
 * @author Nithin
 */
public final class Constants {
    
    public static int
            WIDTH = 800, 
            HEIGHT = 640;
            
    public static final int
            FPS = 30,
            
            TYPE_CAR = 0,
            TYPE_BUILDING = 1;
    
    public static final float
            TIMESTEP = 1.f/FPS,

            GRAVITY = 9.81f,

            EPSILON = 0.001f,
            EPSILON_SQUARED = EPSILON*EPSILON,

            RUBBER_ASPHALT_KF = 6f,
            RUBBER_ASPHALT_SF = 8f;
    
    public static final String
            AI_PATH_URL = "./res/cache/ai_path.json";
    
    public static final int
            ASPHALT_TEX = 0;
    
    private static final String[] TEX_URLS = { 
        "./res/textures/asphalt.jpg" //ASPHALT_TEX
    };
    
    public static final int
            CLOUDY_SKYBOX = 0,
            SUNNY_SKYBOX = 1,
            FLATLAND_SKYBOX = 2,
            GHOST_TOWN_SKYBOX = 3;
    
    private static final String[] SKYBOX_FOLDERS = {
        "./res/skyboxes/cloudy png",
        "./res/skyboxes/sunny png",
        "./res/skyboxes/flatland png",
        "./res/skyboxes/ghost_town png"
    };
    
    public static final int
            DEF_SHADER = 0,
            SPOTLIGHT_SHADOW_SHADER = 1;
    
    private static final String[] SHADER_URLS = {
        "./res/shaders/frag.glsl ./res/shaders/vert.glsl",
        
        "./res/shaders/shadowFrag.glsl ./res/shaders/shadowVert.glsl"
    };
    
    public static final int
            CAR_ENGINE = 0,
            CAR_SKID = 1,
            CAR_CRASH = 2;
    
    private static final String[] SOUND_URLS = {
        "./res/sounds/carEngine.wav",
        "./res/sounds/carSkid.wav",
        "./res/sounds/carCrash.wav"
    };
    
    public static final Comparator<Float> distComparator = (a, b) -> a<b? -1 : Math.abs(a-b)<0.00001f? 0 : 1;
    public static final Comparator<AudioRequest<Float>> distSoundComparator = (a, b) -> distComparator.compare(a.comparisonValue, b.comparisonValue);
    //public static final Comparator<AudioRequest<Float>> distComparator = (a, b) -> (int)(a.comparisonValue-b.comparisonValue)
    public static final Comparator<AudioRequest<SkidParam>> skidSoundComparator = (a, b) -> {
        SkidParam spa = a.comparisonValue, spb = b.comparisonValue;
        int distComp = distComparator.compare(a.comparisonValue.distance, b.comparisonValue.distance);
        if(spa.skidding == spb.skidding) return distComp;
        return spa.skidding? -1 : 1;
    };
    
    private static final ArrayList<PImage> textures = new ArrayList<>();
    private static final ArrayList<PShader> shaders = new ArrayList<>();
    
    private static final ArrayList<Skybox> skyboxes = new ArrayList<>();
    
    public static final ArrayList<Sample> samples = new ArrayList<>();
    public static final ArrayList<SampleControls> samplePlayers = new ArrayList<>();
   
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
            //shaders.add(c.loadShader(urls[0], urls[1]));
        }
    }
    
    public static PShader getShader(int index) {
        return shaders.get(index);
    }
    
    public static void initSamples() {
        ac.out.setGain(8);
        if(samples.isEmpty()) for(String url : SOUND_URLS) samples.add(SampleManager.sample(url));
    }
    
    public static SampleControls createSound(int id, boolean loop) {
        if(id < 0 || id >= samples.size()) return null;
        SamplePlayer sp = new SamplePlayer(ac, samples.get(id));
        
        Glide pGlide = new Glide(ac),
              gGlide = new Glide(ac);
        
        Gain vol = new Gain(ac, 2, gGlide);
        
        vol.addInput(sp);
        sp.setPitch(pGlide);
        
        SampleControls sc = new SampleControls(vol, gGlide, pGlide, sp);
        
        sp.setLoopType(loop? SamplePlayer.LoopType.LOOP_FORWARDS : SamplePlayer.LoopType.NO_LOOP_FORWARDS);
        
        ac.out.addInput(vol);
        
        samplePlayers.add(sc);
        return sc;
    }
    
    public static void closeSounds() {
        samplePlayers.forEach(sp -> sp.kill());
        samples.forEach(s -> s.clear());
        ac.stop();
    }
}

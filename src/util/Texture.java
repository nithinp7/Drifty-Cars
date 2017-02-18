
package util;

import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import static processing.core.PConstants.*;
import processing.core.PImage;

/**
 *
 * @author admin
 */
public class Texture {
    
    private final String texURL;
    private final Vec3 loc;
    
    private final Vec2[] texCoords;
    private final Vec3[] relCoords;
   
    private final PImage tex;
    
    public Texture(String texURL, Vec2[] texCoords, Vec3 loc, Vec3[] relCoords) {
        this.texURL = texURL;
        this.loc = loc;
        
        if(texCoords.length != relCoords.length) System.err.println("Texture Coords and Relative Coords have different lengths");
        
        this.texCoords = texCoords;
        this.relCoords = relCoords;
        
        tex = c.loadImage(texURL);
    }
    
    public void render() {
        c.beginShape();
            c.texture(tex);
            c.textureMode(NORMAL);
            c.textureWrap(REPEAT);
            c.tint(255, 200);
            for(int i=0; i<texCoords.length; i++) {
                Vec2 texCoord = texCoords[i];
                Vec3 relCoord = relCoords[i],
                     locPix = coordWorldToPixels(loc),
                     screenCoord = vectorWorldToPixels(relCoord).add(locPix);
                c.vertex(screenCoord.x, screenCoord.y, screenCoord.z, texCoord.x, texCoord.y);
            }
        c.endShape();
    }
    
    public void updateLoc(Vec3 loc) {
        this.loc.set(loc);
    }
    
    public void updateRelCoords(Vec3[] relCoords) {
        for(int i=0; i<relCoords.length; i++) this.relCoords[i].set(relCoords[i]);
    }
}

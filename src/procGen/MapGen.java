
package procGen;

import entities.building.Block;
import java.util.ArrayList;
import java.util.Random;
import static main.Game.*;
import static main.Main.c;
import static processing.core.PApplet.abs;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.CORNERS;
import static util.Constants.*;

/**
 *
 * @author admin
 */
public final class MapGen {
    
    private static final int 
            TYPE_EMPTY = 0,
            TYPE_BUILDING = 1,
            TYPE_ROAD = 2;
    
    private final float width, height,
                        cellsSideLength,
                        cellWidth, cellHeight,
                        cellWidth_m, cellHeight_m;
    private final int subDivs,
                      cellsSideLength_int;
    
    private final Random r;
    
    private float tx=0, ty=0;
    
    private int[][] map;
    private Block[][] buildings;
    
    public MapGen(float width, float height, int subDivs) {
        this.width = width;
        this.height = height;
        this.subDivs = subDivs;
        
        cellsSideLength = 2.0f*subDivs+1.0f;
        cellsSideLength_int = 2*subDivs+1;
        
        cellWidth = width/cellsSideLength;
        cellHeight = height/cellsSideLength;
        
        cellWidth_m = box2d.scalarPixelsToWorld(cellWidth);
        cellHeight_m = box2d.scalarPixelsToWorld(cellHeight);
        
        map = new int[cellsSideLength_int][cellsSideLength_int];
        buildings = new Block[cellsSideLength_int][cellsSideLength_int];
        
        r = new Random();
        
        emptyMap(0, 0, subDivs, subDivs);
        recalculateMap(0, 0, subDivs, subDivs);
    }

    private void recalculateMap(int startI, int startJ, int endI, int endJ) {
        for(int i=startI; i<endI; i++) for(int j=startJ; j<endJ; j++) {
            int ti = i, tj = j;
            boolean rectPresent = r.nextInt(3) == 0;
            if(!rectPresent) continue;
            while(ti<endI && tj<endJ && map[ti*2+1][tj*2+1] == TYPE_EMPTY) {
                map[ti*2+1][tj*2+1] = TYPE_BUILDING;
                layAdjacentRoads(ti, tj);
                boolean dir = r.nextBoolean();
                rectPresent = r.nextInt(3) == 0;
                if(!rectPresent) break;
                if(dir) {
                    ti++;
                    if(ti>=subDivs) break;
                    map[ti*2][tj*2+1] = TYPE_BUILDING;
                } else {
                    tj++;
                    if(tj>=subDivs) break;
                    map[ti*2+1][tj*2] = TYPE_BUILDING;
                }
            }
        }
        
        for(int i=0; i<cellsSideLength_int; i++) for(int j=startJ; j<cellsSideLength_int; j++) {
            int type = map[i][j];
            float x = tx - width/2 + WIDTH/2 + i*cellWidth,
                  y = ty - height/2 + HEIGHT/2 + j*cellHeight;
            if(type==TYPE_BUILDING && buildings[i][j]==null) {
                Block b = new Block(box2d.coordPixelsToWorld(x, y), 0, cellWidth_m, cellHeight_m, 40, true);
                buildings[i][j] = b;
            }
        }
    }
    
    private void layAdjacentRoads(int i, int j) {
        int val0 = map[2*i+1][2*j],
            val1 = map[2*i][2*j],
            val2 = map[2*i][2*j+1];

        if(val0 != TYPE_BUILDING) map[2*i+1][2*j] = TYPE_ROAD;
        if(val1 != TYPE_BUILDING) map[2*i][2*j] = TYPE_ROAD;
        if(val2 != TYPE_BUILDING) map[2*i][2*j+1] = TYPE_ROAD;

        boolean iSpace = i < subDivs, jSpace = j < subDivs;
        if(jSpace) {
            if(map[2*i][2*j+2] != TYPE_BUILDING) map[2*i][2*j+2] = TYPE_ROAD;
            if(map[2*i+1][2*j+2] != TYPE_BUILDING) map[2*i+1][2*j+2] = TYPE_ROAD;
            if(iSpace && map[2*i+2][2*j+2] != TYPE_BUILDING) map[2*i+2][2*j+2] = TYPE_ROAD;
        }

        if(iSpace) {
            if(map[2*i+2][2*j] != TYPE_BUILDING) map[2*i+2][2*j] = TYPE_ROAD;
            if(map[2*i+2][2*j+1] != TYPE_BUILDING) map[2*i+2][2*j+1] = TYPE_ROAD;
            if(jSpace && map[2*i+2][2*j+2] != TYPE_BUILDING) map[2*i+2][2*j+2] = TYPE_ROAD;
        }
    }
    
    private void emptyMap(int startI, int startJ, int endI, int endJ) {
        for(int i=startI; i<endI; i++) for(int j=startJ; j<endJ; j++) {
            map[i][j] = TYPE_EMPTY;     
            Block b = buildings[i][j];
            if(b!=null) b.dispose();
            buildings[i][j] = null;
        }
    }
    
    private void translateMap(int ti, int tj) {
        int[][] temp = new int[cellsSideLength_int][cellsSideLength_int];
        Block[][] tempBuildings = new Block[cellsSideLength_int][cellsSideLength_int];
        for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) {
            int copy_i = i-2*ti, copy_j = j-2*tj;
            if(copy_i<cellsSideLength_int && copy_i>=0 && copy_j<cellsSideLength_int && copy_j>=0) {
                temp[i][j] = map[copy_i][copy_j];
                tempBuildings[i][j] = buildings[copy_i][copy_j];
            } else {
                temp[i][j] = TYPE_EMPTY;
                Block b = buildings[cellsSideLength_int-1-i][cellsSideLength_int-1-j];
                if(b != null) b.dispose();
                tempBuildings[i][j] = null;
            }
        }
        map = temp;
        buildings = tempBuildings;

        if(ti > 0) recalculateMap(0, 0, ti+1, subDivs);
        else recalculateMap(subDivs+ti, 0, subDivs, subDivs);

        if(tj > 0) recalculateMap(0, 0, subDivs, tj+1);
        else recalculateMap(0, subDivs+tj, subDivs, subDivs);
    }
    
    private void drawGround() {
        
    }
    
    private void drawBuildings() {
        for(Block[] col : buildings) for(Block b : col) if(b != null) b.render(c.g);
    }
    
    private void drawBuildings2() {
        int mode = c.g.rectMode;
        c.rectMode(CENTER);
        c.stroke(50);
        c.strokeWeight(1);
        c.fill(150, 0, 0);
        c.pushMatrix();
        c.translate(tx-width/2+WIDTH/2, ty-height/2+HEIGHT/2);
        for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) {
          int val = map[i][j];
          if(val == TYPE_BUILDING) {
            c.pushMatrix();
            c.translate(i*cellWidth, j*cellHeight, 20);
            c.box(cellWidth, cellHeight, 40);
            c.popMatrix();
          }
        }
        c.popMatrix();
        c.rectMode(mode);
    }
    
    private void drawRoads() {
        int mode = c.g.rectMode;
        c.rectMode(CORNERS);
        c.pushStyle();
        c.pushMatrix();
        c.translate(tx-width/2+WIDTH/2 - 0.5f*cellWidth, ty-height/2+HEIGHT/2 - 0.5f*cellHeight, 0.5f);
        //c.strokeWeight(30);
        c.noStroke();
        c.fill(30);
        for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) {
            if(map[i][j] != TYPE_ROAD) continue;
            boolean right = i<subDivs*2 && map[i+1][j]==TYPE_ROAD,
                    left = i>0 && map[i-1][j]==TYPE_ROAD, 
                    down = j<subDivs*2 && map[i][j+1]==TYPE_ROAD, 
                    up = j>0 && map[i][j-1]==TYPE_ROAD;
            if(right) c.rect((i+0.5f)*cellWidth, (j+0.2f)*cellHeight, (i+1.0f)*cellWidth, (j+0.8f)*cellHeight);
            if(left) c.rect((i+0.5f)*cellWidth, (j+0.2f)*cellHeight, i*cellWidth, (j+0.8f)*cellHeight);
            if(down) c.rect((i+0.2f)*cellWidth, (j+0.5f)*cellHeight, (i+0.8f)*cellWidth, (j+1.0f)*cellHeight);
            if(up) c.rect((i+0.2f)*cellWidth, (j+0.5f)*cellHeight, (i+0.8f)*cellWidth, j*cellHeight);
        }
        c.popMatrix();
        c.popStyle();
        c.rectMode(mode);
    }
    
    public void update() {
        float camX = getCamTransX(), camY = getCamTransY(), difX = camX-tx, difY = camY-ty;
        if(abs(difX) >= 4*cellWidth) {
            int sign = -(int)Math.signum(difX);
            tx = camX;
            translateMap(sign*2, 0);
        }

        if(abs(difY) >= 4*cellHeight) {
            int sign = -(int)Math.signum(difY);
            ty = camY;
            translateMap(0, sign*2);
        }
    }
    
    public void render() {
        drawGround();
        drawBuildings();
        drawRoads();
    }
}


package procGen;

import ai.Path.PathNode;
import entities.building.Block;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import static main.Game.*;
import static main.Main.c;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.*;
import static util.Constants.*;

/**
 *
 * @author Nithin
 */
public final class MapGen {
    
    public static final int 
            TYPE_EMPTY = 0,
            TYPE_BUILDING = 1,
            TYPE_ROAD = 2;
    
    public final float width, height,
                       cellsSideLength,
                       cellWidth, cellHeight,
                       cellWidth_world, cellHeight_world;
    
    private final int subDivs,
                      cellsSideLength_int;
    
    private final Random r;
    
    private float tx=0, ty=0;
    
    private int[][] map;
    private Block[][] buildings;
    private PathNode[][] nodes;
    
    private final ArrayList<RoadSegment> roads = new ArrayList<>();
    
    public MapGen(float width, float height, int subDivs) {
        this.width = width;
        this.height = height;
        this.subDivs = subDivs;
        
        cellsSideLength = 2.0f*subDivs+1.0f;
        cellsSideLength_int = 2*subDivs+1;
        
        cellWidth = width/cellsSideLength;
        cellHeight = height/cellsSideLength;
        
        cellWidth_world = box2d.scalarPixelsToWorld(cellWidth);
        cellHeight_world = box2d.scalarPixelsToWorld(cellHeight);
        
        map = new int[cellsSideLength_int][cellsSideLength_int];
        buildings = new Block[cellsSideLength_int][cellsSideLength_int];
        nodes = new PathNode[cellsSideLength_int][cellsSideLength_int];
        
        r = new Random();
        
        emptyMap(0, 0, subDivs, subDivs);
        recalculateMap(0, 0, subDivs, subDivs);
    }
    
    public MapCoord getClosestMapCoord(Vec2 loc) {
        return getClosestMapCoord(loc.x, loc.y);
    }
    
    public MapCoord getClosestMapCoord(float x, float y) {
        float xLoc = x-box2d.scalarPixelsToWorld(tx+width/2-WIDTH/2),
              yLoc = y-box2d.scalarPixelsToWorld(ty+height/2-HEIGHT/2);
        int i = (int)(xLoc/cellWidth_world),
            j = (int)(yLoc/cellHeight_world);
        if(!isWithin(i, j)) return null;
        return new MapCoord(i, j);
    }
    
    public MapCoord getClosestMapCoordOfType(Vec2 loc, int type) {
        return getClosestMapCoordOfType(loc, type, cellsSideLength_int);
    }
    
    public MapCoord getClosestMapCoordOfType(Vec2 loc, int type, int maxSearchSize) {
        return getClosestMapCoordOfType(getClosestMapCoord(loc), type, maxSearchSize);
    }
    
    public MapCoord getClosestMapCoordOfType(MapCoord mc, int type) {
        return getClosestMapCoordOfType(mc, type, cellsSideLength_int);
    }
    
    public MapCoord getClosestMapCoordOfType(MapCoord mc, int type, int maxSearchSize) {
        if(!isWithin(mc)) return null;
        ArrayList<MapCoord> possible = new ArrayList<>();

        for(int search=0; search<maxSearchSize/2; search++) {
            for(int i=-search; i<=search; i++) {
                if(isWithin(mc.i+i)) {
                    if(isWithin(mc.j-search) && map[mc.i+i][mc.j-search]==type) 
                        possible.add(new MapCoord(mc.i+i, mc.j-search)); 
                    if(isWithin(mc.j+search) && map[mc.i+i][mc.j+search]==type) 
                        possible.add(new MapCoord(mc.i+i, mc.j+search));
                }
                
                if(isWithin(mc.j+i)) {
                    if(isWithin(mc.i-search+1) && map[mc.i-search+1][mc.j+i]==type) 
                        possible.add(new MapCoord(mc.i-search+1, mc.j+i)); 
                    if(isWithin(mc.i+search-1) && map[mc.i+search-1][mc.j+i]==type) 
                        possible.add(new MapCoord(mc.i+search-1, mc.j+i)); 
                }
            }
            
            Optional<MapCoord> closest = possible.
                    stream().
                    min((m, m1) -> (int)Math.signum(m.distanceTo(mc) - m1.distanceTo(mc)));
            
            if(closest.isPresent()) return closest.get();
        }
        return null;
    }
    
    public boolean isWithin(int i) {
        return i>0 && i<cellsSideLength_int;
    }
    
    public boolean isWithin(MapCoord mc) {
        return isWithin(mc.i, mc.j);
    }
    
    public boolean isWithin(int i, int j) {
        return isWithin(i) && isWithin(j);
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
                    if(ti>=endI) break;
                    map[ti*2][tj*2+1] = TYPE_BUILDING;
                } else {
                    tj++;
                    if(tj>=endJ) break;
                    map[ti*2+1][tj*2] = TYPE_BUILDING;
                }
            }
        }
        
        for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) {
            int type = map[i][j];
            float x = tx - width/2 + WIDTH/2 + i*cellWidth,
                  y = ty - height/2 + HEIGHT/2 + j*cellHeight;
            
            Vec2 pos = box2d.coordPixelsToWorld(x, y);
            
            if(type==TYPE_BUILDING && buildings[i][j]==null) {
                Block b = new Block(pos.x, pos.y, 0, cellWidth_world*0.6f, cellHeight_world*0.6f, 40, false);
                buildings[i][j] = b;
            } else if(type==TYPE_ROAD) {
                int i_copy = i, j_copy = j;
                
                //pos.set(pos.x-2*cellWidth_world, pos.y-2*cellHeight_world);
                
                PathNode p;
                if(nodes[i][j]==null){
                    p = path.createRawNode(pos.x, pos.y);
                    nodes[i][j] = p;
                } else p = nodes[i][j];
                       
                if(i+1<cellsSideLength_int && map[i+1][j]==TYPE_ROAD) {
                    if(roads.stream().noneMatch(road -> road.isSame(i_copy, j_copy, i_copy+1, j_copy))) {
                        PathNode p1;
                        if(nodes[i+1][j]==null){
                            p1 = path.createRawNode(pos.x+cellWidth_world, pos.y);
                            nodes[i+1][j] = p1;
                        } else p1 = nodes[i+1][j];
                        roads.add(new RoadSegment(i, j, i+1, j, p, p1, cellWidth, cellHeight));
                    }
                }
                if(j+1<cellsSideLength_int && map[i][j+1]==TYPE_ROAD) {
                    if(roads.stream().noneMatch(road -> road.isSame(i_copy, j_copy, i_copy, j_copy+1))) {
                        PathNode p1;
                        if(nodes[i][j+1]==null){
                            p1 = path.createRawNode(pos.x, pos.y-cellHeight_world);
                            nodes[i][j+1] = p1;
                        } else p1 = nodes[i][j+1];
                        roads.add(new RoadSegment(i, j, i, j+1, p, p1, cellWidth, cellHeight));
                    }
                }
            }
        }
    }
    
    private void layAdjacentRoads(int i, int j) {
        
        if(map[2*i+2][2*j] != TYPE_BUILDING) map[2*i+2][2*j] = TYPE_ROAD;
        if(map[2*i+2][2*j+1] != TYPE_BUILDING) map[2*i+2][2*j+1] = TYPE_ROAD;
        if(map[2*i+2][2*j+2] != TYPE_BUILDING) map[2*i+2][2*j+2] = TYPE_ROAD;
        
        if(map[2*i][2*j] != TYPE_BUILDING) map[2*i][2*j] = TYPE_ROAD;
        if(map[2*i][2*j+1] != TYPE_BUILDING) map[2*i][2*j+1] = TYPE_ROAD;
        if(map[2*i][2*j+2] != TYPE_BUILDING) map[2*i][2*j+2] = TYPE_ROAD;
        
        if(map[2*i+1][2*j] != TYPE_BUILDING) map[2*i+1][2*j] = TYPE_ROAD;
        if(map[2*i+1][2*j+2] != TYPE_BUILDING) map[2*i+1][2*j+2] = TYPE_ROAD;
    }
    
    private void emptyMap(int startI, int startJ, int endI, int endJ) {
        for(int i=startI; i<endI; i++) for(int j=startJ; j<endJ; j++) {
            map[i][j] = TYPE_EMPTY;     
            Block b = buildings[i][j];
            if(b!=null) b.dispose();
            buildings[i][j] = null;
            PathNode p = nodes[i][j];
            if(p != null) {
                path.removeNode(p);
                nodes[i][j] = null;
            }
        }
        //DEAD FLAG NOT SET
        roads.removeIf(road -> road.withinBounds(startI, startJ, endI, endJ));
    }
    
    private void translateMap(int ti, int tj) {
        int[][] temp = new int[cellsSideLength_int][cellsSideLength_int];
        Block[][] tempBuildings = new Block[cellsSideLength_int][cellsSideLength_int];
        PathNode[][] tempNodes = new PathNode[cellsSideLength_int][cellsSideLength_int];
        for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) {
            int copy_i = i-2*ti, copy_j = j-2*tj;
            if(copy_i<cellsSideLength_int && copy_i>=0 && copy_j<cellsSideLength_int && copy_j>=0) {
                temp[i][j] = map[copy_i][copy_j];
                tempBuildings[i][j] = buildings[copy_i][copy_j];
                tempNodes[i][j] = nodes[copy_i][copy_j];
            } else {
                temp[i][j] = TYPE_EMPTY;
                Block b = buildings[cellsSideLength_int-1-i][cellsSideLength_int-1-j];
                if(b != null) b.dispose();
                tempBuildings[i][j] = null;
                PathNode p = nodes[cellsSideLength_int-1-i][cellsSideLength_int-1-j];
                if(p != null) {
                    path.removeNode(p);
                }
                tempNodes[i][j] = null;
            }
        }
        translateRoads(ti*2, tj*2);
        map = temp;
        buildings = tempBuildings;
        nodes = tempNodes;

        if(ti > 0) recalculateMap(0, 0, ti+1, subDivs);
        else recalculateMap(subDivs+ti, 0, subDivs, subDivs);

        if(tj > 0) recalculateMap(0, 0, subDivs, tj+1);
        else recalculateMap(0, subDivs+tj, subDivs, subDivs);
    }
    
    private void translateRoads(int ti, int tj) {
            roads
                .removeIf(road -> {
                   //DEAD FLAG IS NOT SET
                   road.translate(ti, tj);
                   return !road.withinBounds(0, 0, cellsSideLength_int, cellsSideLength_int);
                });
    }
    
    private void drawGround() {
        
    }
    
    private void drawBuildings() {
        for(Block[] col : buildings) for(Block b : col) if(b != null) b.render(c.g);
    }
    
    private void drawRoads() {
        int mode = c.g.rectMode;
        c.rectMode(CORNERS);
        //c.rectMode(CENTER);
        c.pushStyle();
        c.pushMatrix();
        c.translate(tx-width/2+WIDTH/2, ty-height/2+HEIGHT/2, 0.5f);
        c.noStroke();
        c.fill(30);
        
        roads.forEach(road -> road.render(c.g));
        //for(int i=0; i<cellsSideLength_int; i++) for(int j=0; j<cellsSideLength_int; j++) if(map[i][j] == TYPE_ROAD) c.rect(i*cellWidth, j*cellWidth, cellWidth, cellHeight);
        
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
    
    public class MapCoord {
        public final int i, j;
        
        public MapCoord(int i, int j) {
            this.i = i;
            this.j = j;
        }
        
        public boolean isSame(MapCoord mc) {
            return i==mc.i && j==mc.j;
        }
        
        public ArrayList<MapCoord> getAdjacentCoords() {
            ArrayList<MapCoord> adj = new ArrayList<>();
            
            if(i+1<cellsSideLength) {
                adj.add(new MapCoord(i+1, j));
            }
            if(i>0) {
                adj.add(new MapCoord(i-1, j));
            }
            if(j+1<cellsSideLength) {
                adj.add(new MapCoord(i, j+1));
            }
            if(j>0) {
                adj.add(new MapCoord(i, j-1));
            }
            
            return adj;
        }
        
        public int getTileType() {
            return map[i][j];
        }
        
        public PathNode getNode() {
            return nodes[i][j];
        }
        
        public float distanceTo(MapCoord mc) {
            return sqrt(pow(mc.i-i, 2) + pow(mc.j-j, 2));
        }
    }
}

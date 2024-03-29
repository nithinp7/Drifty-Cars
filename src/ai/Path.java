
package ai;

import java.io.File;
import static java.lang.Math.signum;
import java.util.ArrayList;
import java.util.Optional;
import static main.Main.c;
import static main.Game.box2d;
import org.jbox2d.common.Vec2;
import static processing.core.PApplet.*;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 *
 * @author Nithin
 */
public final class Path {
    
    private final ArrayList<PathNode> nodes = new ArrayList<>();
    private final ArrayList<PathSegment> segments = new ArrayList<>(), tempSegments = new ArrayList<>();
    
    public final class PathNode {
        
        private final float x, y;
        
        private final ArrayList<PathSegment> connectingSegments = new ArrayList<>(4);
        private final ArrayList<PathNode> connectingNodes = new ArrayList<>(4);
        
        private PathNode(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        private PathNode(Vec2 pos) {
            this(pos.x, pos.y);
        }
        
        private void addSegmentConnection(PathSegment seg) {
            connectingSegments.add(seg);
            connectingNodes.add(seg.n1 != this ? seg.n1 : seg.n2);
        }
        
        private void render() {
            c.pushMatrix();
                Vec2 pixPos = box2d.coordWorldToPixels(x, y);
                c.translate(pixPos.x, pixPos.y);
                c.fill(240, 250, 50);
                c.noStroke();
                c.sphere(box2d.scalarWorldToPixels(2));
            c.popMatrix();
        }
    }
    
    public final class PathSegment {
        
        private final PathNode n1, n2;
        private final Vec2 normal, perp;
        
        private final boolean directional;
        
        @SuppressWarnings("LeakingThisInConstructor")
        private PathSegment(PathNode n1, PathNode n2, boolean directional) {
            this.n1 = n1;
            this.n2 = n2;
            
            n1.addSegmentConnection(this);
            n2.addSegmentConnection(this);
            
            normal = new Vec2(n2.x-n1.x, n2.y-n1.y);
            normal.normalize();
            
            perp = new Vec2(-normal.y, normal.x);
            
            this.directional = directional;
        }
        
        public float getPerpendicularDeviation(Vec2 loc, Vec2 dir) {
            return ((loc.x-n1.x)*perp.x + (loc.y-n1.y)*perp.y) * (dir==null ? 1 : Math.signum(Vec2.dot(dir, normal)));
        }
        
        public float getPerpendicularDeviation(Vec2 loc, float angle) {
            return getPerpendicularDeviation(loc, new Vec2(cos(angle), sin(angle)));
        }
        
        private float getClosestDistance(Vec2 loc) {
            return getShortestVector(loc).length();
        }
        
        private Vec2 getShortestVector(Vec2 loc) {
            Vec2 n1_l = new Vec2(loc.x-n1.x, loc.y-n1.y);
            Vec2 n2_l = new Vec2(loc.x-n2.x, loc.y-n2.y);
            float proj = Vec2.dot(n1_l, normal);
            float proj2 = Vec2.dot(n2_l, normal);
            
            return proj < 0 ? n1_l : 
                   proj2 > 0 ? n2_l : 
                   perp.mul(getPerpendicularDeviation(loc, null));
        }
        
        public float getAngularDeviation(Vec2 dir) {
            float angDev = atan2(dir.y*normal.x - dir.x*normal.y, dir.x*normal.x + dir.y*normal.y);
            return true ? angDev : angDev > PI/2 ? angDev-PI : angDev < -PI/2 ? angDev+PI : angDev;
        }
        
        public float getAngularDeviation(float angle) {
            return getAngularDeviation(new Vec2(cos(angle), sin(angle)));
        }
        
        private void render() {
            Vec2 pixPosN1 = box2d.coordWorldToPixels(n1.x, n1.y);
            Vec2 pixPosN2 = box2d.coordWorldToPixels(n2.x, n2.y);
            if(directional) c.stroke(100, 240, 80);
            else c.stroke(240, 180, 20);
            c.strokeWeight(box2d.scalarWorldToPixels(2f));
            c.line(pixPosN1.x, pixPosN1.y, pixPosN2.x, pixPosN2.y);
        }
    }
    
    public void addNode(float x, float y, boolean directional, boolean startNewChain) {
        
        PathNode n = new PathNode(x, y);
        
        if(nodes.isEmpty()) {
            nodes.add(n);
            return;
        }
        
        PathNode reselect = checkReselect(x, y), prev = nodes.get(nodes.size()-1);
        
        if(reselect != null && reselect == prev) return;
        
        if(startNewChain) {
            nodes.add(reselect==null? n : reselect);
            return;
        }
        
        if(reselect != null) {
            nodes.add(reselect);
            PathSegment seg = new PathSegment(prev, reselect, directional);
            segments.add(seg);
            prev.addSegmentConnection(seg);
            reselect.addSegmentConnection(seg);
            return;
        }
        
        nodes.add(n);
        PathSegment seg = new PathSegment(prev, n, directional);
        segments.add(seg);
        prev.addSegmentConnection(seg);
        n.addSegmentConnection(seg);
    }
    
    public PathNode createRawNode(float x, float y) {
        PathNode n = new PathNode(x, y);
        nodes.add(n);
        return n;
    }
    
    private PathNode checkReselect(float x, float y) {
        PathNode closest = nodes.stream().min((n, n2) -> (int)(sqrt(pow(n.x-x, 2) + pow(n.y-y, 2)) - sqrt(pow(n2.x-x, 2) + pow(n2.y-y, 2)))).get();
        if(sqrt(pow(closest.x-x, 2) + pow(closest.y-y, 2)) < 8) return closest;
        else return null;
    }
    
    public Optional<PathNode> getClosestNode(Vec2 loc, float maxDist) {
        return nodes.
                stream().
                filter(node -> sqrt(pow(loc.x-node.x, 2) + pow(loc.y-node.y, 2)) < maxDist).
                min((n, n1) -> (int)signum(sqrt(pow(loc.x-n.x, 2) + pow(loc.y-n.y, 2)) - sqrt(pow(loc.x-n1.x, 2) + pow(loc.y-n1.y, 2))));
    }
    
    public Optional<PathSegment> getClosestSegment(Vec2 loc, float maxDist) {
        return segments.stream().filter(seg -> seg.getClosestDistance(loc) < maxDist).min((seg, seg2) -> (int)signum(seg.getClosestDistance(loc) - seg2.getClosestDistance(loc)));
    }
    
    public Optional<PathSegment> getClosestSegment(float x, float y, float maxDist) {
        return getClosestSegment(new Vec2(x, y), maxDist);
    }
    
    public PathSegment createTemporarySegment(Vec2 pos) {
        Optional<PathSegment> closest = getClosestSegment(pos, 100);
        if(!closest.isPresent()) return null;
        PathSegment closestSeg = closest.get();
        Vec2 shortestConnection = closestSeg.getShortestVector(pos);
        PathNode n = new PathNode(pos),
                 n1 = new PathNode(pos.sub(shortestConnection));
        PathSegment seg = new PathSegment(n, n1, true);
        tempSegments.add(seg);
        return seg;
    }
    
    public PathSegment createTemporarySegment(Vec2 pos, Vec2 target, boolean directional) {
        PathNode n = new PathNode(pos),
                 n1 = new PathNode(target);
        
        PathSegment seg = new PathSegment(n, n1, directional);
        tempSegments.add(seg);
        return seg;
    }
    
    public PathSegment createTemporarySegment(PathNode n, PathNode n1, boolean directional) {
        PathSegment seg = new PathSegment(n, n1, directional);
        tempSegments.add(seg);
        return seg;
    }
        
    public void removeTemporarySegment(PathSegment temp) {
        tempSegments.remove(temp);
    }
    
    public void removeNodes(ArrayList<PathNode> toRemove) {
        toRemove.forEach(p -> segments.removeAll(p.connectingSegments));
        nodes.removeAll(toRemove);
    }
    
    public void removeNode(PathNode p) {
        segments.removeAll(p.connectingSegments);
        nodes.remove(p);
    }
    
    public void removeSegments(ArrayList<PathSegment> toRemove) {
        segments.removeAll(toRemove);
    }
    
    public PathSegment createRawSegment(PathNode p, PathNode p2, boolean directional) {
        PathSegment s = new PathSegment(p, p2, directional);
        segments.add(s);
        return s;
    }
    
    public void render() {
        c.pushStyle();
        c.pushMatrix();
            c.translate(0, 0, 2);
            //nodes.forEach(node -> node.render());
            segments.forEach(segment -> segment.render());
            //tempSegments.forEach(segment -> segment.render());
        c.popMatrix();
        c.popStyle();
    }
    
    public void clear() {
        segments.clear();
        nodes.clear();
    }
    
    public void savePath(String file) {
        JSONObject js = new JSONObject();
        JSONArray nodes_raw = new JSONArray();
        for(int i=0; i<nodes.size(); i++) {
            PathNode node = nodes.get(i);
            nodes_raw.setFloat(i*2, node.x);
            nodes_raw.setFloat(i*2+1, node.y);
        }
        js.setJSONArray("nodes", nodes_raw);
        JSONArray segments_raw = new JSONArray();
        for(int i=0; i<segments.size(); i++) {
            PathSegment segment = segments.get(i);
            segments_raw.setInt(i*2, nodes.indexOf(segment.n1));
            segments_raw.setInt(i*2+1, nodes.indexOf(segment.n2));
        }
        js.setJSONArray("segments", segments_raw);
        js.save(new File(file), "");
    }
    
    public void initFromJSON(String file) {
        JSONObject js = loadJSONObject(new File(file));
        JSONArray nodes_raw = js.getJSONArray("nodes");
        for(int i=0; i<nodes_raw.size(); i+=2) nodes.add(new PathNode(nodes_raw.getFloat(i), nodes_raw.getFloat(i+1)));
        JSONArray segments_raw = js.getJSONArray("segments");
        for(int i=0; i<segments_raw.size(); i+=2) segments.add(new PathSegment(nodes.get(segments_raw.getInt(i)), nodes.get(segments_raw.getInt(i+1)), false));
    }
}

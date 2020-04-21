package gsynlib.particles;

import java.util.*;

import gsynlib.geom.Bounds;
import gsynlib.geom.QuadTree;
import gsynlib.geom.QuadTreeNode;
import processing.core.*;

//TODO QUADTREE
public class ForceField {

	public QuadTree quadTree;
	ArrayList<Force> forces = new ArrayList<Force>();

	public ForceField(Bounds initialBounds) {
		this.quadTree = new QuadTree(initialBounds);
	}

	public void addForce(PVector position, PVector force) {
		Force f = new Force(position, force);
		quadTree.insert(f.position, f);
		forces.add(f);
	}

	public void clear() {
		Bounds b = new Bounds();
		b.copyFrom(quadTree.getRoot().bounds);
		quadTree = new QuadTree(b);
		forces.clear();
	}

	ArrayList<Force> forceList = new ArrayList<Force>();
	public Force getClosestForce(PVector pos) {
		
		QuadTreeNode containingNode = quadTree.getNodeUnder(pos);
		
		forceList.clear();
		for(Object obj : containingNode.data) {
			if(obj instanceof Force) {
				forceList.add((Force)obj);
			}
		}
		
		Force found = null;
		float closest = Float.MAX_VALUE;
		int s = forceList.size();
		for (int i = 0; i < s; i++) {
			Force f = forceList.get(i);
			float d = PVector.dist(pos, f.position);
			if (d < closest) {
				found = f;
				closest = d;
			}
		}

		return found;
	}
}

package gsynlib.particles;

import java.util.*;

import gsynlib.geom.*;
import processing.core.*;

//TODO QUADTREE
public class ForceField {

	public QuadTree quadTree;

	public ForceField(Bounds initialBounds) {
		this.quadTree = new QuadTree(initialBounds);
	}

	public void addForce(PVector position, PVector force) {
		QuadTreeDataVector d = new QuadTreeDataVector(position,force);
		quadTree.insert(d);
	}

	public void clear() {
		Bounds b = new Bounds();
		b.set(quadTree.getRoot().bounds);
		quadTree = new QuadTree(b);
	}

	public QuadTreeDataVector getClosestForce(PVector pos) {
		return (QuadTreeDataVector)quadTree.getNearestData(pos);
	}
}

package gsynlib.geom;

import processing.core.PVector;

public class QuadTreeDataVector extends QuadTreeData {
	public PVector vector = new PVector();
	
	public QuadTreeDataVector(PVector pos, PVector vec) {
		this.position.set(pos);
		this.vector.set(vec);
	}
}

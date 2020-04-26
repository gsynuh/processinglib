package gsynlib.geom;

import processing.core.PVector;

public class QuadTreeDataVector extends QuadTreeData {
	public PVector vector = new PVector();
	public int type = 0; //Allows for a sketch to identify what to do with the vector information
	
	public QuadTreeDataVector(PVector pos, PVector vec) {
		this.position.set(pos);
		this.vector.set(vec);
	}
}

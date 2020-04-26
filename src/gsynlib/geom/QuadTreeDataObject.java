package gsynlib.geom;

import processing.core.PVector;

public class QuadTreeDataObject extends QuadTreeData {
	public Object object = null;
	
	public QuadTreeDataObject(PVector pos, Object data) {
		this.position.set(pos);
		this.object = data;
	}
}
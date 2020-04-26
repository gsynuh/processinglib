package gsynlib.geom;

import processing.core.PVector;

//position has arbitrary data attached. one can also extend QuadTreeData, QuadTree/QuadTreeNode have generic types.
public class QuadTreeDataObject extends QuadTreeData {
	public Object object = null;
	
	public QuadTreeDataObject(PVector pos, Object data) {
		this.position.set(pos);
		this.object = data;
	}
}
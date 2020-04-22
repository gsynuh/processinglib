package gsynlib.geom;

import processing.core.PVector;
import static processing.core.PApplet.*;

public class QuadTreeData {
	public PVector position = new PVector();
	public Object object = null;
	
	public QuadTreeData(PVector pos, Object data) {
		this.position.set(pos);
		this.object = data;
	}
}

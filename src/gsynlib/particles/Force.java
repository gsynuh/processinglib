package gsynlib.particles;

import processing.core.*;

public class Force {
	public PVector position = new PVector();
	public PVector value = new PVector();
	
	public Force() {
		
	}
	
	public Force(PVector pos, PVector val) {
		this.position.set(pos);
		this.value.set(val);
	}
}

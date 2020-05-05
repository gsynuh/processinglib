package gsynlib.utils;

import gsynlib.base.*;
import static processing.core.PApplet.*;

public class NoiseLoop extends GsynlibBase {

	float p = 0;

	public NoiseLoop(float perimeter) {
		this.p = perimeter;
	}

	public NoiseLoop() {
		this.p = 1;
	}
	
	public void setLoopPerimeter(float v) {
		this.p = v;
	}

	public float get(float t) {
		return get(t, 0);
	}

	public float get(float t, float z) {

		t = t % 1.0f;

		float r = TWO_PI / this.p;

		float x = cos(t * TWO_PI) * r;
		float y = sin(t * TWO_PI) * r;

		return app().noise(x + r, y + r, z);
	}

}

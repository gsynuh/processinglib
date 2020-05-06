package gsynlib.utils;

import gsynlib.base.*;
import static processing.core.PApplet.*;

public class NoiseLoop extends GsynlibBase {

	float p = 0; //perimeter of loop

	public NoiseLoop(float perimeter) {
		this.p = perimeter;
	}

	public NoiseLoop() {
		this.p = 10;
	}

	public void setLoopPerimeter(float v) {
		this.p = v;
	}

	public float get(float t) {
		return get(t, 0);
	}

	public float get(float t, float z) {

		t = t % 1.0f;

		float r = this.p / TWO_PI;

		float x = cos(t * TWO_PI) * r;
		float y = sin(t * TWO_PI) * r;

		return app().noise(x + r, y + r, z);
	}

}

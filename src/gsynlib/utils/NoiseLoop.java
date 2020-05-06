package gsynlib.utils;

import gsynlib.base.*;
import static processing.core.PApplet.*;

public class NoiseLoop extends GsynlibBase {

	OpenSimplexNoise noise;
	float p = 0; //perimeter of loop
	
	//float halfSqrtDimensions = 0.5f * sqrt(2);

	public NoiseLoop(float perimeter) {
		this.p = perimeter;
		init();
	}

	public NoiseLoop() {
		this.p = 10;
		init();
	}
	
	void init() {
		noise = new OpenSimplexNoise(floor(app().random(10000)));
		setLoopPerimeter(this.p);
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
		
		float n = map((float)noise.eval(x + r, y + r, z),-1,1,0,1);
		
		return n;
	}

}

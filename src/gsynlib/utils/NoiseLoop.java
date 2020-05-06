package gsynlib.utils;

import gsynlib.base.*;
import static processing.core.PApplet.*;

public class NoiseLoop extends GsynlibBase {

	OpenSimplexNoise noise;
	long seed = 0;
	float p = 0; //perimeter of loop
	
	//float halfSqrtDimensions = 0.5f * sqrt(2);

	public NoiseLoop(float perimeter) {
		this.seed = floor(app().random(10000));
		this.p = perimeter;
		init();
	}
	
	public NoiseLoop(long seed) {
		this.seed = seed;
		this.p = 10;
		init();
	}
	
	public NoiseLoop(long seed, float perimeter) {
		this.seed = seed;
		this.p = perimeter;
		init();
	}

	public NoiseLoop() {
		this.p = 10;
		init();
	}
	
	void init() {
		noise = new OpenSimplexNoise(this.seed);
		setLoopPerimeter(this.p);
	}
	
	public void setSeed(long seed) {
		init();
	}

	public void setLoopPerimeter(float v) {
		this.p = v;
	}

	public float get(float t) {
		return get(0, t, 0);
	}

	public float get(float t, float z) {
		return get(0,t,z);
	}
	
	public float get(int id,float t) {
		return get(id, t, 0);
	}
	
	float gw = 256f;

	public float get(int id,float t,float z) {
		t = t % 1.0f;

		float r = this.p / TWO_PI;

		float x = cos(t * TWO_PI) * r;
		float y = sin(t * TWO_PI) * r;
		
		float offX = (id % gw) * r * 2;
		float offY = (id / gw) * r * 2;
		
		float n = map((float)noise.eval(x + offX + r, y + offY + r, z),-1,1,0,1);
		return n;
	}

}

package gsynlib.utils;

import gsynlib.geom.*;
import processing.core.*;

public class Formula {
	public Bounds bounds = new Bounds();
	public float numSamples = 100;

	public float f(float x) {
		return x;
	}

	public PVector evaluate(float x) {
		float y = f(x);
		PVector p = bounds.getPositionFromNorm(x, y);
		return p;
	}
}

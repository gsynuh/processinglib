package gsynlib.particles;

import java.util.ArrayList;

import processing.core.*;
import static processing.core.PApplet.*;

public class CachedParticle {

	public ParticlesCache cache;

	public ArrayList<PVector> points = new ArrayList<PVector>();

	public void clear() {
		points.clear();
	}

	static PVector h1 = new PVector();
	static PVector h2 = new PVector();

	public void addPoint(PVector p) {

		if (points.size() > 1) {

			int lastIndex = points.size() - 1;
			int lastLastIndex = points.size() - 2;

			h1.set(p);
			h1.sub(points.get(lastLastIndex));

			h2.set(p);
			h2.sub(points.get(lastIndex));

			float deltaDist = abs(h1.mag() - h2.mag());
			float deltaAng = abs(h1.heading() - h2.heading());
			
			if(h2.mag() < cache.minDist)
				return;

			if (deltaAng < cache.precisionAngle && deltaDist < cache.maxDist) {
				points.remove(lastIndex);
			}

		}
		
		points.add(p);
	}

}

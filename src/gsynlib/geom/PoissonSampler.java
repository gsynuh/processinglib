package gsynlib.geom;
import gsynlib.base.*;
import java.util.*;
import processing.core.*;
import static processing.core.PApplet.*;

/**
 * @author gsynuh
 * Fake Poisson Disc sampler. each getPoint() returns an unsed point in the list. Used first in the bezier loop sketch.
 */
public class PoissonSampler extends GsynlibBase {

	Bounds bounds;
	float gridSize = 10;

	ArrayList<PVector> points;
	ArrayList<Boolean> pointsTaken;

	ArrayList<PVector> searchBuffer;


	public PoissonSampler() {

		bounds = new Bounds();
		points = new ArrayList<PVector>();
		pointsTaken = new ArrayList<Boolean>();
		searchBuffer = new ArrayList<PVector>();
	}

	public void init(float _gridSize, float _x, float _y, float _w, float _h) {
		bounds.set(_x, _y,_w,_h);
		this.gridSize = _gridSize;
		build();
	}
	
	public void translatePoints(PVector t) {
		for(PVector p : points) {
			p.x += t.x;
			p.y += t.y;
		}
	}
	
	public void scalePoints(PVector s) {
		for(PVector p : points) {
			p.x *= s.x;
			p.y *= s.y;
		}
	}

	void build() {
		points.clear();
		pointsTaken.clear();
		
		float boundSize = bounds.size.x < bounds.size.y ? bounds.size.x : bounds.size.y;
		
		float divSize = boundSize / this.gridSize;

		for (int gridX = 0; gridX < this.gridSize; gridX++) {
			for (int gridY = 0; gridY < this.gridSize; gridY++) {

				PVector pos = new PVector(gridX * divSize, gridY * divSize);
				
				pos.x += bounds.position.x;
				pos.y += bounds.position.y;

				pos.x += divSize / 2;
				pos.y += divSize / 2;

				pos.x += app().random(-divSize / 8, divSize / 8);
				pos.y += app().random(-divSize / 8, divSize / 8);

				points.add(pos);
				pointsTaken.add(false);
			}
		}
	}

	Boolean IsPointIndexTaken(int index) {
		return pointsTaken.get(index);
	}

	int GetPointIndex(PVector p) {
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i) == p)
				return i;
		}

		return 0;
	}

	Boolean AllPointsTaken() {
		Boolean r = true;
		for (Boolean b : pointsTaken) {
			r = b && r;
		}

		return r;
	}

	public PVector getPointNeighboor(PVector n1, float searchRadius) {
		PVector foundPoint = null;
		searchBuffer.clear();

		for (int i = 0; i < points.size(); i++) {
			if (IsPointIndexTaken(i)) {
				continue;
			}
			PVector n2 = points.get(i);
			if (n1 == n2)
				continue;

			float dist = PVector.dist(n1, n2);
			if (dist <= searchRadius) {
				searchBuffer.add(n2);
			}
		}

		if (searchBuffer.size() == 0) {
			foundPoint = points.get(floor(app().random(0, points.size())));
			return foundPoint;
		}

		do {
			int index = floor(app().random(0, searchBuffer.size()));
			PVector p = searchBuffer.get(index);
			foundPoint = p;
		} while (foundPoint == null);

		pointsTaken.set(GetPointIndex(foundPoint), true);
		return foundPoint.copy();
	}

	public PVector getPoint() {

		PVector foundPoint = null;

		do {

			if (AllPointsTaken()) {
				foundPoint = points.get(floor(app().random(points.size())));
			} else {

				int index = floor(app().random(0, points.size()));

				if (!IsPointIndexTaken(index)) {
					PVector p = points.get(index);
					pointsTaken.set(index, true);
					foundPoint = p;
				}
			}
		} while (foundPoint == null);

		pointsTaken.set(GetPointIndex(foundPoint), true);
		return foundPoint.copy();
	}

	public void renderDebug() {
		app().noStroke();
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			Boolean taken = pointsTaken.get(i);
			app().fill(taken ? 220 : 130);
			float r = taken ? 10 : 4;
			app().ellipse(p.x, p.y, r, r);
		}
	}
}
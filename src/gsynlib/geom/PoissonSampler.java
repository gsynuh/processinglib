package gsynlib.geom;

import gsynlib.base.*;
import gsynlib.utils.GApp;
import gsynlib.utils.*;

import java.util.*;
import processing.core.*;
import static processing.core.PApplet.*;

/**
 * @author gsynuh
 * @code Based on Sebastian Lague's C# implementation
 *       (https://www.youtube.com/watch?v=7WcmyxyFO7o)
 */
public class PoissonSampler extends GsynlibBase {

	public Bounds bounds;
	float minDistance = 10;
	float cellSize = 0;

	public int maxSearchIterations = 32;

	ArrayList<PVector> points;
	
	VectorPool vecpool;

	public ArrayList<PVector> getPoints() {
		return points;
	}

	public PoissonSampler() {
		bounds = new Bounds();
		points = new ArrayList<PVector>();
		vecpool = new VectorPool(64);
	}

	public void init(float minDistance, float _x, float _y, float _w, float _h) {
		bounds.set(_x, _y, _w, _h);
		this.minDistance = minDistance;
		this.cellSize = this.minDistance / sqrt(2);
		build();
	}

	public void translatePoints(PVector t) {
		for (PVector p : points) {
			p.x += t.x;
			p.y += t.y;
		}
	}

	public void scalePoints(PVector s) {
		for (PVector p : points) {
			p.x *= s.x;
			p.y *= s.y;
		}
	}

	public PVector getRandomPoint() {
		return points.get(floor(app().random(points.size())));
	}

//-------------------------------------- POINT BUILD -----------------------------------------

	PVector candidate = new PVector();
	ArrayList<PVector> spawnPoints = new ArrayList<PVector>();
	PVector initPoint = new PVector();
	
	int cellIndex(float pos) {
		return floor(pos / cellSize);
	}

	void build() {
		points.clear();
		spawnPoints.clear();

		int gridSizeX = ceil(bounds.size.x / this.cellSize);
		int gridSizeY = ceil(bounds.size.y / this.cellSize);
		float sqrdMinDist = this.minDistance * this.minDistance;
		int[][] grid = new int[gridSizeX][gridSizeY];

		//We're doing everything as if bounds is at 0,0
		//So create the 'center' point
		
		initPoint.set(bounds.size.x * 0.5f,bounds.size.y * 0.5f);
		
		spawnPoints.add(initPoint);

		while (spawnPoints.size() > 0) {

			int spawnIndex = floor(app().random(spawnPoints.size())); // get random spawn point index
			PVector sp = spawnPoints.get(spawnIndex);

			Boolean candidateAccepted = false;

			for (int i = 0; i < maxSearchIterations; i++) {

				setRandomCandidate(sp, candidate); // create random vector starting from spawn point

				if (isPointValid(candidate, grid, gridSizeX, gridSizeY, sqrdMinDist)) { // check if point is within
																						// bounds and there are not
																						// points closer than min dist
					points.add(candidate.copy());
					
					PVector v = vecpool.get();
					v.set(candidate);
					spawnPoints.add(v);

					grid[cellIndex(candidate.x)][cellIndex(candidate.y)] = points.size();
					candidateAccepted = true;
					break;
				}
			}

			if (!candidateAccepted) {
				PVector v = spawnPoints.get(spawnIndex);
				spawnPoints.remove(spawnIndex);
				vecpool.dispose(v);
			}

		}

		// move all points to bounds position
		translatePoints(bounds.position);
		
	}

	void setRandomCandidate(PVector from, PVector candidate) {

		float a = app().random(-TWO_PI, TWO_PI);
		float d = app().random(this.minDistance, this.minDistance * 2);
		
		candidate.set(from);
		
		candidate.x += cos(a) * d;
		candidate.y += sin(a) * d;
	}

	Boolean isPointValid(PVector p, int[][] grid, int gridSizeX, int gridSizeY, float sqrdDist) {

		if (p.x < 0 || p.x > bounds.size.x || p.y < 0 || p.y > bounds.size.y)
			return false;

		int gridX = cellIndex(p.x);
		int gridY = cellIndex(p.y);

		int searchXa = max(0, gridX - 2);
		int searchXb = min(gridSizeX - 1, gridX + 2);
		int searchYa = max(0, gridY - 2);
		int searchYb = min(gridSizeY - 1, gridY + 2);

		for (int x = searchXa; x <= searchXb; x++) {
			for (int y = searchYa; y <= searchYb; y++) {

				int pointIndex = grid[x][y] - 1; // when index is set, it's 1 based

				if (pointIndex < 0) // grid cell not set
					continue;

				PVector n = points.get(pointIndex);
				float sd = GApp.sqrDist(n, p);

				if (sd < sqrdDist) // at least one point is too close
					return false;

			}
		}

		return true;
	}

//-------------------------------------- END POINT BUILD -----------------------------------------

	public Boolean debugDrawBounds = false;

	public void renderDebug() {

		app().pushStyle();
		if (debugDrawBounds) {
			app().stroke(100);
			app().strokeWeight(1);
			app().rect(bounds.position.x, bounds.position.y, bounds.size.x, bounds.size.y);
		}
		app().noStroke();

		if (points.size() > 0)
			for (int i = 0; i < points.size(); i++) {
				PVector p = points.get(i);
				app().fill(130);
				float r = 4;
				app().ellipse(p.x, p.y, r, r);
			}
		app().popStyle();
	}
}
package gsynlib.geom;
import gsynlib.base.*;
import gsynlib.utils.GApp;

import java.util.*;
import processing.core.*;
import static processing.core.PApplet.*;

/**
 * @author gsynuh
 * Fake Poisson Disc sampler. each getPoint() returns an unsed point in the list. Used first in the bezier loop sketch.
 */
public class PoissonSampler extends GsynlibBase {

	Bounds bounds;
	float minDistance = 10;
	float cellSize = 0;
	
	public int maxSearchIterations = 100;

	ArrayList<PVector> points;
	
	
	public ArrayList<PVector> getAllPoints() {
		return points;
	}


	public PoissonSampler() {

		bounds = new Bounds();
		points = new ArrayList<PVector>();
	}

	public void init(float minDistance, float _x, float _y, float _w, float _h) {
		bounds.set(_x, _y,_w,_h);
		this.minDistance = minDistance;
		this.cellSize = this.minDistance / sqrt(2);
		println("PoissonSampler minDist",minDistance,"cellSize",cellSize,"bounds",bounds);
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
	
	public PVector getRandomPoint() {
		return points.get(floor(app().random(points.size())));
	}
	
	
	PVector createCandidate(PVector center) {
		float a = app().random(0,TWO_PI);
		float d = app().random(this.minDistance,this.minDistance * 2);
		PVector p = new PVector();
		p.set(center);
		
		p.x += cos(a)*d;
		p.y += sin(a)*d;
		return p;
	}
	
	int cellX(float x) {return floor(x / cellSize);}
	int cellY(float y) {return floor(y / cellSize);}
	
	Boolean isPointValid(PVector p,int[][] grid, int gridSize, float sqrdDist) {
				
		if(p.x < 0 || p.x > bounds.size.x || p.y < 0 || p.y > bounds.size.y)
			return false;
		
		int gridX = cellX(p.x);
		int gridY = cellY(p.y);
		
		int searchXa = max(0,gridX - 2);
		int searchXb = min(gridSize,gridX + 2);
		int searchYa = max(0,gridY - 2);
		int searchYb = min(gridSize,gridY + 2);
		
		
		for(int x = searchXa; x < searchXb; x++) {
			for(int y = searchYa; y < searchYb; y++) {
				
				
				int pointIndex = grid[x][y]-1;
				
				if(pointIndex < 0)
					continue;
				
				PVector n = points.get(pointIndex);

				float sd = GApp.sqrDist(n, p);
				
					if(sd < sqrdDist)
						return false;
				
			}
		}
		
		return true;
	}

	void build() {
		points.clear();
		
		float boundSize = bounds.size.x < bounds.size.y ? bounds.size.x : bounds.size.y;
		
		int gridSize = ceil(boundSize / this.cellSize);
		
		println("PoissonSampler gridSize",gridSize,boundSize);
		
		float sqrdMinDist = this.minDistance * this.minDistance;
		int[][] grid = new int[gridSize][gridSize];
		
		ArrayList<PVector> spawnPoints = new ArrayList<PVector>();
		spawnPoints.add(new PVector(0, 0));
		
		
		while(spawnPoints.size()>0) {
			
			int spawnIndex = floor(app().random(spawnPoints.size()));
			PVector sp = spawnPoints.get(spawnIndex);
			
			Boolean candidateAccepted = false;
			
			for(int i = 0; i < maxSearchIterations; i++) {
				PVector candidate = createCandidate(sp);
				
				if(isPointValid(candidate,grid,gridSize,sqrdMinDist)) {
					points.add(candidate);
					spawnPoints.add(candidate);
					int x = cellX(candidate.x);
					int y = cellY(candidate.y);
					grid[cellX(candidate.x)][cellY(candidate.y)] = points.size();
					candidateAccepted = true;
					break;
				}
			}
			
			if(!candidateAccepted) {
				spawnPoints.remove(spawnIndex);
			}
			
		}
		
		translatePoints(bounds.position);
	}


	int GetPointIndex(PVector p) {
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i) == p)
				return i;
		}

		return 0;
	}

	public void renderDebug() {
		
		app().pushStyle();
		//app().stroke(100);
		//app().strokeWeight(1);
		//app().rect(bounds.position.x,bounds.position.y,bounds.size.x,bounds.size.y);
		
		app().noStroke();
		
		if(points.size() > 0)
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			app().fill(130);
			float r = 4;
			app().ellipse(p.x, p.y, r, r);
		}
		app().popStyle();
	}
}
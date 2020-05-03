package gsynlib.vigoxy;

import java.util.*;

import gsynlib.utils.*;
import gsynlib.base.GsynlibBase;

import processing.core.*;
import static processing.core.PApplet.*;

public class DrawCommand extends PlotterCommand {

	public ArrayList<PVector> originalPoints;
	public ArrayList<PVector> bakedPoints;

	public Boolean connectPointsOnDraw = true;

	public Boolean baked = false;
	public int drawnPointCount = -1;

	protected PlotterCanvas canvas;

	
	public DrawCommand(PlotterCanvas _canvas, PVector... values) {
		super();
		this.canvas = _canvas;

		originalPoints = new ArrayList<PVector>();

		for (int i = 0; i < values.length; i++) {
			originalPoints.add(values[i].copy());
		}

		bakedPoints = new ArrayList<PVector>();
	}

	public void prepare() {

	}

	public void reset() {
		this.drawnPointCount = -1;
		baked = false;
	}

	public void bake(PMatrix2D mat) {

		if (baked) {
			println("Already baked, reset first.");
			return;
		}

		bakedPoints.clear();

		currentMatrix = mat;
		bakePoints();

		fillBakedPoints();

		baked = true;
	}

	protected void TransformPoint(int index) {
		PVector bp = bakedPoints.get(index);
		PVector h = canvas.vecPool.get();
		currentMatrix.mult(bp, h);
		bp.set(h);
		canvas.vecPool.dispose(h);
		bakedPoints.set(index, bp);
	}

	protected PVector TransformPoint(PVector p) {
		PVector h = canvas.vecPool.get();
		currentMatrix.mult(p, h);
		p.set(h);
		canvas.vecPool.dispose(h);
		return p;
	}

	PMatrix2D currentMatrix;

	public void bakePoints() {

		PVector p1 = originalPoints.get(0);
		bakedPoints.add(p1);

		for (int i = 1; i < originalPoints.size(); i++) {
			PVector p = originalPoints.get(i);
			bakedPoints.add(p);
		}

		bakedPoints.add(p1);

		for (int i = 0; i < bakedPoints.size(); i++) {
			TransformPoint(i);
		}
	}

	public void draw() {
		app().pushStyle();
		app().pushMatrix();

		app().noFill();
		app().colorMode(HSB);

		drawBake();

		app().popMatrix();
		app().popStyle();
	}

	public void drawBake() {

		PVector initp = null;

		for (int i = 0; i < this.bakedPoints.size(); i++) {
			PVector p = this.bakedPoints.get(i);

			Boolean debugDrawLine = canvas.debugLinesDI;
			Boolean drawnPoint = this.drawnPointCount >= i;

			float sweight = 0;

			if (!this.connectPointsOnDraw)
				sweight = canvas.displayPenSize + 0.5f;
			else
				sweight = canvas.displayPenSize;

			if (debugDrawLine) {
				sweight = max(1f,sweight);
			}

			if (drawnPoint) {
				sweight += 1f;
			}

			int h = (i * 1320 + this.commandID * 60 + (int)(this.rand*255f)) % 255;

			if (debugDrawLine)
				app().stroke(h,255,drawnPoint ? 128 : 255);
			else
				app().stroke(drawnPoint ? 0 : 128);

			app().strokeWeight(sweight * canvas.screenScale);

			if (this.connectPointsOnDraw) {
				if (initp != null)
					app().line(initp.x, initp.y, p.x, p.y);
				initp = p;
			} else {
				app().point(p.x, p.y);
			}
		}
	}

	public void destroy() {

	}

	static ArrayList<PVector> fillList = new ArrayList<PVector>();

	//Subdivide segments based on max length
	protected void fillBakedPoints() {
		if (bakedPoints.size() < 2)
			return;
		
		if (!this.connectPointsOnDraw)
			return;

		fillList.clear();
		
		PVector h = canvas.vecPool.get();

		for (int i = 0; i < bakedPoints.size() - 1; i++) {
			
			PVector start = bakedPoints.get(i);
			PVector end = bakedPoints.get(i+1);
			
			
			h.set(end);
			h.sub(start);
			
			float mag = h.mag();
			float a = h.heading();

			fillList.add(start);

			if (mag > this.canvas.maxLengthToDraw) {
				float l = 0f;
				
				for (l = 0f; l < mag; l += this.canvas.maxLengthToDraw) {

					PVector p = new PVector();

					p.x = start.x + cos(a) * l;
					p.y = start.y + sin(a) * l;

					fillList.add(p);
				}
			}
		}
		
		canvas.vecPool.dispose(h);
		
		//ADD END POINT AS FILL STOPS EARLY.
		fillList.add(bakedPoints.get(bakedPoints.size()-1).copy());
		
		//COPY FILL POINTS
		

		bakedPoints.clear();
		for (int i = 0; i < fillList.size(); i++)
			bakedPoints.add(fillList.get(i));

	}
}

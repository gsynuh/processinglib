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
		GApp.helperPoint.set(0, 0);
		currentMatrix.mult(bp, GApp.helperPoint);
		bp.set(GApp.helperPoint);
		bakedPoints.set(index, bp);
	}

	protected PVector TransformPoint(PVector p) {
		GApp.helperPoint.set(0, 0);
		currentMatrix.mult(p, GApp.helperPoint);
		p.set(GApp.helperPoint);
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
		app().stroke(0);

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
				sweight = 1.2f;
			else
				sweight = 1f;

			if (debugDrawLine) {
				sweight += 1f;
			}

			if (drawnPoint) {
				sweight += 1f;
			}

			int r = (int) ((p.x * 2348f) % 255f);
			int g = (int) ((p.y * 10092f) % 255f);
			int b = (int) ((p.x * p.y * 12333f) % 255f);

			if (debugDrawLine)
				app().stroke(app().color(r, g, b));
			else
				app().stroke(0);

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

	protected void fillBakedPoints() {
		if (bakedPoints.size() < 2)
			return;
		
		if (!this.connectPointsOnDraw)
			return;

		fillList.clear();
		fillList.add(bakedPoints.get(0));

		for (int i = 0; i < bakedPoints.size(); i += 2) {
			
			PVector start = bakedPoints.get(i);
			PVector end = null;
			
			if(i+1 > bakedPoints.size()-1)
			{
				end = bakedPoints.get(bakedPoints.size()-1);
			}else {
				end = bakedPoints.get(i + 1);
			}
			
			GApp.helperPoint.set(end);
			GApp.helperPoint.sub(start);
			
			PVector rel = GApp.helperPoint.copy();
			float mag = rel.mag();
			float a = rel.heading();

			fillList.add(start);

			float div = round(ceil(mag / this.canvas.maxLengthToDraw));
			float fillDiv = mag/div;

			if (mag >= this.canvas.maxLengthToDraw) {

				for (float l = 0f; l < mag; l += fillDiv) {

					PVector p = new PVector();

					p.x = start.x + cos(a) * l;
					p.y = start.y + sin(a) * l;

					fillList.add(p);
				}
			}
			
			fillList.add(end);
		}

		bakedPoints.clear();
		for (int i = 0; i < fillList.size(); i++)
			bakedPoints.add(fillList.get(i));

	}
}

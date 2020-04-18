package gsynlib.vigoxy;

import java.util.*;

import gsynlib.utils.*;
import gsynlib.base.GsynlibBase;

import processing.core.*;
import static processing.core.PApplet.*;

public class DrawCommand extends GsynlibBase {

	public ArrayList<PVector> originalPoints;
	public ArrayList<PVector> bakedPoints;

	public Boolean connectPointsOnDraw = true;

	public Boolean dirty = true;
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
	}

	public void bake() {
		bakedPoints.clear();
		bakePoints();
		reset();
		dirty = false;
	}

	public void bakePoints() {

		PVector p1 = originalPoints.get(0);
		bakedPoints.add(p1);

		for (int i = 1; i < originalPoints.size(); i++) {
			PVector p = originalPoints.get(i);
			bakeFill(p);
			bakedPoints.add(p);
		}

		bakeFill(p1);
		bakedPoints.add(p1);
	}

	public void draw() {
		app().pushMatrix();
		app().pushStyle();

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

			if (!this.connectPointsOnDraw) {
				sweight = 1.2f;
			}else
				sweight = 1;

			if (debugDrawLine) {
				sweight += 1;
			}

			if (drawnPoint) {
				sweight += 1;
			}

			int r = (int) ((p.x * 2348f) % 255f);
			int g = (int) ((p.y * 10092f) % 255f);
			int b = (int) ((p.x * p.y * 12333f) % 255f);

			if (debugDrawLine)
				app().stroke(app().color(r, g, b));
			else
				app().stroke(0);

			app().strokeWeight(this.canvas.screenScale * sweight);

			if (this.connectPointsOnDraw) {
				if (initp != null)
					app().line(initp.x, initp.y, p.x, p.y);
				initp = p;
			}
			else {
				app().point(p.x, p.y);
			}
		}
	}

	public void destroy() {

	}

	protected void bakeFill(PVector end) {
		if (bakedPoints.size() < 1)
			return;

		PVector start = bakedPoints.get(bakedPoints.size() - 1);
		GApp.helperPoint.set(end);
		GApp.helperPoint.sub(start);

		float mag = GApp.helperPoint.mag();
		float a = GApp.helperPoint.heading();

		float fillDiv = this.canvas.maxLengthToDraw / 4.0f;

		if (mag >= this.canvas.maxLengthToDraw) {

			for (float l = 0f; l < mag; l += fillDiv) {

				PVector p = new PVector();

				p.x = start.x + cos(a) * l;
				p.y = start.y + sin(a) * l;

				bakedPoints.add(p);
			}
		}
	}
}

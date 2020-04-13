package gsynlib.vigoxy;

import java.util.*;

import gsynlib.base.GsynlibBase;
import processing.core.*;
import static processing.core.PApplet.*;

public class DrawCommand extends GsynlibBase {

	static final PVector helperPoint = new PVector();

	public static final int UNKNOWN = 0;
	public static final int POINT = 1;
	public static final int LINE = 2;
	public static final int RECT = 3;
	public static final int CIRCLE = 4;
	public static final int LOOP = 5;

	public ArrayList<PVector> originalPoints;
	public ArrayList<PVector> bakedPoints;

	public Boolean dirty = true;
	public int type = UNKNOWN;
	public int drawCount = 0;

	protected PlotterCanvas canvas;

	public DrawCommand(PlotterCanvas _canvas, PVector ...values) {
		this.canvas = _canvas;

		originalPoints = new ArrayList<PVector>();
		
		for(int i = 0; i < values.length; i++) {
			originalPoints.add(values[i].copy());
		}
		
		bakedPoints = new ArrayList<PVector>();
	}

	public void prepare() {
		
	}

	public void bake() {
		bakedPoints.clear();
		bakePoints();
		drawCount = 0;
		dirty = false;
	}
	
	public void bakePoints() {
		
		PVector p1 = originalPoints.get(0);
		bakedPoints.add(p1);
		
		for(int i = 1; i < originalPoints.size(); i++) {
			PVector p = originalPoints.get(i);
			bakeFill(p);
			bakedPoints.add(p);
		}
		
		bakeFill(p1);
		bakedPoints.add(p1);
	}

	public void draw(int type) {
		app().pushMatrix();
		app().pushStyle();
		
		app().noFill();
		app().stroke(0);
		app().strokeWeight(canvas.displayScale * 0.02f);
		
		switch (type) {
		default:
			drawBake();
			break;
		case 0:
			drawOriginal();
			break;
		}
		
		app().popMatrix();
		app().popStyle();
	}

	public void drawOriginal() {
	}

	public void drawBake() {
		
		if(this.bakedPoints.size() == 1) {
			PVector p = this.bakedPoints.get(0);
			app().point(p.x,p.y);
		}else {
		app().beginShape();
		for(PVector bp : this.bakedPoints) {
			app().vertex(bp.x,bp.y);
		}
			app().endShape(OPEN);
		}
	}

	public void destroy() {

	}

	protected void bakeFill(PVector end) {
		if (bakedPoints.size() < 1)
			return;

		PVector start = bakedPoints.get(bakedPoints.size() - 1);
		helperPoint.set(end);
		helperPoint.sub(start);

		float mag = helperPoint.mag();
		float a = helperPoint.heading();
		
		float fillDiv =  this.canvas.maxLengthToDraw / 5.0f;

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

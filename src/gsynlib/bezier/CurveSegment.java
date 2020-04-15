package gsynlib.bezier;

import gsynlib.base.*;
import gsynlib.geom.*;
import gsynlib.utils.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class CurveSegment extends GsynlibBase {

	public PVector p1 = new PVector();
	public PVector p2 = new PVector();
	public PVector p3 = new PVector();
	public PVector p4 = new PVector();

	public PVector t1 = new PVector();
	public PVector t2 = new PVector();

	public int id = 0;

	public CurveSegment(int id) {
		this.id = id;
	}

	public void initialize() {
		createTangents();
	}

	public void initialize(PVector _p1, PVector _p4) {
		this.p1.set(_p1);
		this.p4.set(_p4);
		createTangents();
	}

	public void setTrangent(int i, float angle, float dist) {
		PVector tp = i == 0 ? this.p2 : this.p3;
		PVector p = i == 0 ? this.p1 : this.p4;

		GApp.helperPoint.x = cos(angle) * dist;
		GApp.helperPoint.y = sin(angle) * dist;
		GApp.helperPoint.add(p);

		tp.set(GApp.helperPoint);

		createTangents();
	}

	void createTangents() {
		GApp.helperPoint.set(p2);
		GApp.helperPoint.sub(p1);
		t1.set(GApp.helperPoint);
		GApp.helperPoint.set(p3);
		GApp.helperPoint.sub(p4);
		t2.set(GApp.helperPoint);
	}

	public PVector sampleCurve(float t, Boolean reverse) {
		float x = 0;
		float y = 0;

		t = constrain(t, 0, 1);

		if (reverse) {
			x = app().bezierPoint(p4.x, p3.x, p2.x, p1.x, t);
			y = app().bezierPoint(p4.y, p3.y, p2.y, p1.y, t);
		} else {
			x = app().bezierPoint(p1.x, p2.x, p3.x, p4.x, t);
			y = app().bezierPoint(p1.y, p2.y, p3.y, p4.y, t);
		}

		return new PVector(x, y);
	}

	public Bounds getBounds() {
		Bounds b = new Bounds(this.p1.x, this.p1.y, 0, 0);

		b.Encapsulate(this.p2);
		b.Encapsulate(this.p3);
		b.Encapsulate(this.p4);

		return b;
	}

	public void render() {
		app().pushStyle();
		app().strokeWeight(1);

		app().noFill();
		app().strokeWeight(2);
		app().stroke(64);

		app().bezier(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);

		app().popStyle();
	}

	void drawPoint(int i, PVector p, float size) {
		app().pushMatrix();
		app().noStroke();
		app().fill(0);
		app().translate(p.x, p.y);
		app().ellipse(0, 0, size, size);
		app().text("c" + this.id + "p" + i, 5, 2 + this.id * 10);
		app().popMatrix();
	}

	void renderDebug() {
		app().pushStyle();
		app().strokeWeight(1);

		app().pushMatrix();
		app().stroke(0, 120, 255);
		app().translate(p1.x, p1.y);
		app().line(0, 0, t1.x, t1.y);
		app().popMatrix();

		app().pushMatrix();
		app().stroke(255, 120, 80);
		app().translate(p4.x, p4.y);
		app().line(0, 0, t2.x, t2.y);
		app().popMatrix();

		float r1 = 5;
		float r2 = 1;

		drawPoint(1, p1, r1);
		drawPoint(2, p2, r2);
		drawPoint(3, p3, r2);
		drawPoint(4, p4, r1);

		app().popStyle();
	}

	public void translateCurve(PVector translation) {
		this.p1.add(translation);
		this.p2.add(translation);
		this.p3.add(translation);
		this.p4.add(translation);
		createTangents();
	}

	public void scaleCurve(PVector scale) {
		this.p1.x *= scale.x;
		this.p1.y *= scale.y;
		this.p2.x *= scale.x;
		this.p2.y *= scale.y;
		this.p3.x *= scale.x;
		this.p3.y *= scale.y;
		this.p4.x *= scale.x;
		this.p4.y *= scale.y;
		createTangents();
	}
}
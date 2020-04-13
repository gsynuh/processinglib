package gsynlib.bezier;

import gsynlib.base.*;
import gsynlib.geom.*;
import java.util.*;
import processing.core.*;

public class BezierLoop extends GsynlibBase {

	public Bounds bounds;
	public float bakePrecision = 20;

	PoissonSampler poisson;

	float w;
	float h;
	float m;

	ArrayList<CurveSegment> curves = new ArrayList<CurveSegment>();
	ArrayList<PVector> points = new ArrayList<PVector>();

	public BezierLoop() {
		poisson = new PoissonSampler();
	}

	public void init(float numCurves, float _w, float _h, float _m) {

		this.w = _w;
		this.h = _h;
		this.m = _m;

		curves.clear();
		points.clear();

		float minTangentDistance = this.w;
		float maxTangentDistance = this.w * 1.6f;

		float poissonRadius = minTangentDistance;
		poisson.init(poissonRadius, -maxTangentDistance, -maxTangentDistance, maxTangentDistance * 2,
				maxTangentDistance * 2);

		PVector p1 = poisson.getPoint();
		PVector tangentTarget = poisson.getPointNeighboor(p1, maxTangentDistance);

		float tanAngle = tangentTarget.sub(p1).heading() - PApplet.PI;
		float tanRadius = app().random(minTangentDistance, maxTangentDistance * 2);

		for (int i = 0; i < numCurves; i++) {

			CurveSegment cs = new CurveSegment(i);

			PVector p4 = poisson.getPoint();

			cs.initialize(p1, p4);

			tangentTarget = poisson.getPointNeighboor(p4, maxTangentDistance * 2);
			float a = tangentTarget.sub(p4).heading() - PApplet.PI;

			cs.setTrangent(0, tanAngle, tanRadius); // ALIGN WITH PREVIOUS
			cs.setTrangent(1, a, app().random(minTangentDistance, maxTangentDistance * 2));

			p1 = cs.p4;
			tanAngle = cs.t2.heading() - PApplet.PI;
			tanRadius = cs.t2.mag();

			curves.add(cs);
		}

		// ADD CLOSING CURVE
		CurveSegment csB = curves.get(curves.size() - 1);
		CurveSegment csA = curves.get(0);
		CurveSegment csClose = new CurveSegment((int) numCurves);

		csClose.initialize(csB.p4, csA.p1);
		csClose.setTrangent(0, csB.t2.heading() - PApplet.PI, csB.t2.mag());
		csClose.setTrangent(1, csA.t1.heading() - PApplet.PI, csA.t1.mag());

		curves.add(csClose);

		bake();
	}

	public void bake() {
		// BAKE
		bakePoints(bakePrecision);
		calcBoundsFromBake();
		moveAndScaleCurve();
		calcBoundsFromBake();

		// MARGIN

		bounds.position.x -= m;
		bounds.position.y -= m;
		bounds.size.x += m * 2;
		bounds.size.y += m * 2;
	}

	public ArrayList<CurveSegment> getCurves() {
		return this.curves;
	}
	
	public PVector sampleCurve(float t) {

		t = PApplet.constrain(t, 0, 1);

		float timeDiv = 1.0f / (curves.size());
		float curveIndex = PApplet.floor(t * curves.size());

		if (curveIndex >= curves.size())
			curveIndex = curves.size() - 1;

		float t2 = PApplet.map(t, curveIndex * timeDiv, (curveIndex + 1) * timeDiv, 0, 1);

		CurveSegment cs = curves.get((int) curveIndex);
		PVector p = cs.sampleCurve(t2, false);
		return p;
	}

	public void render() {
		app().pushMatrix();
		for (int i = 0; i < curves.size(); i++) {
			CurveSegment cs = curves.get(i);
			cs.render();
		}
		app().popMatrix();
	}

	public void renderBake() {
		app().pushMatrix();
		app().noFill();
		app().stroke(0);
		app().strokeWeight(1);
		app().beginShape();
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			app().vertex(p.x, p.y);
		}

		app().endShape(PApplet.OPEN);
		app().popMatrix();
	}

	public void renderDebug() {
		app().pushMatrix();
		poisson.renderDebug();

		for (int i = 0; i < curves.size(); i++) {
			CurveSegment cs = curves.get(i);
			cs.renderDebug();
		}
		app().popMatrix();
	}

	void moveAndScaleCurve() {
		PVector sizeDiv = bounds.position.copy();
		sizeDiv.mult(-1);

		PVector s = new PVector(w, h);
		s.x = s.x / bounds.size.x;
		s.y = s.y / bounds.size.y;

		for (int i = 0; i < curves.size(); i++) {
			CurveSegment cs = curves.get(i);
			cs.translateCurve(sizeDiv);
			cs.scaleCurve(s);
		}

		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			p.x += sizeDiv.x;
			p.y += sizeDiv.y;

			p.x *= s.x;
			p.y *= s.y;
		}

		poisson.translatePoints(sizeDiv);
		poisson.scalePoints(s);
	}

	void bakePoints(float numDiv) {

		points.clear();

		float div = 1 / numDiv;
		div = div / curves.size();

		PVector p = new PVector();
		for (float t = 0; t < 1 + div; t += div) {
			PVector pa = sampleCurve(t);

			if (pa.x == p.x && pa.y == p.y)
				continue;

			p.set(pa);
			points.add(pa);
		}
	}
	
	void calcBoundsFromBake() {
		bounds = new Bounds(points.get(0));

		for (int i = 0; i < points.size(); i++) {
			bounds.Encapsulate(points.get(i));
		}
	}
}
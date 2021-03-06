package gsynlib.bezier;

import gsynlib.base.*;
import gsynlib.geom.*;
import gsynlib.utils.*;

import java.util.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class BezierLoop extends GsynlibBase {

	Bounds targetBounds = new Bounds();
	
	public Bounds bounds;
	public float bakePrecision = 20;

	PoissonSampler poisson;
	VectorPool vecPool;

	float m;

	ArrayList<CurveSegment> curves = new ArrayList<CurveSegment>();
	ArrayList<PVector> points = new ArrayList<PVector>();
	
	public ArrayList<CurveSegment> getCurves() {
		return this.curves;
	}
	
	public ArrayList<PVector> getBakedPoints() {
		return this.points;
	}

	public BezierLoop() {
		vecPool = new VectorPool(64);
		poisson = new PoissonSampler();
		setDefaultTargetBounds();
		targetBounds.dirty = false;
	}
	
	public void setTargetBounds(Bounds b) {
		targetBounds.set(b);
		targetBounds.dirty = true;
	}
	
	void setDefaultTargetBounds() {
		targetBounds.set(0,0,app().width,app().height);
	}
	
	
	ArrayList<PVector> poissonPoints = new ArrayList<PVector>();
	
	PVector getRandom() {
		int index = getRandomPoissonPointIndex();
		PVector p = poissonPoints.get(index);
		poissonPoints.remove(p);
		return p;
	}
	
	int getRandomPoissonPointIndex() {
		return floor(app().random(poissonPoints.size()));
	}

	public void init(float numCurves, float _m) {

		if(!targetBounds.dirty) {
			setDefaultTargetBounds();
		}
		
		this.m = _m;
		
		
		curves.clear();
		points.clear();
		poissonPoints.clear();

		float maxDist = this.targetBounds.size.x > this.targetBounds.size.y ? this.targetBounds.size.y : this.targetBounds.size.x;
		
		float minTangentDistance = maxDist * 0.8f;
		float maxTangentDistance = maxDist * 1.2f;
		
		float numPointsNeededPerCurve = (numCurves+1) * 2 + 1;
		
		float minDistance = ceil(this.targetBounds.size.x / sqrt(numPointsNeededPerCurve));

		float s = maxDist/sqrt(2);
		
		poisson.init(minDistance, 
				this.targetBounds.position.x - s, 
				this.targetBounds.position.y - s,
				this.targetBounds.size.x + s*2,
				this.targetBounds.size.y + s*2);
		
		
		
		poissonPoints.addAll(poisson.getPoints());

		PVector p1 = getRandom();
		PVector tangentTarget = getRandom();

		float tanAngle = tangentTarget.sub(p1).heading() - PI;
		float tanRadius = app().random(minTangentDistance, maxTangentDistance * 2);

		for (int i = 0; i < numCurves; i++) {

			CurveSegment cs = new CurveSegment(i);
			cs.vec = vecPool;

			PVector p4 = getRandom();

			cs.initialize(p1, p4);

			tangentTarget = getRandom();
			float a = tangentTarget.sub(p4).heading() - PI;

			cs.setTrangent(0, tanAngle, tanRadius); // ALIGN WITH PREVIOUS
			cs.setTrangent(1, a, app().random(minTangentDistance, maxTangentDistance * 2));

			p1 = cs.p4;
			tanAngle = cs.t2.heading() - PI;
			tanRadius = cs.t2.mag();

			curves.add(cs);
		}

		// ADD CLOSING CURVE
		CurveSegment csB = curves.get(curves.size() - 1);
		CurveSegment csA = curves.get(0);
		CurveSegment csClose = new CurveSegment((int) numCurves);
		csClose.vec = vecPool;

		csClose.initialize(csB.p4, csA.p1);
		csClose.setTrangent(0, csB.t2.heading() - PI, csB.t2.mag());
		csClose.setTrangent(1, csA.t1.heading() - PI, csA.t1.mag());

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
		
		bounds.set(
				bounds.position.x - m,
				bounds.position.y - m,
				bounds.size.x + m*2f,
				bounds.size.y + m*2f
				);
	}
	
	public PVector sampleCurve(float t) {

		t = constrain(t, 0, 1);

		float timeDiv = 1.0f / (curves.size());
		float curveIndex = floor(t * curves.size());

		if (curveIndex >= curves.size())
			curveIndex = curves.size() - 1;

		float t2 = map(t, curveIndex * timeDiv, (curveIndex + 1) * timeDiv, 0, 1);

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

		app().endShape(OPEN);
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

		PVector s = new PVector(targetBounds.size.x, targetBounds.size.y);
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
		
		for (int i = 0; i < points.size(); i++) {
			PVector p = points.get(i);
			p.x += targetBounds.position.x;
			p.y += targetBounds.position.y;
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
package gsynlib.geom;

import java.util.*;

import processing.core.*;

import static processing.core.PApplet.*;

public class HilbertC2D {

	int order = 1;
	int gridSize = 0;

	ArrayList<PVector> points = new ArrayList<PVector>();
	ArrayList<Integer> gridIndices = new ArrayList<Integer>();

	public HilbertC2D(int _order) {
		this.setOrder(_order);
	}

	PVector[] base = { new PVector(0, 0), new PVector(0, 1), new PVector(1, 1), new PVector(1, 0) };

	PVector hilbert(int i) {
		int index = i & 3;
		PVector v = base[index].copy();

		for (int j = 1; j < order; j++) {
			i = i >>> 2;
			index = i & 3;

			float len = pow(2, j);
			float t = 0;

			if (index == 0) {
				t = v.x;
				v.x = v.y;
				v.y = t;
			} else if (index == 1) {
				v.y += len;
			} else if (index == 2) {
				v.x += len;
				v.y += len;
			} else if (index == 3) {
				t = len - 1 - v.x;
				v.x = len - 1 - v.y;
				v.y = t;
				v.x += len;
			}
		}
		return v;
	}

	public void setOrder(int _order) {
		this.order = _order;

		gridSize = (int) pow(2, this.order);
		int total = gridSize * gridSize;

		points.clear();
		gridIndices.clear();

		for (int x = 0; x <= gridSize; x++) {
			for (int y = 0; y <= gridSize; y++) {
				gridIndices.add(0);
			}
		}

		for (int i = 0; i < total; i++) {
			PVector p = hilbert(i);

			int pointIndex = points.size();
			p.z = pointIndex;
			points.add(p);

			float w = gridSize;

			float gX = floor(p.x);
			float gY = floor(p.y);

			int index = floor(gX + gY * w);

			index = index <= 0 ? 0 : index;
			index = index > floor(w * w) ? floor(w * w) : index;

			gridIndices.set(index, pointIndex);

		}

	}

	public int getOrder() {
		return this.order;
	}

	public int getSize() {
		return this.gridSize;
	}

	public int getPointCount() {
		return points.size();
	}

	public PVector getPoint(int id) {
		return points.get(id);
	}

	public float sampleTime(float _x, float _y) {
		float t = 0f;

		float w = gridSize;
		float hw = 1f / ((float) gridSize) * 0.5f;

		_x = constrain(_x + hw, 0, 1);
		_y = constrain(_y + hw, 0, 1);

		float gx = constrain(floor(_x * w), 0, gridSize - 1);
		float gy = constrain(floor(_y * w), 0, gridSize - 1);

		int i = floor(gx + gy * w);

		i = i <= 0 ? 0 : i;
		i = i > floor(w * w) ? floor(w * w) : i;

		int pointIndex = gridIndices.get(i);
		t = map(pointIndex, 0, points.size() - 1, 0, 1);
		t = constrain(t, 0, 1);

		return t;
	}

	public PVector samplePoint(float t) {
		t = constrain(t, 0, 1);

		float indices = points.size() - 1;

		int indexA = floor(t * indices);
		int indexB = indexA + 1 <= indices ? indexA + 1 : indexA;

		if (indexA != indexB) {
			t = map(t * indices, indexA, indexB, 0, 1);
		} else {
			t = map(t * indices, 0, indices, 0, 1);
		}

		PVector a = getPoint(indexA);
		PVector b = getPoint(indexB);

		PVector p = new PVector(lerp(a.x, b.x, t), lerp(a.y, b.y, t));

		p.z = a.z;

		return p;

	}

}
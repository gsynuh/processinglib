package gsynlib.geom;
import gsynlib.base.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class Bounds extends GsynlibBase {
	
	float minA = -Float.MAX_VALUE;
	float maxA = Float.MAX_VALUE;
	
	public PVector position = new PVector();
	public PVector size = new PVector();

	public Boolean dirty = true;

	public Bounds() {
		position.x = position.y = -100;
		size.x = size.y = 200;
	}

	public Bounds(PVector p) {
		float m = 0;
		position.set(p);
		position.x -= m;
		position.y -= m;
		size.set(m * 2, m * 2);
	}
	
	public Bounds(PVector _pos, PVector _size) {
		position.set(_pos);
		size.set(_size);
	}

	public Bounds(float _x, float _y, float _w, float _h) {
		position.x = _x;
		position.y = _y;
		size.x = _w;
		size.y = _h;
	}

	public void copyFrom(Bounds b) {
		this.position.set(b.position);
		this.size.set(b.size);
	}
	
	public PVector getRandom() {
		return new PVector(
				position.x + app().random(0,size.x),
				position.y + app().random(0,size.y)
				);
	}

	public PVector getCenter() {
		PVector p = new PVector();
		p.x = (float) (position.x + size.x * 0.5);
		p.y = (float) (position.y + size.y * 0.5);
		return p;
	}

	public PVector getBottomRight() {
		return PVector.add(position, size);
	}

	public void Encapsulate(PVector p) {
		this.Encapsulate(new Bounds(p));
	}

	static PVector horizontal = new PVector();
	static PVector vertical = new PVector();

	public void set(Bounds b) {
		this.position.set(b.position);
		this.size.set(b.size);
	}

	public Boolean Contains(PVector p) {

		Boolean ch = p.x >= position.x && p.x <= position.x + size.x;
		Boolean cv = p.y >= position.y && p.y <= position.y + size.y;

		return ch && cv;
	}

	public void Encapsulate(Bounds b) {

		if (b == null)
			return;

		horizontal.x = min(maxA, this.position.x);
		horizontal.x = min(horizontal.x, b.position.x);

		vertical.x = min(maxA, this.position.y);
		vertical.x = min(vertical.x, b.position.y);

		horizontal.y = max(minA, this.position.x + this.size.x);
		horizontal.y = max(horizontal.y, b.position.x + b.size.x);

		vertical.y = max(minA, this.position.y + this.size.y);
		vertical.y = max(vertical.y, b.position.y + b.size.y);

		this.position.x = horizontal.x;
		this.position.y = vertical.x;

		this.size.x = horizontal.y - horizontal.x;
		this.size.y = vertical.y - vertical.x;

		this.size.x = this.size.x < 0 ? -this.size.x : this.size.x;
		this.size.y = this.size.y < 0 ? -this.size.y : this.size.y;
	}

	public String toString() {
		return "[Bounds x:" + this.position.x + " y:" + this.position.y + " w:" + this.size.x + " h:" + this.size.y
				+ " ]";
	}
}
package gsynlib.image;

import static processing.core.PApplet.*;

//Object to hold and process RGB colors with float components
public class ColorFloat {
	public float r = 0;
	public float g = 0;
	public float b = 0;
	public float a = 1;

	public ColorFloat() {
    }

	public ColorFloat(float _r,float _g,float _b) {
      this.r = _r;
      this.g = _g;
      this.b = _b;
    }

	public ColorFloat(float _r,float _g,float _b, float _a) {
      this.r = _r;
      this.g = _g;
      this.b = _b;
      this.a = _a;
    }
	
	@Override
	public String toString() {
		return "[ColorFloat r:"+this.r+" g:"+this.g+" b:" +this.b+ " a:"+this.a+"]";
	}
	
	public void Quantize(float factor) {
		this.r = round(this.r*factor)/factor;
		this.g = round(this.g*factor)/factor;
		this.b = round(this.b*factor)/factor;
		this.Clamp();
	}

	public void sub(ColorFloat b) {
		this.a = this.a - b.a;
		this.r = this.r - b.r;
		this.g = this.g - b.g;
		this.b = this.b - b.b;
	}
	
	public static ColorFloat sub(ColorFloat a, ColorFloat b) {
		ColorFloat result = new ColorFloat();
		result.a = a.a - b.a;
		result.r = a.r - b.r;
		result.g = a.g - b.g;
		result.b = a.b - b.b;
		return result;
	}

	public void CopyTo(ColorFloat b) {
		b.a = this.a;
		b.r = this.r;
		b.g = this.g;
		b.b = this.b;
	}

	public ColorFloat Copy() {
		ColorFloat result = new ColorFloat();
		result.a = this.a;
		result.r = this.r;
		result.g = this.g;
		result.b = this.b;
		return result;
	}
	
	public static ColorFloat add(ColorFloat a, ColorFloat b) {
		ColorFloat result = new ColorFloat();
		result.a = a.a + b.a;
		result.r = a.b + b.r;
		result.g = a.g + b.g;
		result.b = a.b + b.b;
		return result;
	}

	public void add(ColorFloat b) {
		this.a += b.a;
		this.r += b.r;
		this.g += b.g;
		this.b += b.b;
	}

	public void Clamp() {
		this.a = this.a > 1f ? 1f : this.a < 0f ? 0f : this.a;
		this.r = this.r > 1f ? 1f : this.r < 0f ? 0f : this.r;
		this.g = this.g > 1f ? 1f : this.g < 0f ? 0f : this.g;
		this.b = this.b > 1f ? 1f : this.b < 0f ? 0f : this.b;
	}
	
	public static ColorFloat mult(ColorFloat c, float t) {
		ColorFloat result = new ColorFloat();
		c.CopyTo(result);
		result.a *= t;
		result.r *= t;
		result.g *= t;
		result.b *= t;
		return result;
	}

	public void mult(float t) {
		this.a *= t;
		this.r *= t;
		this.g *= t;
		this.b *= t;
	}

	public static ColorFloat fromColor(long c) {
		float f = 1 / (float) 0xFF;
		ColorFloat col = new ColorFloat();
		col.a = ((c >> 24) & 0xFF) * f;
		col.r = ((c >> 16) & 0xFF) * f;
		col.g = ((c >> 8) & 0xFF) * f;
		col.b = ((c >> 0) & 0xFF) * f;
		return col;
	}
	
	public static int toInt32(ColorFloat c) {
		c = c.Copy();
		c.Clamp();
		int r = (int)constrain(c.r * 255,0,255);
		int g = (int)constrain(c.g * 255,0,255);
		int b = (int)constrain(c.b * 255,0,255);
		int a = (int)constrain(c.a * 255,0,255);
		
		return a << 24 | r << 16 | g << 8 | b ;
	}

	public static ColorFloat toLuminance(ColorFloat col) {
		ColorFloat ret = new ColorFloat();
		ret.a = 1.0f;
		float lum = col.r * 0.299f + col.g * 0.587f + col.b * 0.114f;
		ret.r = ret.g = ret.b = lum;
		return ret;
	}
}

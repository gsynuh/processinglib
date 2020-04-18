package gsynlib.image;

import processing.core.*;

//Floyd-Steinberg dithering
//using float rgb colors
public class Dithering {

	public float quantizeFactor = 8;
	public Boolean luminance = false;

	public int width = 0;
	public int height = 0;
	ColorFloat[][] colors;
	
	public ColorFloat getColor(int x, int y) {
		if (x < 0 || x >= width)
			return null;
		if (y < 0 || y >= height)
			return null;

		return colors[x][y].Copy();
	}

	// error propagation matrix
	float[] errorProp = new float[] { 7f / 16f, 5f / 16f, 3f / 16f, 1f / 16f };

	public Dithering() {
	}

	public Dithering(float colorQuantizeFactor) {
		this.quantizeFactor = colorQuantizeFactor;
	}


	void SetColor(ColorFloat c, int x, int y) {
		if (x < 0 || x >= width)
			return;
		if (y < 0 || y >= height)
			return;

		colors[x][y] = c;
	}

	public void CreateFilter(PImage image) {
		image.loadPixels();

		this.width = image.width;
		this.height = image.height;

		colors = new ColorFloat[this.width][this.height];

		// CREATE COLORFLOAT ARRAY
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {

				int pixelIndex = x + y * this.width;

				int color32 = image.pixels[pixelIndex];

				ColorFloat c = ColorFloat.fromColor(color32);

				if (luminance) {
					c = ColorFloat.toLuminance(c);
				}

				colors[x][y] = c;
			}
		}

		// DITHERING
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {

				ColorFloat oldColor = getColor(x, y);
				ColorFloat newColor = oldColor.Copy();
				newColor.Quantize(this.quantizeFactor);

				SetColor(newColor, x, y);

				ColorFloat err = ColorFloat.sub(oldColor, newColor);

				ColorFloat right = getColor(x + 1, y);
				ColorFloat bottomLeft = getColor(x - 1, y + 1);
				ColorFloat bottom = getColor(x, y + 1);
				ColorFloat bottomRight = getColor(x + 1, y + 1);

				// PROPAGATE ERROR

				if (right != null) {
					right.add(ColorFloat.mult(err, errorProp[0]));
					SetColor(right, x + 1, y);
				}

				if (bottomLeft != null) {
					bottomLeft.add(ColorFloat.mult(err, errorProp[1]));
					SetColor(bottomLeft, x - 1, y + 1);
				}

				if (bottom != null) {
					bottom.add(ColorFloat.mult(err, errorProp[2]));
					SetColor(bottom, x, y + 1);
				}

				if (bottomRight != null) {
					bottomRight.add(ColorFloat.mult(err, errorProp[3]));
					SetColor(bottomRight, x + 1, y + 1);
				}

			}
		}

		image.updatePixels();
	}

	public void ApplyTo(PImage image) {

		CreateFilter(image);

		image.loadPixels();

		// APPLY
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				ColorFloat c = getColor(x,y);

				if (c == null)
					continue;

				int pixelIndex = x + y * this.width;
				image.pixels[pixelIndex] = ColorFloat.toInt32(c);
			}
		}

		image.updatePixels();
	}

}

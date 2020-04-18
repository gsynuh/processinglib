import gsynlib.image.*;

PImage originalCat;
PImage cat;
Dithering ditheringFilter;

void setup() {
  size(1024, 1024);

  noSmooth();

  originalCat = loadImage("cat.png");

  ditheringFilter = new Dithering();
  init();
}

void init() {
  ditheringFilter.luminance = !ditheringFilter.luminance;

  if (ditheringFilter.luminance) {
    ditheringFilter.quantizeFactor = 1;
  } else {
    ditheringFilter.quantizeFactor = 3;
  }

  cat = loadImage("cat.png");

  ditheringFilter.ApplyTo(cat);
  ditheringFilter.CreateFilter(originalCat);

  loop();
}

void mousePressed() {
  init();
}

void DrawImage() {
  pushStyle();

  int pixelJump = 5;

  strokeWeight(pixelJump);
  strokeCap(PROJECT);

  for (int y = 0; y < ditheringFilter.height; y+= pixelJump) {
    for (int x = 0; x < ditheringFilter.width; x+= pixelJump) {
      ColorFloat c = ditheringFilter.getColor(x, y);
      color col = ColorFloat.toInt32(c);

      float r = red(col);
      float g = green(col);
      float b = blue(col);

      if ( r >= 255 && r == g && g == b) {
        continue;
      }

      stroke(col);
      point(x, y);
    }
  }
  popStyle();
}

void draw() {
  background(255);
  image(originalCat, 0, 0);
  image(cat, 0, 512);

  pushMatrix();
  translate(512, 0);
  DrawImage();
  popMatrix();

  fill(0);
  rect(512, 512, 512, 512);

  drawLabel(0, 0, 200, "ORIGINAL");
  drawLabel(512, 0, 200, "DRAWN USING FILTER DATA");
  drawLabel(0, 512, 200, "APPLIED TO IMAGE PIXELS");
  noLoop();
}

void drawLabel(int x, int y, int labelSize, String txt) {
  pushStyle();
  pushMatrix();
  noStroke();
  fill(255);
  translate(x, y);
  rect(0, 0, labelSize, 20);
  fill(0);
  text(txt, 10, 15);
  popStyle();
  popMatrix();
}

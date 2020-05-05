import gsynlib.utils.*;

Grid<Integer> grid;

void setup() {
  size(800, 800,FX2D);
  noSmooth();

  grid = new Grid<Integer>(25, 25);
  grid.loop = true;

  colorMode(HSB);

  for (int x = 0; x < grid.width(); x++) {
    for (int y = 0; y < grid.height(); y++) {
      grid.set(x, y, color(noise(x*0.2f, y*0.2f) * 255, 255, 255));
    }
  }
}

void drawAroundMouse() {
  float cellW = width / grid.width();
  float cellH = height / grid.height();

  int mX = floor(mouseX/cellW);
  int mY = floor(mouseY/cellH);
  int m = 2;

  for (int x = mX - m; x < mX + m; x++) {
    for (int y = mY - m; y < mY + m; y++) {
      color col = int(grid.get(x, y));
      fill(col);
      rect(x*cellW, y*cellH, cellW, cellH);
    }
  }
}

void draw() {

  background(255);
  noStroke();
  float cellW = width / grid.width();
  float cellH = height / grid.height();

  for (int x = 0; x < grid.width(); x++) {
    for (int y = 0; y < grid.height(); y++) {
      color col = int(grid.get(x, y));
      float h = hue(col);

      fill(h, 50, 255);
      rect(x*cellW, y*cellH, cellW, cellH);
    }
  }
  
  drawAroundMouse();
}

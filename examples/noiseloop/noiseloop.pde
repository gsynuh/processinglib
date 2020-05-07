import gsynlib.utils.*;

NoiseLoop blobLoops;
NoiseLoop timeLoop;

float numPoints = 64;

void setup() {
  size(800, 800, FX2D);
  GApp.set(this);

  blobLoops = new NoiseLoop(4);
  timeLoop = new NoiseLoop(8);
}

void drawTubes(float w) {

  float gridSize = 12;
  float cellSize = w / gridSize;
  float totalFrames = 300;

  for (float x =0; x < gridSize; x++) {
    for (float y = 0; y < gridSize; y++) {

      int id = floor(x%gridSize + y*gridSize);

      float frame = frameCount % totalFrames;
      float z = timeLoop.get(id, frame/totalFrames) * 5;

      beginShape();
      for (float i = 0; i <= numPoints; i++) {
        float t = i/numPoints;

        float n = blobLoops.get(id, t, z);
        float r = cellSize/4 + n*cellSize/4;

        float a = t * TWO_PI;
        vertex(
          cos(a)*r + x*cellSize + cellSize/2, 
          sin(a)*r + y*cellSize + cellSize/2
          );
      }
      endShape(CLOSE);
    }
  }
}

void draw() {

  background(220);
  fill(32);

  float m = width/12;
  translate(m, m);
  float w = width-m*2;
  drawTubes(w);
}

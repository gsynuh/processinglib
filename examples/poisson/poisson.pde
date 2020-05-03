import gsynlib.utils.*;
import gsynlib.geom.*;

long start = 0;
long end = 0;
long procTime = 0;

float minDistance = 6;

PoissonSampler poisson;

ArrayList<PVector> points;
ArrayList<Integer> highlights = new ArrayList<Integer>();
long ms = 0;

void setup() {
  size(800, 800);
  GApp.set(this);
  poisson = new PoissonSampler();
  init();
}
void init() {

  start = System.nanoTime();

  poisson.maxSearchIterations = 10;
  poisson.init(minDistance, 50, 150, width-100, height-300);

  end = System.nanoTime();

  procTime = end-start;

  points = poisson.getPoints();
  highlights.clear();

  for (int i = 0; i < points.size(); i++) {
    PVector a = points.get(i);
    for (int j = 0; j < points.size(); j++) {
      PVector b = points.get(j);
      if (a == b)
        continue;

      float d = PVector.dist(a, b);
      if (d < minDistance) {
        highlights.add(i);
        highlights.add(j);
      }
    }
  }

  ms = procTime/1000000;
  System.gc();
}

void keyPressed() {
  init();
}

void draw() {
  background(255);

  for (int i = 0; i < points.size(); i++) {
    PVector p = points.get(i);

    fill(255, 0, 0, 0);
    noStroke();
    ellipse(p.x, p.y, minDistance*2, minDistance*2);

    if (highlights.contains(i)) {
      strokeWeight(5);
      stroke(255, 0, 0);
    } else { 
      strokeWeight(4);
      stroke(64);
    }

    point(p.x, p.y);
  }

  fill(0);
  text("numPoints:" + points.size() + " procTime:" + ms + "ms", 10, height - 5);
}

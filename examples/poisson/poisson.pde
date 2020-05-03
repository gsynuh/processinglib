import gsynlib.utils.*;
import gsynlib.geom.*;

long start = 0;
long end = 0;
long procTime = 0;

float minDistance = 12;

PoissonSampler poisson;

void setup() {
  size(800, 800);
  GApp.set(this);
  poisson = new PoissonSampler();
  init();
}

ArrayList<PVector> points;
ArrayList<Integer> highlights = new ArrayList<Integer>();
void init() {
  start = System.nanoTime();

  poisson.maxSearchIterations = 10;
  poisson.init(minDistance, 50, 50, width-100);

  end = System.nanoTime();

  procTime = end-start;

  points = poisson.getAllPoints();
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

  println("process ms:", procTime/1000000);
}

void keyPressed() {
  init();
}

void draw() {
  background(255);

  for (int i = 0; i < points.size(); i++) {
    PVector p = points.get(i);

    if (highlights.contains(i)) {
      strokeWeight(5);
      stroke(255, 0, 0);
    } else { 
      strokeWeight(4);
      stroke(120);
    }
    
    point(p.x, p.y);
  }
}

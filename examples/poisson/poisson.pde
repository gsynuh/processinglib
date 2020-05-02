import gsynlib.utils.*;
import gsynlib.geom.*;

long start = 0;
long end = 0;
long procTime = 0;

PoissonSampler poisson;

void setup() {
  size(800, 800);
  GApp.set(this);
  poisson = new PoissonSampler();
  init();
}

void init() {
  start = System.nanoTime();

  poisson.maxSearchIterations = 20;
  poisson.init(14, 50, 50, width-100, height-100);

  end = System.nanoTime();

  procTime = end-start;

  println("process ms:", procTime/1000000);
}

void keyPressed() {
  init();
}

void draw() {
  background(255);

  stroke(120);
  strokeWeight(4);

  for (PVector p : poisson.getAllPoints()) {
    point(p.x, p.y);
  }
}

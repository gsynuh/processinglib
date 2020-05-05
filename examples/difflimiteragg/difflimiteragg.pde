//Diffusion Limited Aggregation
//https://www.youtube.com/watch?v=Cl_Gjj80gPE

import gsynlib.utils.*;
import gsynlib.geom.*;

QuadTree quadTree;

float radius = 2;
int iterations = 100;

VectorPool vecPool;

Bounds windowBounds = new Bounds();
Bounds treeBounds = new Bounds();

ArrayList<Particle> liveParticles = new ArrayList<Particle>();

ArrayList<Particle> roots = new ArrayList<Particle>();

void setup() {
  size(800, 800, FX2D);
  GApp.set(this);

  windowBounds.set(100, 100, width-200, height-200);

  vecPool = new VectorPool(64);
  quadTree = new QuadTree<Particle>(0, 0, width, height);

  init();
}

void keyPressed() {
  init();
}

void init() {
  liveParticles.clear();
  quadTree.clear();
  roots.clear();

  diag = 0;

  Particle root = new Particle();
  root.live = false;
  root.position.set(width/2, height/2);

  roots.add(root);

  treeBounds.position.set(root.position);
  treeBounds.size.set(0, 0);
  treeBounds.update();

  quadTree.insert(root);

  diag = sqrt(treeBounds.size.x * treeBounds.size.x + treeBounds.size.y * treeBounds.size.y);

  for (int i = 0; i < 1000; i++) {
    createParticle();
  }
}

void limitBounds(PVector pos) {

  PVector c = treeBounds.center;
  float sqrdist = GApp.sqrDist(c, pos);

  float d = max(100, diag*0.5 + 100);

  if (sqrdist > (d * d)) {

    PVector p = vecPool.get();

    float a = random(-TWO_PI, TWO_PI);
    p.set(cos(a)*d, sin(a)*d);
    p.add(c);

    pos.set(p);

    vecPool.dispose(p);
  }
}

float diag = 0;


void createParticle() {

  float x = treeBounds.center.x;
  float y = treeBounds.center.y;

  float r = max(100, diag*0.5 + 100);
  float a = random(-TWO_PI, TWO_PI);
  x += cos(a) * r;
  y += sin(a) * r;

  Particle p = new Particle();
  p.position.set(x, y);
  liveParticles.add(p);
}

void attachParticle(Particle p, Particle to) {
  p.live = false;
  liveParticles.remove(p);
  quadTree.insert(p);
  to.childParticles.add(p);

  treeBounds.Encapsulate(p.position);
  diag = sqrt(treeBounds.size.x * treeBounds.size.x + treeBounds.size.y * treeBounds.size.y);

  createParticle();
}

void stopLive() {
  println("Stopping live particles");
  liveParticles.clear();
}

void draw() {

  background(255);

  if (liveParticles.size()>0) {
    strokeWeight(1);
    noFill();
    stroke(0);
    rect(treeBounds.position.x, 
      treeBounds.position.y, 
      treeBounds.size.x, 
      treeBounds.size.y);
    stroke(0, 0, 255);
    rect(windowBounds.position.x, 
      windowBounds.position.y, 
      windowBounds.size.x, 
      windowBounds.size.y);
  }

  stroke(255, 0, 0);

  for (int i = 0; i < iterations; i++) {

    for (int j = 0; j < liveParticles.size(); j++) {
      Particle p = liveParticles.get(j);
      p.update();
    }
  }

  strokeWeight(2);
  for (Particle p : liveParticles) {
    point(p.position.x, p.position.y);
  }

  noFill();
  stroke(64);
  strokeWeight(2);

  for (Particle root : roots)
    drawParticle(root, root);

  if (liveParticles.size() > 0 && !windowBounds.Contains(treeBounds)) {
    stopLive();
  }

  fill(255);
  text("iterations per frame " + iterations, 20, height - 20);
}

void drawParticle(Particle p, Particle parent) {

  line(parent.position.x, parent.position.y, p.position.x, p.position.y);

  for (Particle c : p.childParticles)
    drawParticle(c, p);
}

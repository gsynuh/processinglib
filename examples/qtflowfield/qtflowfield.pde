import gsynlib.geom.*;
import gsynlib.utils.*;
QuadTree quadTree;

Bounds b;

ArrayList<Particle> particles = new ArrayList<Particle>();

void setup() {
  fullScreen(2);
  GApp.set(this);
  QuadTreeNode.maxNodeDataNum = 2;

  b = new Bounds(100, 100, 600, 600);
  quadTree = new QuadTree(b);

  float noiseScale = 0.01;
  float margin = 0;

  for (int i = 0; i < 600; i++) {
    PVector pos = new PVector(random(margin,width-margin*2), random(margin,height-margin*2));
    PVector vec = new PVector();

    float n = noise(pos.x*noiseScale, pos.y*noiseScale) * 2 - 1;
    vec.x = cos(n * TWO_PI);
    vec.y = sin(n * TWO_PI);

    vec.setMag(random(0.5, 2));

    QuadTreeDataVector v = new QuadTreeDataVector(pos, vec);
    quadTree.insert(v);
  }

  init();
  
  background(255);
}

void init() {
  particles.clear();
  for (int i = 0; i < 1000; i++) {
    Particle p = new Particle();
    p.position.x = random(width);
    p.position.y = random(height);
    particles.add(p);
  }

}

void keyPressed() {
  init();
  background(255);
}

void mouseDragged() {
  Particle p = new Particle();
  p.position.x = mouseX;
  p.position.y = mouseY;
  particles.add(p);
}

void draw() {

  noStroke();
  fill(255, 12);
  rect(0, 0, width, height);


  for (Particle p : particles) {
    p.update();
  }

  for (Particle p : particles) {
    p.render();
  }

  quadTree.resetVisited();
  quadTree.render();
  
  pushMatrix();
  pushStyle();
  fill(255);
  translate(0,height-30);
  rect(0,0,200,30);
  fill(0);
  text("particle count " + particles.size(),5,20);
  popStyle();
  popMatrix();
}

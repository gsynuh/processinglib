import gsynlib.geom.*;
import gsynlib.utils.*;
QuadTree quadTree;

Bounds b;
int seed = 0;
float rad = 150;
ArrayList<Particle> particles = new ArrayList<Particle>();

void setup() {
  fullScreen(FX2D, 2);
  smooth(0);

  GApp.set(this);
  QuadTreeNode.maxNodeDataNum = 2;

  b = new Bounds(10, 10, 200, 200);

  init();
}

void init() {

  quadTree = new QuadTree(b);
  quadTree.debugDrawNodes = false;
  quadTree.debugDrawVisited = false;
  quadTree.debugDrawVectors = false;
  quadTree.debugDrawData = false;
  quadTree.debugVectorScale = 10;

  seed = round(random(0, 99999));

  noiseSeed(seed);

  float noiseScale = 0.005;
  float margin = 0;

  PVector c = new PVector(width/2, height/2);

  //NOISE FORCES
  for (int i = 0; i < 1000; i++) {
    PVector pos = new PVector(random(margin, width-margin*2), random(margin, height-margin*2));
    PVector vec = new PVector();

    float d = PVector.dist(pos, c);
    if (d < rad+50)
      continue;

    float n = noise(pos.x*noiseScale, pos.y*noiseScale);
    float a = map(n, 0, 1, -TWO_PI, TWO_PI);
    vec.x = cos(a);
    vec.y = sin(a);

    vec.setMag(0.5);

    QuadTreeDataVector v = new QuadTreeDataVector(pos, vec);
    quadTree.insert(v);
  }

  //CIRCLE
  float ang = 60;
  for (float i = 0; i < ang; i++) {
    PVector pos = new PVector(width/2, height/2);
    PVector vec = new PVector();
    float a = (i/ang) * TWO_PI;

    pos.x += cos(a) * rad;
    pos.y += sin(a) * rad;

    vec.x = cos(a);
    vec.y = sin(a);

    vec.setMag(5);

    QuadTreeDataVector v = new QuadTreeDataVector(pos, vec);
    quadTree.insert(v);

    float r = rad+50;
    pos.x = width/2 + cos(a) * r;
    pos.y = height/2 + sin(a) * r;
    vec.setMag(-1);
    v = new QuadTreeDataVector(pos, vec);
    quadTree.insert(v);
  }

  particles.clear();
  for (int i = 0; i < 0; i++) {
    Particle p = new Particle();
    p.position.x = random(width);
    p.position.y = random(height);
    particles.add(p);
  }

  background(0);
  System.gc();
}

void keyPressed() {
  if (keyCode == ENTER) {
    init();
  }
}

void mouseDragged() {
  addParticlesAt(mouseX, mouseY, 5);
}

void addParticlesAt(float x, float y, int count) {
  PVector a = new PVector();
  for (int i = 0; i < count; i++) {
    Particle p = new Particle();
    p.position.x = x;
    p.position.y = y;
    particles.add(p);
    p.update();
    a.set(p.accel);
    float h = a.heading() + random(-PI/4, PI/4);
    float d = a.mag();

    if (d <= 0) {
      h = random(-TWO_PI, TWO_PI);
      d= 1;
    }
    a.x = cos(h)*d;
    a.y = sin(h)*d;
    p.accel.set(a);
  }
}

void doSpawn() {
  if (particles.size() < 1000) {
    float d = 120f;
    for (float i = 0; i < d; i++) {
      float posX = width/2;
      float posY = height/2;
      float a = i/d * TWO_PI + (frameCount/PI);
      posX += cos(a) * (rad*2.8);
      posY += sin(a) * (rad*2.8);

      addParticlesAt(posX, posY, 1);
    }
  }
}

void draw() {

  colorMode(RGB);
  noStroke();
  fill(0, 20);
  if (frameCount % 4 == 0)
    rect(0, 0, width, height);

  doSpawn();

  pushMatrix();
  stroke(255);
  strokeWeight(4);
  translate(width/2, height/2);
  ellipse(0, 0, rad*2, rad*2);
  popMatrix();

  quadTree.render();

  for (Particle p : particles) {
    p.update();
  }

  for (Particle p : particles) {
    p.render();
  }

  drawText();
}

void drawText() {
  pushMatrix();
  pushStyle();
  fill(255);
  translate(0, height-30);
  rect(0, 0, 250, 30);
  fill(0);
  text("seed:" + seed + "  particles:" + particles.size() + "  fps:" + round(frameRate), 5, 20);
  popStyle();
  popMatrix();
}

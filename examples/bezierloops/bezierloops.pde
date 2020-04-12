import gsynlib.bezier.*;

BezierLoop loop;

float size = 400;
float margin = 20;
float numCurves = 2;
int seed = 0;


void setup() {
  size(600,600);
  loop = new BezierLoop(this);
  init();
}

void init() {
  seed = (millis() + (int)random(1000)) % 1000;
  randomSeed(seed);
  numCurves = round(map(mouseX, 0, width, 0, 12))+1; 
  loop.init(numCurves, size, size,margin);
  
  loop.bakePrecision = 20;
  loop.bake();
}

void mousePressed() {
  init();
}


void draw() {
  background(255);
  textSize(10);

  PVector c = loop.bounds.getCenter();
  translate(width/2 - c.x, height/2 - c.y);

  stroke(64);
  strokeWeight(10);
  fill(255);
  rect(loop.bounds.position.x, 
    loop.bounds.position.y, 
    loop.bounds.size.x, 
    loop.bounds.size.y);

  loop.renderDebug();
  loop.render();
  loop.renderBake();

  PVector br = loop.bounds.getBottomRight();
  translate(loop.bounds.position.x,br.y + 5);

  fill(255, 120);
  noStroke();
  rect(0, 0, loop.bounds.size.x, 60);
  fill(0);
  textSize(18);
  text("numCurves : " + (int)loop.getCurves().size(), 0, 25);
  text("seed : " + seed, 0, 45);
}

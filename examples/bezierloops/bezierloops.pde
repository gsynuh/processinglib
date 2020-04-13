import gsynlib.utils.*;
import gsynlib.geom.*;
import gsynlib.bezier.*;

BezierLoop bloop;

float size = 400;
float margin = 20;
float numCurves = 2;
int seed = 0;


void setup() {
  size(600,600);
  
  GApp.set(this);
  
  bloop = new BezierLoop();
  init();
}

void init() {
  seed = (millis() + (int)random(1000)) % 1000;
  randomSeed(seed);
  numCurves = round(map(mouseX, 0, width, 0, 12))+1; 
  
  bloop.setTargetBounds(new Bounds(0,0,size,size));
  bloop.init(numCurves,margin);
  
  bloop.bakePrecision = 20;
  bloop.bake();
}

void mousePressed() {
  init();
}


void draw() {
  background(255);
  textSize(10);

  PVector c = bloop.bounds.getCenter();
  translate(width/2 - c.x, height/2 - c.y);

  stroke(64);
  strokeWeight(10);
  fill(255);
  rect(bloop.bounds.position.x, 
    bloop.bounds.position.y, 
    bloop.bounds.size.x, 
    bloop.bounds.size.y);

  bloop.renderDebug();
  bloop.render();
  bloop.renderBake();

  PVector br = bloop.bounds.getBottomRight();
  translate(bloop.bounds.position.x,br.y + 5);

  fill(255, 120);
  noStroke();
  rect(0, 0, bloop.bounds.size.x, 60);
  fill(0);
  textSize(18);
  text("numCurves : " + (int)bloop.getCurves().size(), 0, 25);
  text("seed : " + seed, 0, 45);
}

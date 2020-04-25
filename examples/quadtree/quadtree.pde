import gsynlib.geom.*;
import gsynlib.utils.*;

QuadTree quadTree;

Bounds b;
float queryRadius = 50;

QuadTreeData mouseData;
PVector mousePoint = new PVector();
ArrayList<QuadTreeData> queryResults = new ArrayList<QuadTreeData>();


ArrayList<Walker> walkers = new ArrayList<Walker>();
class Walker {
  QuadTreeData data;
  PVector position = new PVector();
  Boolean inCollision = false;

  public void update() {
    position.x += random(-1, 1);
    position.y += random(-1, 1);
    inCollision = false;

    //This is not an appropriate way to test for collision
    //rather, circle query check would be faster since all Walkers have the same size.
    QuadTreeData nearestQTD = quadTree.getNearestData(position, data);
    if (nearestQTD != null) {
      float d = GApp.sqrDist(position, nearestQTD.position);
      if (d <= 100) {
        inCollision= true;
      }
    }
  }

  public void show() {
    pushMatrix();
    pushStyle();
    noStroke();
    if (inCollision) {
      fill(255, 0, 0);
    } else {
      fill(0, 255, 0);
    }
    ellipse(position.x, position.y, 10, 10);
    popStyle();
    popMatrix();
  }
}

void setup() {
  size(800, 800);

  GApp.set(this);

  QuadTreeNode.maxNodeDataNum = 2;

  b = new Bounds(325.3, 315.3, 125, 125);
  quadTree = new QuadTree(b);


  for (int i = 0; i < 500; i++) {
    PVector pos = new PVector(
      random(width), 
      random(height)
      );
    Walker w = new Walker();
    QuadTreeData d = new QuadTreeData(pos, w);

    if (quadTree.insert(d)) {
      w.data = d;
      w.position.set(d.position);
      walkers.add(w);
    }
  }
}

void mousePressed() {
  PVector pos = new PVector(mouseX, mouseY);
  Walker w = new Walker();
  QuadTreeData d = new QuadTreeData(pos, w);

  if (quadTree.insert(d)) {
    w.data = d;
    w.position.set(d.position);
    walkers.add(w);
  }
}

float time = 0f;

void draw() {
  background(255);
  noFill();

  time += 0.04f;

  mousePoint.set(mouseX, mouseY);

  quadTree.resetVisited();

  QuadTreeNode rootNode = quadTree.getRoot();

  QuadTreeData closestData = quadTree.getNearestData(mousePoint);

  strokeWeight(12);

  for (QuadTreeData d : queryResults) {
    point(d.position.x, d.position.y);
  }

  drawQuadTreeNode(rootNode);

  if (closestData != null) {
    noStroke();
    fill(255, 0, 0);
    ellipse(closestData.position.x, closestData.position.y, 10, 10);
  }

  noFill();
  stroke(255, 0, 255);
  strokeWeight(3);
  rect(b.position.x, b.position.y, b.size.x, b.size.y);


  for (Walker w : walkers) {
    w.update();
    quadTree.updatePosition(w.data, w.position);
  }

  for (Walker w : walkers) {
    w.show();
  }
}

// DRAW

void drawBounds(Bounds b, int i) {

  if (i >0) {
    switch(i) {
    case 1 : 
      fill(255, 0, 0, 30); 
      break;
    case 2 : 
      fill(0, 0, 255, 30); 
      break;
    }
  } else
    noFill();

  rect(b.position.x, 
    b.position.y, 
    b.size.x, 
    b.size.y);
}

void drawQuadTreeNode(QuadTreeNode q) {
  noFill();
  stroke(100);
  strokeWeight(1);

  drawBounds(q.bounds, q.id);

  if (!q.isLeaf()) {
    drawQuadTreeNode(q.A);
    drawQuadTreeNode(q.B);
    drawQuadTreeNode(q.C);
    drawQuadTreeNode(q.D);
  } else {

    stroke(32);
    strokeWeight(2);
    for (QuadTreeData d : q.data) {
      point(d.position.x, d.position.y);
    }
  }
}

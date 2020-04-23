import gsynlib.geom.*;
import gsynlib.utils.*;

QuadTree quadTree;

Bounds b;
float queryRadius = 50;

QuadTreeData mouseData;
PVector mousePoint = new PVector();
QuadTreeNode mouseNode = null;
QuadTreeData closestData = null;
ArrayList<QuadTreeData> queryResults = new ArrayList<QuadTreeData>();

void setup() {
  size(800, 800);

  QuadTreeNode.maxNodeDataNum = 2;

  b = new Bounds(325.3, 315.3, 125, 125);
  quadTree = new QuadTree(b);

  mouseData = new QuadTreeData(new PVector(100, 100), "mouseData");
  quadTree.insert(mouseData);

  /*
  for (int i = 0; i < 10; i++) {
   PVector pos = new PVector(
   random(width), 
   random(height)
   );
   String str = "data" + floor(random(1) * 1000);
   QuadTreeData d = new QuadTreeData(pos, str);
   quadTree.insert(d);
   }
   */
}

void mousePressed() {
  PVector pos = new PVector(mouseX, mouseY);

  QuadTreeData d = new QuadTreeData(pos, "mousePressed" + pos.toString());
  quadTree.insert(d);
}

float time = 0f;

void draw() {
  background(255);
  noFill();

  time += 0.04f;

  mousePoint.set(mouseX, mouseY);

  quadTree.resetVisited();

  float rad = (cos(time) * 0.5f + 0.5f) * queryRadius + queryRadius;
  quadTree.queryCircle(mousePoint, rad, queryResults);

  //constantly remove and re-insert mouseData , changing its position
  quadTree.updatePosition(mouseData, mousePoint);

  stroke(0, 255, 255);
  fill(0, 255, 255, 100);
  ellipse(
    mousePoint.x, 
    mousePoint.y, 
    rad * 2f, 
    rad * 2f
    );
  strokeWeight(12);

  for (QuadTreeData d : queryResults) {
    point(d.position.x, d.position.y);
  }

  QuadTreeNode r = quadTree.getRoot();

  mouseNode = quadTree.getNodeUnder(mousePoint);

  closestData = QuadTreeNode.getClosestDataInCandidates(mousePoint, queryResults);

  drawQuadTreeNode(r);

  if (closestData != null) {
    noStroke();
    fill(255, 0, 0);
    ellipse(closestData.position.x, closestData.position.y, 10, 10);
  }

  noFill();
  stroke(255, 0, 255);
  strokeWeight(3);
  rect(b.position.x, b.position.y, b.size.x, b.size.y);
}

// DRAW

void drawBounds(Bounds b, Boolean doFill) {

  if (doFill)
    fill(0, 10);
  else
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

  if (mouseNode == q) {
    strokeWeight(3);
  }

  drawBounds(q.bounds, q.visited);

  if (q.isSplit) {
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

import gsynlib.geom.*;

QuadTree quadTree;
Bounds b;

PVector mousePos = new PVector();

void setup() {
  size(800, 800);

  QuadTreeNode.maxNodeDataNum = 3;

  b = new Bounds(400, 400, 200, 200);
  quadTree = new QuadTree(b);


  for (int i = 0; i < 10; i++) {
    PVector pos = new PVector(
      random(width), 
      random(height)
      );
    String str = "data" + floor(random(1) * 1000);
    quadTree.insert(pos, str);
  }
}

void mousePressed() {
  mousePos.set(mouseX, mouseY);
  String str = "data" + floor(random(1) * 1000);
  quadTree.insert(mousePos.copy(), str);
}

void drawBounds(Bounds b, Boolean doFill) {

  if (doFill)
    fill(0,20);
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
    strokeWeight(4);
    for (QuadTreeData d : q.data) {
      point(d.position.x, d.position.y);
    }
  }
}

QuadTreeNode mouseNode = null;
QuadTreeData closestData = null;

void draw() {
  background(255);
  noFill();
  
  quadTree.resetVisited();

  mousePos.set(mouseX, mouseY);

  QuadTreeNode r = quadTree.getRoot();

  mouseNode = quadTree.getNodeUnder(mousePos);
  closestData = quadTree.getNearestData(mousePos);

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

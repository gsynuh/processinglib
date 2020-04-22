import gsynlib.geom.*;

QuadTree quadTree;

Bounds b;
Bounds queryBounds;
QuadTreeData mouseData;
PVector mousePoint = new PVector();
QuadTreeNode mouseNode = null;
QuadTreeData closestData = null;
ArrayList<QuadTreeData> queryResults = new ArrayList<QuadTreeData>();

void setup() {
  size(800, 800);

  QuadTreeNode.maxNodeDataNum = 2;

  queryBounds = new Bounds(0, 0, 200, 100);

  b = new Bounds(400, 400, 125, 125);
  quadTree = new QuadTree(b);

  mouseData = new QuadTreeData(new PVector(100, 100), "mouseData");
  quadTree.insert(mouseData);

  for (int i = 0; i < 1000; i++) {
    PVector pos = new PVector(
      random(width), 
      random(height)
      );
    String str = "data" + floor(random(1) * 1000);
    QuadTreeData d = new QuadTreeData(pos, str);
    quadTree.insert(d);
  }
}


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

void draw() {
  background(255);
  noFill();

  mousePoint.set(mouseX, mouseY);

  quadTree.resetVisited();

  queryBounds.position.x = mousePoint.x - queryBounds.size.x * 0.5;
  queryBounds.position.y = mousePoint.y - queryBounds.size.y * 0.5;
  quadTree.queryBounds(queryBounds, queryResults);

  //constantly remove and re-insert mouseData , changing its position
  quadTree.updatePosition(mouseData, mousePoint);

  stroke(0, 255, 255);
  fill(0, 255, 255, 100);
  rect(
    queryBounds.position.x, 
    queryBounds.position.y, 
    queryBounds.size.x, 
    queryBounds.size.y
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

import gsynlib.geom.*;

QuadTree quadTree;
Bounds b;

void setup() {
  size(800,800);
  
  QuadTreeNode.maxNodeDataNum = 4;
  
b = new Bounds(400,400,200,200);
quadTree = new QuadTree(b);


for(int i = 0; i < 1000; i++) {
  PVector pos = new PVector(
  random(width),
  random(height)
  );
  String str = "data" + floor(random(1) * 1000);
  quadTree.insert(pos,str);
}


}

void drawBounds(Bounds b) {
rect(b.position.x,
    b.position.y,
    b.size.x,
    b.size.y);
}

void drawQuadTreeNode(QuadTreeNode q) {
  
    noFill();
    stroke(100);
    strokeWeight(1);
    
    if(mouseNode == q) {
      strokeWeight(3);
    }
    
    drawBounds(q.bounds);
  
  if(q.isSplit) {
    drawQuadTreeNode(q.A);
    drawQuadTreeNode(q.B);
    drawQuadTreeNode(q.C);
    drawQuadTreeNode(q.D);
  }else {
    
    stroke(32);
    drawBounds(q.bounds);
    strokeWeight(4);
    
    for(QuadTreeData d : q.data) {
      point(d.position.x,d.position.y);
    }
    
  }
}
QuadTreeNode mouseNode = null;
PVector mousePos = new PVector();

void draw() {
  background(255);
  noFill();
  
  mousePos.set(mouseX,mouseY);
  
  if(frameCount % 60 == 0) {
    String str = "data" + floor(random(1) * 1000);
    quadTree.insert(mousePos.copy(),str);
  }
  
  QuadTreeNode r = quadTree.getRoot();
  
  mouseNode = quadTree.search(mousePos);
  
  float minDist = Float.MAX_VALUE;
  QuadTreeData closest = null;
  for(QuadTreeData d : mouseNode.data) {
    float dist = PVector.dist(d.position,mousePos);
    if(dist < minDist) {
      minDist = dist;
      closest = d;
    }
  }
  
   drawQuadTreeNode(r);
  
  if(closest != null) {
    noStroke();
    fill(255,0,0);
    ellipse(closest.position.x,closest.position.y,10,10);
  }
  
  noFill();
  stroke(255,0,255);
  strokeWeight(3);
  rect(b.position.x,b.position.y,b.size.x,b.size.y);
  
}

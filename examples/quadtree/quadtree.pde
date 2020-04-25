import gsynlib.geom.*;
import gsynlib.utils.*;

QuadTree quadTree;

Bounds b;
float queryRadius = 50;

QuadTreeData mouseData;

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

  QuadTreeNode.maxNodeDataNum = 3;

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

PVector pos = new PVector(0,0);
void draw() {
  background(255);

  for (Walker w : walkers) {
    w.update();
    quadTree.updatePosition(w.data, w.position);
  }
  
  pos.set(mouseX,mouseY);
  
  QuadTreeData closestData = quadTree.getNearestData(pos);

  quadTree.render();

  if (closestData != null) {
    fill(0, 0, 255);
    ellipse(closestData.position.x, closestData.position.y, 20, 20);
  }

  for (Walker w : walkers) {
    w.show();
  }
}

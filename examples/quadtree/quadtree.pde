import gsynlib.geom.*;
import gsynlib.utils.*;

QuadTree quadTree;

Bounds b;
float queryRadius = 50;

QuadTreeData mouseData;

ArrayList<Walker> walkers = new ArrayList<Walker>();

void setup() {
  size(800, 800);

  GApp.set(this);

  QuadTreeNode.maxNodeDataNum = 3;

  b = new Bounds(325.3, 315.3, 125, 125);
  quadTree = new QuadTree(b);


  for (int i = 0; i < 1; i++) {
    PVector pos = new PVector(
      random(width), 
      random(height)
      );
    Walker w = new Walker();
    QuadTreeDataObject d = new QuadTreeDataObject(pos, w);

    if (quadTree.insert(d)) {
      w.data = d;
      w.position.set(d.position);
      walkers.add(w);
    }
  }
}

void mouseDragged() {
  PVector pos = new PVector(mouseX + random(-20, 20), mouseY + random(-20, 20));
  Walker w = new Walker();
  QuadTreeDataObject d = new QuadTreeDataObject(pos, w);

  if (quadTree.insert(d)) {
    w.data = d;
    w.position.set(d.position);
    walkers.add(w);
  }
}

PVector pos = new PVector(0, 0);
void draw() {
  background(255);

  for (Walker w : walkers) {
    w.update();
    quadTree.updatePosition(w.data, w.position);
  }

  pos.set(mouseX, mouseY);

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

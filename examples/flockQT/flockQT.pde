import gsynlib.geom.*;
import gsynlib.utils.*;

QuadTree quadTree;

Bounds b;
float queryRadius = 50;

QuadTreeData mouseData;

ArrayList<Boid> boids = new ArrayList<Boid>();

void setup() {
  size(800,800);

  GApp.set(this);

  QuadTreeNode.maxNodeDataNum = 3;

  b = new Bounds(325.3, 315.3, 125, 125);
  quadTree = new QuadTree(b);


  for (int i = 0; i < 500; i++) {
    PVector pos = new PVector(
      random(width), 
      random(height)
      );
    Boid b = new Boid();
    QuadTreeDataObject d = new QuadTreeDataObject(pos, b);

    if (quadTree.insert(d)) {
      b.data = d;
      b.position.set(d.position);
      boids.add(b);
    }
  }
}

void mouseDragged() {
  PVector pos = new PVector(mouseX, mouseY);
  Boid b = new Boid();
  QuadTreeDataObject d = new QuadTreeDataObject(pos, b);

  if (quadTree.insert(d)) {
    b.data = d;
    b.position.set(d.position);
    boids.add(b);
  }
}

void draw() {
  background(245);

  for (Boid b : boids) {
    b.update();
    quadTree.updatePosition(b.data, b.position);
  }

  for (Boid b : boids) {
    b.show();
  }

  quadTree.render();
}

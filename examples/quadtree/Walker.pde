public class Walker extends QuadTreeData {
  Boolean inCollision = false;

  public void update() {
    position.x += random(-1, 1);
    position.y += random(-1, 1);
    inCollision = false;

    //This is not an appropriate way to test for collision
    //rather, circle query check would be faster since all Walkers have the same size.
    QuadTreeData nearestQTD = quadTree.getNearestData(position, this);
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

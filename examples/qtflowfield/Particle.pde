ArrayList<QuadTreeData> queryResult = new ArrayList<QuadTreeData>();
ArrayList<QuadTreeDataVector> forces = new ArrayList<QuadTreeDataVector>();

public class Particle {

  PVector position = new PVector();
  PVector vel = new PVector();
  PVector accel = new PVector();
  
  public Particle() {
    accel.x = random(-1000,1000);
    accel.y = random(-1000,1000);
  }

  float size = 2;


  void addForce(float x, float y) {
    this.accel.x += x;
    this.accel.y += y;
  }

  void processForces() {
    float c = 0;
    float x = 0;
    float y = 0;

    for (QuadTreeDataVector v : forces) {
      x+=v.vector.x;
      y+=v.vector.y;
      c++;
    }

    if (c>0) {
      x /= c;
      y /= c;
      addForce(x, y);
    }
  }

  public void update() {
    queryResult.clear();
    forces.clear();
    quadTree.queryCircle(this.position, 50, queryResult);
    for (QuadTreeData d : queryResult) {
      if (d instanceof QuadTreeDataVector) {
        forces.add((QuadTreeDataVector)d);
      }
    }

    processForces();
    accel.mult(0.951452);
    
    accel.limit(40);
    vel.add(accel);
    vel.limit(4);
    position.add(vel);
    
    doBounds();
  }
  
  void doBounds() {
      float r =0;

    if (position.x < -r)
      position.x = width -r;
    if (position.y < -r)
      position.y = height -r;
    if (position.x > width + r)
      position.x = -r;
    if (position.y > height + r)
      position.y = -r;
  }

  public void render() {
    pushMatrix();
    pushStyle();
    noStroke();
    fill(0);
    translate(position.x, position.y);
    ellipse(-size/2, -size/2, size, size);
    popStyle();
    popMatrix();
  }
}

ArrayList<QuadTreeData> boidNeighbors = new ArrayList<QuadTreeData>();
float maxSpeed = 2;
float maxForce = 0.02;
float desiredSep = 11;
PVector sep = new PVector();
PVector ali = new PVector();
PVector coh = new PVector();
PVector positionDelta = new PVector();
float neighborDist = 50;

public class Boid {

  public QuadTreeData data;
  public PVector position = new PVector();
  public PVector velocity = new PVector();
  public PVector acceleration = new PVector();

  public Boid() {
    velocity.x = cos(random(-TWO_PI,TWO_PI));
    velocity.y = sin(random(-TWO_PI,TWO_PI));
    velocity.setMag(random(0.2,1) * maxSpeed);
  }

  public void applyForce(PVector force) {
    acceleration.x += force.x;
    acceleration.y += force.y;
  }

  void align(PVector force, ArrayList<QuadTreeData> ns) {
    force.set(0, 0);
    float count = 0;
    for (QuadTreeData d : ns) {
      Boid b = (Boid)d.object;
      if (b != null) {
        force.x += b.velocity.x;
        force.y += b.velocity.y;
        count++;
      }
    }

    if (count > 0) {
      force.div(count);
      force.normalize();
      force.mult(maxSpeed);
      force.x = force.x - velocity.x;
      force.y = force.y - velocity.y;
      force.limit(maxForce);
    }
  }

  void seperate(PVector force, ArrayList<QuadTreeData> ns) {
    force.set(0, 0);
    float count = 0;

    for (QuadTreeData d : ns) {
      Boid b = (Boid)d.object;
      if (b != null) {
        positionDelta.set(position);
        positionDelta.sub(b.position);
        float dist = positionDelta.mag();

        if (dist > 0 && (dist < desiredSep)) {
          positionDelta.normalize();
          positionDelta.div(dist);
          force.add(positionDelta);
          count++;
        }
      }
    }

    if (count > 0) {
      force.div(count);
    }

    if (force.mag() > 0) {
      force.normalize();
      force.mult(maxSpeed);
      force.sub(velocity);
      force.limit(maxForce);
    }
  }
  
  PVector seekP = new PVector();
  void seek(PVector force) {
    seekP.set(force);
    seekP.sub(position);
    seekP.normalize();
    seekP.mult(maxSpeed);
    force.sub(velocity);
    force.limit(maxForce);
  }

  void cohesion(PVector force, ArrayList<QuadTreeData> ns) {

    float count = 0;
    force.set(0, 0);

    for (QuadTreeData d : ns) {
      Boid b = (Boid)d.object;
      positionDelta.set(position);
      positionDelta.sub(b.position);
      float dist = positionDelta.mag();

      if (dist > 0) {
        force.add(b.position);
        count++;
      }
    }


    if (count > 0) {
      force.div(count);
      seek(force);
    }
  }

  public void update() {
    boidNeighbors.clear();
    quadTree.queryCircle(position, neighborDist, boidNeighbors);

    //FLOCK LOGIC

    seperate(sep, boidNeighbors);
    align(ali, boidNeighbors);
    cohesion(coh, boidNeighbors);

    sep.mult(1.5f);
    ali.mult(1.0f);
    coh.mult(1.0f);

    this.applyForce(sep);
    this.applyForce(ali);
    this.applyForce(coh);

    //APPLY ACCEL
    velocity.add(acceleration);
    velocity.limit(maxSpeed);
    position.add(velocity);
    acceleration.mult(0);

    float r = 10;

    if (position.x < -r)
      position.x = width -r;
    if (position.y < -r)
      position.y = height -r;
    if (position.x > width + r)
      position.x = -r;
    if (position.y > height + r)
      position.y = -r;
  }

  public void show() {
    pushMatrix();
    pushStyle();
    noStroke();
    fill(0, 200);
    ellipse(position.x, position.y, 8, 8);
    popStyle();
    popMatrix();
  }
}

ArrayList<QuadTreeData> queryResult = new ArrayList<QuadTreeData>();
ArrayList<QuadTreeDataVector> forces = new ArrayList<QuadTreeDataVector>();

public class Particle {

  PVector oldPosition = new PVector();
  PVector position = new PVector();
  PVector vel = new PVector();
  PVector accel = new PVector();
  
  float range = 0;
  float size = 1;
  
  public Particle() {
    range = 45;
  }


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
  
  PVector p = new PVector();
  float bounds = 50;
  void boundForces() {
    p.set(0,0);
    
    
    if(this.position.x < bounds) {
      p.x += 1;
    } 
    else if(this.position.x > width - bounds) {
      p.x -= 1;
    }
    
    if(this.position.y < bounds) {
      p.y += 1;
    }
    else if(this.position.y > height - bounds) {
      p.y -= 1;
    }
    
    float d = p.mag();
    
    if(d > 0) {
    p.setMag(10);
    addForce(p.x,p.y);
    }
  }

  public void update() {
    queryResult.clear();
    forces.clear();
    quadTree.queryCircle(this.position, range, queryResult);
    for (QuadTreeData d : queryResult) {
      if (d instanceof QuadTreeDataVector) {
        forces.add((QuadTreeDataVector)d);
      }
    }

    processForces();
    accel.mult(0.951452);

    oldPosition.set(position);

    accel.limit(40);
    vel.add(accel);
    vel.limit(8);
    position.add(vel);
    
    doBounds();
    boundForces();
  }

  void doBounds() {
    Boolean teleported = false;

    if (position.x < bounds) {
      position.x = width -bounds;
      teleported= true;
    }
    if (position.y < bounds) {
      position.y = height -bounds;
      teleported = true;
    }
    if (position.x > width - bounds) {
      position.x = bounds;
      teleported = true;
    }
    if (position.y > height - bounds) {
      position.y = bounds;
      teleported = true;
    }

    if (teleported)
      oldPosition.set(position);
  }

  public void render() {
    pushMatrix();
    pushStyle();
    
    colorMode(HSB);
    int h = (ceil(map(vel.heading(), -PI, PI, 0,255))+128 + frameCount) % 255;

    stroke(h,255,255);
    strokeWeight(size);
    line(oldPosition.x, oldPosition.y, position.x, position.y);

    popStyle();
    popMatrix();
  }
}

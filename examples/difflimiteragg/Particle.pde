ArrayList<Particle> queryResults = new ArrayList<Particle>();

public class Particle extends QuadTreeData {

  public ArrayList<Particle> childParticles = new ArrayList<Particle>();
  public Boolean live = true;

  public void update() {
    if(!live)
      return;
    
    PVector mov = vecPool.get();
    float a = random(0, TWO_PI);
    mov.set(cos(a), sin(a));
    mov.setMag(radius);
    this.position.add(mov);
    vecPool.dispose(mov);
    
    limitBounds(this.position);

    queryResults.clear();
    quadTree.queryCircle(this.position, radius*2, queryResults);
    if (queryResults.size() > 0) {
      for (int ri = 0; ri < queryResults.size(); ri++) {
        Particle np = queryResults.get(ri);
        if(np == this)
          continue;
          
        float sqrdist = GApp.sqrDist(np.position,this.position);
        if(sqrdist <= (radius * radius * 4)) {
          attachParticle(this,np);
          return;
        }
      }
    }
  }
}

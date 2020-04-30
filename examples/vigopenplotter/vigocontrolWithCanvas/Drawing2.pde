import gsynlib.particles.*;

public class Drawing2 {
  public Drawing2(PlotterCanvas c) {
    
    //c.drawParticleSystem = true;
    
    ParticleSystem ps = c.getParticleSystem();
    
    ps.cache.precisionAngle = 0.01;
    ps.wrapParticles = false;
    ps.maxVelocity = 100;
    
    Bounds canvasArea = c.getBounds();
    
    noiseSeed(round(random(99999)));
    
    float noiseScale = 0.05f;
    
    for(int i = 0; i < 600; i++) {
      PVector point = canvasArea.getRandom();
      float n = noise(point.x * noiseScale,point.y * noiseScale);
      float a = map(n,0,1,-TWO_PI,TWO_PI);
      PVector force = new PVector(cos(a),sin(a));
      force.setMag(20);
      c.force(point,force);
    }
    
    c.pushMatrix();
    for(int i = 0; i < 32; i++) {
      PVector point = canvasArea.getPositionFromNorm(0.5,0.5 + map(i,0,32,-0.2,0.2));
      Particle p = c.particle(point, 0.5f);
      p.range = 5;
    }
    c.popMatrix();
  }
}

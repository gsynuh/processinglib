import gsynlib.geom.*;
import gsynlib.utils.*;
import gsynlib.particles.*;

ParticleSystem ps;

void setup() {
  size(1000,1000);
  smooth(2);

  GApp.set(this);

  ps = new ParticleSystem(new Bounds(0, 0, width, height));
  ps.wrapParticles = false;
  ps.drag = 0.98f;
  ps.maxVelocity = 100;

  float numParticles = 128;

  for (int i = 0; i < numParticles; i++) {

    float y = map(i, 0, numParticles, height/4, height-height/4);

    PVector p = new PVector(width/2, y);
    Particle part = ps.createParticle(p, 0f);
    part.initialAccel.set(random(-1,1), 0);
    part.lifetime = random(2, 3);
    part.initialAccel.setMag(10);
  }

  createForces();
  simulate();
}

void createForces() {
  noiseSeed(round(random(99999)));

  ps.forceField.clear();

  float noiseScale = 0.01;
  float noisePhaseX = random(1);
  float noisePhaseY = random(1);

  for (int i = 0; i < 800; i++) {
    PVector pos = new PVector(random(width), random(height));

    PVector f = new PVector();
    float a = noise(pos.x*noiseScale + noisePhaseX, pos.y*noiseScale + noisePhaseY);
    a = map(a, 0, 1, -TWO_PI, TWO_PI);
    f.set(cos(a), sin(a));
    f.setMag(10);

    ps.addForce(pos, f);
  }
}

void simulate() {
  background(255);
  ps.Play();

  while (ps.getLiveParticleCount() > 0) {
    ps.update(0.01);
    stroke(0,120);
    strokeWeight(1);
    for (Particle p : ps.particles) {
      if(p.live)
        point(p.position.x, p.position.y);
    }
  }
}

void keyPressed() {

  if (keyCode == ENTER)
    createForces();

  simulate();
}

void draw() {
}

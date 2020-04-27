import gsynlib.geom.*;
import gsynlib.utils.*;
import gsynlib.particles.*;
import java.util.*;
import java.util.Map.*;

ParticleSystem ps;

void setup() {
  size(800, 800);
  smooth(2);

  GApp.set(this);

  ps = new ParticleSystem(new Bounds(0, 0, width, height));
  ps.wrapParticles = false;
  ps.drag = 0.5f;
  ps.maxVelocity = 150;

  ps.cache.enabled = true;

  ps.cache.minDist = 3;
  ps.cache.maxDist = 50;
  ps.cache.precisionAngle = 0.02;

  ps.forceField.debugDrawNodes = false;
  ps.forceField.debugVectorScale = 10f;

  float numParticles = 128;
  float h = 1.5;

  for (int i = 0; i < numParticles; i++) {

    float  a = map(i, 0, numParticles, 0, TWO_PI);
    PVector p = new PVector(width/2, height/2);

    p.x += cos(a) * 200;
    p.y += sin(a) * 200;

    Particle part = ps.createParticle(p, 0f);
    part.initialAccel.set( cos(a), sin(a));

    part.lifetime = 5;
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

  PVector center = new PVector(width/2, height/2);
  PVector h = new PVector();

  for (int i = 0; i < 400; i++) {
    PVector pos = new PVector(random(width), random(height));

    h.set(center);
    h.sub(pos);

    PVector f = new PVector();
    float a = noise(pos.x*noiseScale + noisePhaseX, pos.y*noiseScale + noisePhaseY);
    a = map(a, 0, 1, -PI/2, PI/2) + h.heading();
    f.set(cos(a), sin(a));
    f.setMag(random(0.5, 5));

    ps.addForce(pos, f);
  }
}

void simulate() {
  background(255);
  ps.Play();

  while (ps.getLiveParticleCount() > 0) {
    ps.update(0.01);
  }
}

Boolean drawPoints = false;

void keyPressed() {
  frameCount = 0;

  if (keyCode == ENTER)
    createForces();

  if (keyCode == UP) {
    ps.forceField.debugDrawVectors = !ps.forceField.debugDrawVectors;
    ps.forceField.debugDrawData = !ps.forceField.debugDrawData;
  }

  if (keyCode == 32)
    drawPoints = !drawPoints;

  simulate();
}


void draw() {
  background(255);

  ps.forceField.render();


  int cachePointCount = 0;

  noFill();
  stroke(drawPoints ? 0 : 62);
  strokeWeight(drawPoints ? 1.3 : 0.5);

  Map<Particle, CachedParticle> c = ps.cache.getCache();
  for (Entry<Particle, CachedParticle> kvp : c.entrySet()) {
    CachedParticle cp = kvp.getValue();
    beginShape();
    int ct = 1;
    for (PVector p : cp.points) {

      if (ct % frameCount == 0)
        continue;

      if (drawPoints)
        point(p.x, p.y);
      else
        vertex(p.x, p.y);

      cachePointCount++;
      ct++;
    }

    endShape();
  }

  fill(0);
  text("cache points: " + cachePointCount, 10, height-10);
}

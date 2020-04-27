package gsynlib.particles;

import java.util.*;
import gsynlib.geom.*;
import processing.core.*;

import static processing.core.PApplet.*;

public class ParticleSystem {

	public ArrayList<Particle> particles = new ArrayList<Particle>();

	public Boolean wrapParticles = false;
	public Bounds bounds = new Bounds();
	public QuadTree<QuadTreeDataVector> forceField;
	
	public ParticlesCache cache = new ParticlesCache();

	public float drag = 1f;
	public float maxVelocity = 100f;
	
	Boolean isPlaying = false;

	public Boolean isPlaying() {
		return isPlaying;
	}

	public ParticleSystem(Bounds initialBounds) {
		bounds.set(initialBounds);
		this.forceField = new QuadTree<QuadTreeDataVector>(bounds);
	}

	public int getLiveParticleCount() {
		int count = 0;
		for (Particle p : particles) {
			if (p.live)
				count++;
		}

		return count;
	}

	public void addForce(PVector position, PVector value) {
		QuadTreeDataVector v = new QuadTreeDataVector(position, value);
		forceField.insert(v);
	}

	public void Play() {
		Reset();
		lastUpdateTime = System.currentTimeMillis();
		isPlaying = true;
	}

	public void Reset() {
		isPlaying = false;
		for (Particle p : particles)
			p.reset();
		
		if(cache.enabled) {
			cache.clearPoints();
		}
	}

	long lastUpdateTime = 0;

	public void update() {

		if (!isPlaying)
			return;

		long currentUpdateTime = System.currentTimeMillis();

		long time = currentUpdateTime - lastUpdateTime;
		float deltaTime = time * 0.001f;

		update(deltaTime);
		lastUpdateTime = currentUpdateTime;
	}

	public void update(float deltaTime) {
		for (Particle p : particles) {
			if (p.live)
				p.updateTime(deltaTime);
		}

		for (Particle p : particles) {
			if (p.live)
				p.doForces();
		}

		for (Particle p : particles) {
			if (p.live)
				p.update(deltaTime);
		}
		
		if(cache.enabled) {
			cache.update();
		}

	}

	public Particle createParticle(PVector initPosition, float lifetime) {
		Particle p = new Particle(this);
		p.id = particles.size();
		p.lifetime = lifetime;
		p.init(initPosition);
		particles.add(p);
		
		if(cache.enabled) {
			cache.AddParticle(p);
		}
		
		return p;
	}

	public void clear() {
		cache.clearParticles();
		forceField.clear();
		particles.clear();
	}
}

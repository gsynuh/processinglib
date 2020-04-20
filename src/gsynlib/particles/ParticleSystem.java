package gsynlib.particles;

import java.util.*;
import gsynlib.geom.*;
import processing.core.*;

public class ParticleSystem {

	public ArrayList<Particle> particles = new ArrayList<Particle>();
	public ForceField forceField;
	
	public ParticleSystem(Bounds initialBounds) {
		this.forceField = new ForceField(initialBounds);
	}
	
	public void init() {
		lastUpdateTime = System.currentTimeMillis();	
	}
	
	public int getLiveParticleCount() {
		int count = 0;
		for(Particle p : particles) {
			if(p.live)
				count++;
		}
		
		return count;
	}
	
	long lastUpdateTime = 0;
	public void update() {
		long currentUpdateTime = System.currentTimeMillis();
		
		long time = currentUpdateTime - lastUpdateTime;
		float deltaTime = time * 0.001f;
		update(deltaTime);	
		lastUpdateTime = currentUpdateTime;
	}
	
	public void update(float deltaTime) {
		for(Particle p : particles) {
			if(p.live)
				p.updateTime(deltaTime);
		}
		
		for(Particle p : particles) {
			if(p.live)
				p.update(deltaTime);
		}
		
	}
	
	public Particle createParticle(PVector initPosition,float lifetime) {
		return createParticle(initPosition, new PVector() ,lifetime);
	}
	
	public Particle createParticle(PVector initPosition, PVector initVel, float lifetime) {
		Particle p = new Particle(this);
		p.id = particles.size();
		p.lifetime = lifetime;
		p.init(initPosition, initVel);
		particles.add(p);
		return p;
	}
	
	public void clear() {
		forceField.clear();
		particles.clear();
	}
}

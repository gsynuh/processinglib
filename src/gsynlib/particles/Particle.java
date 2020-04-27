package gsynlib.particles;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.*;

import gsynlib.geom.QuadTreeDataVector;

public class Particle {
	
	public int  id = 0;
	public float lifetime = 1f;
	public float currentTime = 0f;
	public Boolean live = true;
	
	public PVector position = new PVector();
	public PVector velocity = new PVector();
	public PVector acceleration = new PVector();
	
	public PVector initialPosition = new PVector();
	public PVector initialVelocity = new PVector();
	public PVector initialAccel = new PVector();
	
	ParticleSystem ps;
	
	public Particle(ParticleSystem _ps) {
		this.ps = _ps;
	}
	
	public void reset() {
		currentTime = 0f;
		live = true;
		
		position.set(initialPosition);
		velocity.set(initialVelocity);
		acceleration.set(initialAccel);
	}
	
	public void init(PVector initPos) {
		this.position.set(initPos);
		this.initialPosition.set(initPos);
	}
	
	public void updateTime(float deltaTime) {
		currentTime += deltaTime;
		
		if(lifetime >= 0f) {
			if(currentTime >= lifetime) {
				this.live = false;
			}
		}
	}
	
	ArrayList<QuadTreeDataVector> queryResults = new ArrayList<QuadTreeDataVector>();
	PVector f = new PVector();
	public void doForces() {
		queryResults.clear();
		ps.forceField.queryCircle(this.position, 50, queryResults);
		
		f.set(0,0);
		float c = 0f;
		
		for(QuadTreeDataVector v : queryResults) { 
			f.add(v.vector);
			c++;
		}
		
		if(c > 0) {
			f.div(c);
		}
		
		this.acceleration.add(f);
		
	}
	
	public void update(float deltaTime) {
		
		this.velocity.x += this.acceleration.x;
		this.velocity.y += this.acceleration.y;
		
		this.velocity.x *= ps.drag;
		this.velocity.y *= ps.drag;
		
		this.velocity.limit(ps.maxVelocity);
		
		this.position.x += this.velocity.x * deltaTime;
		this.position.y += this.velocity.y * deltaTime;
		
		if(this.ps.wrapParticles) {
			doBounds();
		}
		
	}
	
	void doBounds() {
		if(this.position.x > ps.bounds.bottomRight.x) {
			this.position.x = ps.bounds.position.x + abs(ps.bounds.bottomRight.x - this.position.x);
		}
		
		if(this.position.x < ps.bounds.position.x) {
			this.position.x = ps.bounds.bottomRight.x - abs(ps.bounds.bottomRight.x - this.position.x);
		}
		
		if(this.position.y > ps.bounds.bottomRight.y) {
			this.position.y = ps.bounds.position.y + abs(ps.bounds.bottomRight.y - this.position.y);
		}
		
		if(this.position.y < ps.bounds.position.y) {
			this.position.y = ps.bounds.bottomRight.y - abs(ps.bounds.position.y - this.position.y);
		}
	}
	
}

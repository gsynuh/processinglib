package gsynlib.particles;

import processing.core.PVector;

public class Particle {
	
	public int  id = 0;
	public float lifetime = 1f;
	public float currentTime = 0f;
	public Boolean live = true;
	
	public PVector position = new PVector();
	public PVector velocity = new PVector();
	
	PVector initialPosition = new PVector();
	PVector initialVelocity = new PVector();
	
	ParticleSystem ps;
	
	public Particle(ParticleSystem _ps) {
		this.ps = _ps;
	}
	
	public void reset() {
		currentTime = 0f;
		live = true;
		
		position.set(initialPosition);
		velocity.set(initialVelocity);
	}
	
	public void init(PVector initPos) {
		this.position.set(initPos);
		this.initialPosition.set(initPos);
	}
	
	public void init(PVector initPos,PVector initVel) {
		init(initPos);
		this.velocity.set(initVel);
		this.initialVelocity.set(initVel);
	}
	
	public void updateTime(float deltaTime) {
		currentTime += deltaTime;
		
		if(lifetime >= 0f) {
			if(currentTime >= lifetime) {
				this.live = false;
			}
		}
	}
	
	public void update(float deltaTime) {
		this.position.x += this.velocity.x * deltaTime;
		this.position.y += this.velocity.y * deltaTime;
	}
	
}

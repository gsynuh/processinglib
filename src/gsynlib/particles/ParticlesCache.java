package gsynlib.particles;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.*;
import java.util.Map.*;


public class ParticlesCache {
	
	public float precisionAngle = 0.1f;
	public float minDist = 0f;
	public float maxDist = 20f;
	public Boolean enabled = false;
	Map<Particle,CachedParticle> cachedParticles = new HashMap<Particle,CachedParticle>();
	
	public Map<Particle,CachedParticle> getCache() {
		return cachedParticles;
	}
	
	public void clearParticles() {
		cachedParticles.clear();
	}
	
	public void clearPoints() {
		for(Entry<Particle,CachedParticle> kvp : cachedParticles.entrySet()) {
			CachedParticle cp = kvp.getValue();
			cp.clear();
		}
	}
	
	public void AddParticle(Particle p) {
		if(cachedParticles.containsKey(p))
			return;
		
		CachedParticle cp = new CachedParticle();
		cp.cache = this;
		cachedParticles.put(p, cp);
	}
	
	public void update() {
		for(Entry<Particle,CachedParticle> kvp : cachedParticles.entrySet()) {
			Particle p = kvp.getKey();
			
			if(!p.live)
				continue;
			
			CachedParticle cp = kvp.getValue();
			
			cp.addPoint(p.position.copy());
		}
	}
	
	public void simplify() {
		
	}
}

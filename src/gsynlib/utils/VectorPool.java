package gsynlib.utils;

import java.util.ArrayList;

import processing.core.PVector;

public class VectorPool {

	Boolean vectorPoolInitialized = false;
	int livePVCount = 0;
	
	int increments = 32;
	
	ArrayList<PVector> vectorPool = new ArrayList<PVector>();
	ArrayList<PVector> liveVec = new ArrayList<PVector>();
	
	public VectorPool(int inc) {
		this.increments = inc;
		InitializePool();
	}
	
	public void clear() {
		vectorPool.clear();
		liveVec.clear();
		livePVCount = 0;
		InitializePool();
	}
	
	public void InitializePool() {
		if(vectorPoolInitialized)
			return;
		
		IncrementPoolSize(increments);
		vectorPoolInitialized = true;
	}
	
	void IncrementPoolSize(int size) {
		for(int i = 0; i < size; i++) {
			vectorPool.add(new PVector());
		}
	}
	
	public PVector get() {
		if(!vectorPoolInitialized) {
			InitializePool();
		}
		
		if(livePVCount >= vectorPool.size()) {
			IncrementPoolSize(increments);
		}
		
		PVector vec =  vectorPool.get(livePVCount);
		vec.set(0,0,0);
		
		if(liveVec.add(vec)) {
			livePVCount++;
			vectorPool.remove(vec);
		}
		return vec;
	}
	
	public void dispose(PVector p) {
		if(liveVec.remove(p)) {
			livePVCount--;
			vectorPool.add(p);
		}
	}
}

package gsynlib.scheduling;

import gsynlib.base.GsynlibBase;
import processing.core.PApplet;

import static processing.core.PApplet.*;

public abstract class StatefulCommand extends GsynlibBase {

	public enum RUNSTATE  {
		CREATED,
		STARTED,
		DONE,
		THRASH
	}

	public long startTime = 0;
	public float currentTime = 0;
	public float totalTime = -1;
	public float normalizedTime = 0;
	
	public RUNSTATE state = RUNSTATE.CREATED;

	public void start() {
		startTime = System.currentTimeMillis();
		currentTime = 0f;
		normalizedTime = 1f;
		state = RUNSTATE.STARTED;
	}
	
	protected void calcCurrentTime() {
		long time = System.currentTimeMillis() - startTime;
		currentTime = time * 0.001f;
	}
	
	protected void finishCommand() {
		normalizedTime = 1f;
		this.state = RUNSTATE.DONE;
	}
	
	protected void updateTime() {
		calcCurrentTime();
		if(totalTime >= 0f) {
			normalizedTime = map(currentTime, 0f, totalTime, 0f, 1f);
			normalizedTime = constrain(normalizedTime,0f,1f);
		} else {
			normalizedTime = 1f;
		}
	}
	
	public void update() {
		updateTime();
		if (normalizedTime >= 1f){
			finishCommand();
			execute();
		}
	}
	
	public void stop() {
		this.state = RUNSTATE.THRASH;
	}

	public void execute() {
		finishCommand();
	}

	public void forceFinalize() {
		finishCommand();
	}
}

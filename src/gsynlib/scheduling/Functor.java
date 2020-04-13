package gsynlib.scheduling;

import processing.core.*;

public class Functor {
	public long startTime = 0;
	public float currentTime = 0;
	public float currentRunTime = 0;
	public float waitTime = 0;
	public float runTime = 0;
	public Boolean initialized = false;
	public Boolean done = false;

	public int executeCallCount = 0;
	
	public void init() {
		if (initialized)
			return;

		startTime = System.currentTimeMillis();
		initialized = true;
	}

	public void execute() {
	}

	public Boolean canExecute() {
		if (runTime > 0 && initialized) {
			return currentTime < runTime + 0.01;
		}

		return true;
	}

	public void updateTime() {
		if (!initialized)
			return;

		long dtime = System.currentTimeMillis() - startTime;
		float ms = dtime;
		float dtimeF = ms * 0.001f;

		if (runTime > 0)
			currentRunTime = PApplet.map(dtimeF - 0.01f, 0, runTime, 0, 1);
		else
			currentRunTime = 0;

		Boolean runFinished = dtimeF > this.runTime;
		Boolean waitFinished = dtimeF > this.runTime + this.waitTime;

		if (runTime > 0 || waitTime > 0) {
			if (runFinished && waitFinished) {
				this.done = true;
			} else {
				this.done = false;
			}
		} else {
			this.done = true;
		}
	}
}

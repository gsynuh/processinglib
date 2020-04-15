package gsynlib.scheduling;

import static processing.core.PApplet.*;

import gsynlib.base.GsynlibBase;

public class Functor extends GsynlibBase {

	public Boolean continuous = false;

	public long startTime = 0;
	public float currentTime = 0;
	public float normalizedTime = 0;
	public float runTime = 0;

	public Boolean initialized = false;
	public Boolean done = false;

	public int executeCallCount = 0;

	public void init() {
		if (initialized)
			return;

		startTime = System.currentTimeMillis();
		initialized = true;
		
		continuous = runTime > 0f;

		if (!continuous) {
			normalizedTime = 1f;
			done = true;
		}
	}

	public void execute() {
	}

	public void updateTime() {

		if (!initialized) {
			normalizedTime = 0f;
			return;
		}
		
		if (normalizedTime >= 1f) {
			normalizedTime = 1f;
			this.done = true;
			return;
		}

		long time = System.currentTimeMillis() - startTime;
		currentTime = time * 0.001f;

		if (continuous) {
			
			normalizedTime = map(currentTime, 0, runTime, 0, 1);
			normalizedTime = constrain(normalizedTime, 0f, 1f);

		}
	}
}

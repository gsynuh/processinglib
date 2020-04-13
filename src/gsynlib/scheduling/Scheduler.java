package gsynlib.scheduling;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import gsynlib.base.*;
import static processing.core.PApplet.*;

public class Scheduler extends GsynlibBase {

	Timer timer;

	Boolean running = false;

	Object externalTask;
	Method externalRunMethod;
	
	public String name;

	TimerTask task;

	protected class MainTask extends TimerTask {
		protected Scheduler s;

		MainTask(Scheduler _s) {
			this.s = _s;
		}

		@Override
		public void run() {
			this.s.execute();
		}
	}

	public Scheduler() {
		name = this.toString();
		timer = new Timer();
		task = new MainTask(this);
	}

	public void setTask(Object o, String methodName) {
		this.externalTask = o;

		try {
			externalRunMethod = this.externalTask.getClass().getDeclaredMethod(methodName);
			externalRunMethod.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		if (externalRunMethod == null) {
			println("Cannot find '" + methodName + "' method on " + this.externalTask);
			this.externalTask = null;
		}
	}

	public void start(int freq) {

		if (this.externalTask == null) {
			println(
					"Cannot start without task. define an object and its method name to be called using setTask.");
			return;
		}

		stop();

		if (task != null) {
			timer.scheduleAtFixedRate(task, 0, freq);
			println(
					"Scheduler started " + name + " at freq " +freq);
			running = true;
		}
	}

	public void start() {
		this.start(30);
	}

	public void stop() {
		if (running)
			timer.cancel();
	}

	public void destroy() {
		stop();
		timer.cancel();
		timer.purge();
	}

	public void execute() {

		if (this.externalTask == null) {
			println("NO EXTERNAL TASK" + app().millis());
			return;
		}

		try {
			this.externalRunMethod.invoke(externalTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

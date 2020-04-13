package gsynlib.vigoxy.functors;

import gsynlib.scheduling.Functor;
import gsynlib.vigoxy.PlotterXY;
import processing.core.*;

public class CursorValueChange extends Functor {
	PVector ref;
	PVector from;
	PVector to;
	PlotterXY pxy;

	public CursorValueChange(PlotterXY p, PVector reference, PVector from, PVector to) {
		
		super();
		
		this.pxy = p;
		this.ref = reference;
		this.from = from;
		this.to = to;

		float dist = PVector.sub(to, from).mag();

		if (dist > 0.001f) {
			if (this.pxy.moveState == PlotterXY.MOVE_STATE.FAST) {
				this.runTime = dist / this.pxy.fastMoveSpeed;
			} else if (this.pxy.moveState == PlotterXY.MOVE_STATE.PRECISE) {
				this.runTime = dist / (this.pxy.slowMoveSpeed * 0.01f);
			}
		}

	}

	public void execute() {
		super.execute();
		this.ref.x = PApplet.lerp(this.from.x, this.to.x, this.currentRunTime);
		this.ref.y = PApplet.lerp(this.from.y, this.to.y, this.currentRunTime);

		if (this.currentRunTime >= this.runTime - 0.001) {
			this.ref.x = this.to.x;
			this.ref.y = this.to.y;
		}

	}
}
package gsynlib.vigoxy.functors;

import gsynlib.scheduling.StatefulCommand;
import gsynlib.vigoxy.PlotterXY;
import processing.core.*;
import static processing.core.PApplet.*;

public class CursorValueChange extends StatefulCommand {
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

	}
	
	@Override 
	public void start() {
		super.start();
		
		float dist = PVector.sub(to, from).mag();
		if (dist > 0.001f) {
			if (this.pxy.moveState == PlotterXY.MOVE_STATE.FAST) {
				this.totalTime = dist / this.pxy.fastMoveSpeed;
			} else if (this.pxy.moveState == PlotterXY.MOVE_STATE.PRECISE) {
				this.totalTime = dist / (this.pxy.slowMoveSpeed * 0.01f);
			}
		}
	}
	
	@Override
	public void update() {
		super.updateTime();
		
		this.ref.x = lerp(this.from.x, this.to.x, this.normalizedTime);
		this.ref.y = lerp(this.from.y, this.to.y, this.normalizedTime);
		
		if (this.normalizedTime >= 1f) {
			this.ref.x = this.to.x;
			this.ref.y = this.to.y;
			execute();
		}
	}
}
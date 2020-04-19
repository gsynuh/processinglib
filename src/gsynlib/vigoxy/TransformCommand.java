package gsynlib.vigoxy;

import processing.core.PApplet;
import processing.core.*;

public class TransformCommand extends PlotterCommand {

	public enum TXTYPE {
		NONE, PUSH, POP, MAT
	}

	public TransformCommand(PMatrix2D mat) {
		super();
		this.matrix = mat.get();
		matrixSet = true;
	}

	public TransformCommand() {
		super();
	}

	public String name = "TCommand ";
	Boolean matrixSet = false;
	PMatrix2D matrix;

	public TXTYPE type = TXTYPE.NONE;

	public void ApplyCommandToTStack(TransformStack ts) {

		switch (type) {

		case PUSH:
			ts.pushMatrix();
			break;

		case POP:
			ts.popMatrix();
			break;

		case MAT:
			if (matrixSet)
				ts.applyMatrix(matrix);
			break;
		default:
			PApplet.println("TransformCommand unknown type ", this.name);
			break;
		}

	}

}

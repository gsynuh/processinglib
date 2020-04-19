package gsynlib.vigoxy;

import processing.core.*;

public class TransformStack {
	public PMatrix2D[] transformStack;
	int transformIndex = 0;

	static PMatrix2D _baseMatrix = new PMatrix2D();

	PMatrix2D _currentMatrix = new PMatrix2D();
	PMatrix2D _finalMatrix = new PMatrix2D();

	public PMatrix2D getFinalMatrix() {
		return _finalMatrix;
	}

	public TransformStack() {

		transformIndex = 0;
		transformStack = new PMatrix2D[128];

		for (int i = 0; i < transformStack.length; i++) {
			transformStack[i] = _baseMatrix.get();
		}

		reset();
	}

	public void reset() {

		_finalMatrix.set(_baseMatrix);
		transformIndex = 0;

		for (int i = 0; i < transformStack.length; i++) {
			PMatrix2D m = transformStack[i];
			m.set(_baseMatrix);
		}

		updateFinalMatrix();

	}

	public void pushMatrix() {
		transformIndex++;
		_currentMatrix = transformStack[transformIndex];
		_currentMatrix.set(_baseMatrix);
		updateFinalMatrix();
	}

	public void popMatrix() {
		transformIndex--;
		_currentMatrix = transformStack[transformIndex];
		updateFinalMatrix();
	}

	public void applyMatrix(PMatrix2D m) {
		_currentMatrix.apply(m);
		updateFinalMatrix();
	}

	void updateFinalMatrix() {

		_finalMatrix.set(_baseMatrix);

		for (int i = 0; i <= transformIndex; i++) {
			_finalMatrix.apply(transformStack[i]);
		}

	}

}

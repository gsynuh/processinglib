package gsynlib.vigoxy;

import java.lang.reflect.*;

import java.util.*;

import gsynlib.base.GsynlibBase;

import gsynlib.bezier.*;
import gsynlib.geom.*;
import gsynlib.image.*;
import gsynlib.scheduling.*;
import processing.core.*;
import static processing.core.PApplet.*;

public class PlotterCanvas extends GsynlibBase {

	public float screenScale = 1f;

	public float maxLengthToDraw = 15.1f;
	public Boolean debugLinesDI = false;

	protected PlotterXY plotter;
	protected ArrayList<DrawCommand> commands;

	Boolean prepared = false;

	Bounds bounds = new Bounds();
	Bounds drawBounds = new Bounds();

	public Bounds getBounds() {
		return this.bounds;
	}

	public PVector getRandomPointOnCanvas() {
		return this.bounds.getRandom();
	}

	// REFLECTION (get PApplet prepare method if it exists)
	Method externalPrepareMethodA;
	Method externalPrepareMethodB;

	public PlotterCanvas(PlotterXY pxy) {
		this.plotter = pxy;
		this.commands = new ArrayList<DrawCommand>();

		app().registerMethod("draw", this);
		getMethods();

		prepare();
		bake();
	}

	void getMethods() {
		try {
			externalPrepareMethodA = app().getClass().getDeclaredMethod("prepareXY", this.getClass());
			externalPrepareMethodA.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
		}

		try {
			externalPrepareMethodB = app().getClass().getDeclaredMethod("prepareXY");
			externalPrepareMethodB.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException e) {
		}
	}

	public void setCanvasBounds(Bounds b) {
		bounds.copyFrom(b);
	}

	public void setDrawBounds(Bounds b) {
		drawBounds.copyFrom(b);
	}

	public void prepare() {

		this.clear();

		try {
			if (externalPrepareMethodA != null) {
				externalPrepareMethodA.invoke(app(), this);
			} else if (externalPrepareMethodB != null) {
				externalPrepareMethodB.invoke(app());
			} else {
				println("Cannot find prepareXY method on main sketch");
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			println("Couldn't call prepareXY on", app());
			e.printStackTrace();
		}

		for (DrawCommand c : commands) {
			c.prepare();
		}

		prepared = true;
	}

	public void bake() {

		if (!prepared) {
			println("Cannot bake unless prepared ! make sure to implement prepareXY");
			return;
		}

		if (pendingCommand != null) {
			println("A shape has begun and not ended");
		}

		reset();
	}

//------------------- DRAW COMMANDS --------------------

	public void clear() {
		commands.clear();
	}

	public void reset() {
		for (DrawCommand dc : commands) {
			dc.reset();
			dc.bake();
		}
	}

	public void image(PImage im, float x, float y, float w, float h) {
		Bounds imageArea = new Bounds(x, y, w, h);

		class ImageDC extends DrawCommand {
			PImage image;
			Bounds bounds;
			Dithering ditherFilter;

			public ImageDC(PlotterCanvas pc, PImage im, Bounds b) {
				super(pc);
				this.connectPointsOnDraw = false;
				this.bounds = b;

				this.image = app().createImage(im.width, im.height, ARGB);
				this.image.copy(im, 0, 0, im.width, im.height, 0, 0, im.width, im.height);

				this.ditherFilter = new Dithering();
				this.ditherFilter.luminance = true;
				this.ditherFilter.quantizeFactor = 1;
				this.ditherFilter.CreateFilter(this.image);
			}

			@Override
			public void bakePoints() {
				for (int x = 0; x < this.ditherFilter.width; x++) {
					for (int y = 0; y < this.ditherFilter.height; y++) {
						ColorFloat c = this.ditherFilter.getColor(x, y);
						float u = (float) x / (float) this.ditherFilter.width;
						float v = (float) y / (float) this.ditherFilter.height;
						if (c.r <= 0.2f) {
							PVector p = this.bounds.getPositionFromNorm(u, v);
							bakedPoints.add(p);
						}
					}
				}
			}
		}

		ImageDC idc = new ImageDC(this, im, imageArea);
		commands.add(idc);
	}

	public void bezierLoop(int curvesNum, float x, float y, float w, float h) {
		class BezierLoopDC extends DrawCommand {
			BezierLoop loop = null;

			public BezierLoopDC(PlotterCanvas pc, PVector... pts) {
				super(pc, pts);
				loop = new BezierLoop();
				loop.setTargetBounds(new Bounds(pts[0], pts[1]));
				loop.init(curvesNum, 0);
			}

			@Override
			public void bakePoints() {
				for (int i = 0; i < loop.getBakedPoints().size(); i++) {
					PVector p = loop.getBakedPoints().get(i);
					bakeFill(p);
					bakedPoints.add(p);
				}
			}
		}

		BezierLoopDC bloopdc = new BezierLoopDC(this, new PVector(x, y), new PVector(w, h));
		commands.add(bloopdc);
	}

	public void point(float x, float y) {

		class PointDC extends DrawCommand {
			public PointDC(PlotterCanvas pc, PVector... pts) {
				super(pc, pts);
				this.connectPointsOnDraw = false;
			}

			@Override
			public void bakePoints() {
				PVector p = originalPoints.get(0);
				bakedPoints.add(p.copy());
			}
		}

		PointDC pdc = new PointDC(this, new PVector(x, y));
		commands.add(pdc);
	}

	public void rect(float x, float y, float w, float h) {

		class RectDC extends DrawCommand {
			public RectDC(PlotterCanvas pc, PVector... pts) {
				super(pc, pts);
			}

			@Override
			public void bakePoints() {
				PVector pos = originalPoints.get(0);
				PVector size = originalPoints.get(1);

				PVector p1 = pos.copy();
				PVector p2 = new PVector(pos.x + size.x, pos.y);
				PVector p3 = new PVector(pos.x + size.x, pos.y + size.y);
				PVector p4 = new PVector(pos.x, pos.y + size.y);

				bakedPoints.add(p1);
				bakeFill(p2);
				bakedPoints.add(p2);
				bakeFill(p3);
				bakedPoints.add(p3);
				bakeFill(p4);
				bakedPoints.add(p4);
				bakeFill(p1);
				bakedPoints.add(p1);
			}
		}

		RectDC rectdc = new RectDC(this, new PVector(x, y), new PVector(w, h));
		commands.add(rectdc);
	}

	public void line(float x1, float y1, float x2, float y2) {
		class LineDC extends DrawCommand {
			public LineDC(PlotterCanvas pc, PVector... pts) {
				super(pc, pts);
			}

			@Override
			public void bakePoints() {
				PVector p1 = originalPoints.get(0);
				PVector p2 = originalPoints.get(1);

				bakedPoints.add(p1);
				bakeFill(p2);
				bakedPoints.add(p2);

			}
		}

		LineDC linedc = new LineDC(this, new PVector(x1, y1), new PVector(x2, y2));
		commands.add(linedc);
	}

	public void circle(float x, float y, float r) {

		class CircleDC extends DrawCommand {
			public CircleDC(PlotterCanvas pc, PVector... pts) {
				super(pc, pts);
			}

			float getCircleDiv(float v) {
				float c = round(v);

				float p = TWO_PI * v;
				float maxP = (canvas.maxLengthToDraw * 2f) / p;

				c = 12.0f / maxP;

				if (c < 4)
					c = 3;
				if (c >= 360)
					c = 360;
				return c;
			}

			@Override
			public void bakePoints() {
				PVector pa = originalPoints.get(0);
				float x = pa.x;
				float y = pa.y;
				float r = pa.z;

				float divA = getCircleDiv(r);
				float aDiv = TWO_PI / divA;

				for (int i = 0; i <= divA; i++) {
					PVector p = new PVector(cos(i * aDiv) * r + pa.x, sin(i * aDiv) * r + pa.y);

					bakeFill(p);
					bakedPoints.add(p);
				}

				PVector p = new PVector(cos(0) * r + pa.x, sin(0) * r + pa.y);
				bakedPoints.add(p);

			}
		}

		CircleDC circledc = new CircleDC(this, new PVector(x, y, r));
		commands.add(circledc);
	}

	DrawCommand pendingCommand;

	public void beginShape() {
		pendingCommand = new DrawCommand(this);
	}

	public void vertex(float x, float y) {
		if (pendingCommand != null) {
			PVector p = new PVector(x, y);
			pendingCommand.originalPoints.add(p.copy());
		}
	}

	public void endShape(Boolean close) {

		// ADD FIRST POINT TO END TO CLOSE THE SHAPE
		if (close)
			pendingCommand.originalPoints.add(pendingCommand.originalPoints.get(0).copy());

		commands.add(pendingCommand);
		pendingCommand = null;
	}

//------------------- DRAW COMMANDS --------------------

	void startDrawCommand() {
		plotter.penReset();
		plotter.penUp();
		plotter.backToOrigin();
	}

	void endDrawCommand() {
		plotter.penUp();
		plotter.backToOrigin();
		
		plotter.addCommand(new StatefulCommand() {
			@Override
			public void start() {
				super.start();
				resetDrawCommands();
				finishCommand();
			}
		});
	}

	public void showBounds() {
		showBounds(0);
	}

	public void showBounds(int count) {

		if (count == 0) {
			startDrawCommand();
			plotter.SetMoveState(PlotterXY.MOVE_STATE.FAST);
		}

		class DrawBoundsFunctor extends StatefulCommand {
			PlotterCanvas canvas;
			PlotterXY plotter;
			int count;

			public DrawBoundsFunctor(PlotterXY p, PlotterCanvas c, int count) {
				this.canvas = c;
				this.plotter = p;
				this.count = count;
			}

			@Override
			public void execute() {
				super.execute();
				canvas.showBounds(this.count);
			}
		}

		PVector p1 = this.bounds.position.copy();
		PVector p3 = this.bounds.getBottomRight();
		PVector p2 = new PVector(p1.x + p3.x, p1.y);
		PVector p4 = new PVector(p1.x, p1.y + p3.y);

		// plotter.unsafeClear();
		plotter.moveTo(p1);
		plotter.moveTo(p2);
		plotter.moveTo(p3);
		plotter.moveTo(p4);

		DrawBoundsFunctor f = new DrawBoundsFunctor(plotter, this, count + 1);
		plotter.addCommand(f);

		if (count == 0)
			endDrawCommand();
	}

	void printCommand(DrawCommand dc, Boolean penDown) {

		//DRAW PROGRESS COMMANDS
		
		class CommandDrawProgress extends StatefulCommand {
			DrawCommand dc = null;

			public CommandDrawProgress(DrawCommand _dc) {
				this.dc = _dc;
			}

			@Override
			public void start() {
				super.start();
				this.dc.drawnPointCount++;
				super.finishCommand();
			}
		}
		
		//DRAW COMMAND 
		
		
		// FORCE UP
		plotter.penUp();
		// MOVE TO INITIAL POINT
		PVector p = dc.bakedPoints.get(0);
		plotter.moveTo(p.x, p.y);

		if (dc.connectPointsOnDraw) {

			if (penDown)
				plotter.penDown();
			
			plotter.addCommand(new CommandDrawProgress(dc));
			// DRAW PATH
			for (int i = 1; i < dc.bakedPoints.size(); i++) {
				p = dc.bakedPoints.get(i);
				plotter.moveTo(p.x, p.y);
				plotter.addCommand(new CommandDrawProgress(dc));
			}

		} else {
			
			for (int i = 0; i < dc.bakedPoints.size(); i++) {
				p = dc.bakedPoints.get(i);
				plotter.moveTo(p.x, p.y);
				if (penDown)
					plotter.penDown();
				plotter.addCommand(new CommandDrawProgress(dc));
				plotter.penUp();
			}
		}

		// PEN UP
		plotter.penUp();
	}

	void resetDrawCommands() {
		for (int i = 0; i < commands.size(); i++) {
			DrawCommand dc = commands.get(i);
			dc.reset();
		}
	}

	public void print() {
		resetDrawCommands();
		startDrawCommand();
		println("PRINT");

		for (int i = 0; i < commands.size(); i++) {
			DrawCommand dc = commands.get(i);
			printCommand(dc, true);
		}

		endDrawCommand();
	}

	public void testPrint() {
		resetDrawCommands();
		startDrawCommand();
		println("TEST PRINT");

		for (int i = 0; i < commands.size(); i++) {
			DrawCommand dc = commands.get(i);
			printCommand(dc, false);
		}

		endDrawCommand();
	}

	public int backgroundColor = 0x00FFFFFF;
	public int canvasColor = 0xFFFFFFFF;

	public void draw() {

		float rx = drawBounds.size.x / bounds.size.x;
		float ry = drawBounds.size.y / bounds.size.y;
		float s = rx > ry ? ry : rx;

		screenScale = 1f / s;

		PVector drawBoundsC = drawBounds.getCenter();

		app().fill(backgroundColor);
		app().rect(drawBounds.position.x, drawBounds.position.y, drawBounds.size.x, drawBounds.size.y);

		app().pushStyle();
		app().pushMatrix();

		app().translate(drawBoundsC.x, drawBoundsC.y);
		app().scale(s, s);
		app().translate(-bounds.size.x / 2, -bounds.size.y / 2);

		drawBounds();
		drawCommands();
		drawCursor(s);

		app().popMatrix();
		app().popStyle();
	}

	void drawCommands() {
		app().pushStyle();
		app().pushMatrix();
		for (DrawCommand dc : commands) {
			dc.draw();
		}
		app().popMatrix();
		app().popStyle();
	}

	void drawBounds() {
		app().pushStyle();
		app().pushMatrix();
		app().fill(canvasColor);
		app().rect(bounds.position.x, bounds.position.y, bounds.size.x, bounds.size.y);
		app().popMatrix();
		app().popStyle();
	}

	void drawCursor(float scale) {
		app().pushMatrix();

		PVector displayCursor = plotter.getDisplayCursor();

		app().translate(displayCursor.x, displayCursor.y);
		app().stroke(255, 0, 0);
		app().strokeWeight(screenScale * 2f);

		app().noFill();
		float size = 60 * screenScale;

		app().ellipse(0, 0, size / 2, size / 2);
		app().line(-size / 2, 0, -size / 8, 0);
		app().line(size / 8, 0, size / 2, 0);
		app().line(0, -size / 2, 0, -size / 8);
		app().line(0, size / 8, 0, size / 2);

		app().fill(0);
		app().textSize(size * 0.2f);
		app().text("CD" + printVec(displayCursor), size * 0.2f, - size * 0.4f);

		app().popMatrix();
	}

	String printVec(PVector vec) {
		return "x:" + vec.x + " y:" + vec.y;
	}
}

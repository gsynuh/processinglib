package gsynlib.vigoxy;

import java.util.*;

import gsynlib.base.GsynlibBase;

import gsynlib.scheduling.*;
import gsynlib.utils.GApp;
import gsynlib.vigoxy.functors.*;
import jssc.*;
import processing.core.*;
import processing.serial.*;
import static processing.core.PApplet.*;

//PlotterXY sends GCODE command to serial
//Made with the commands of Vigo Tec's writer/engraver in mind
//https://www.extremeelectronics.co.uk/vigo-tec-vg-a4-writer-engraver/
//PlotterXY uses a timer/scheduler to progressively either send serial command or execute actions via the "Functor" class in the gsynlib.scheduling package. 

public class PlotterXY extends GsynlibBase implements SerialPortEventListener {

	public enum PenState {
		DOWN, UP, UNKNOWN,
	}

	public enum MOVE_STATE {
		UNKNOWN, FAST, PRECISE,
	}

	public int fastMoveSpeed = 72;
	public int slowMoveSpeed = 1000;

	public MOVE_STATE moveState = MOVE_STATE.UNKNOWN;
	public PenState pen = PenState.UNKNOWN;
	public Boolean initialized = false;

	Serial serialPort;
	int lf = 0x0D;
	String lfstr = "";

	Scheduler scheduler;

	// QUEUE OF COMMANDS
	ArrayList<Functor> commands = new ArrayList<Functor>();

	PVector cursorA = new PVector(0, 0);
	PVector cursorD = new PVector(0, 0);
	PVector motorPosition = new PVector();

	public PVector getCursor() {
		return cursorA;
	}

	public PVector getDisplayCursor() {
		return cursorD;
	}

	public PVector getMotorPos() {
		return motorPosition;
	}

	public int getCommandCount() {
		return commands.size();
	}
	
	public void addCommand(Functor f) {
		this.commands.add(f);
	}

	public PlotterXY(String _serialPortName) {
		lfstr = GApp.hexToAscii("" + lf);

		PApplet a = app();

		serialPort = new Serial(a, _serialPortName, 115200);
		serialPort.bufferUntil(lf);

		try {
			if (serialPort.port.removeEventListener()) {
				serialPort.port.addEventListener(this);
			} else {
				println("Couldn't remove port listener");
			}
		} catch (SerialPortException e) {
			e.printStackTrace();
		}

		println("Plotter uses device at " + _serialPortName);

		scheduler = new Scheduler();
		scheduler.name = "PlotterXY_Scheduler";
	}

	public void open() {

		// set scheduler to run this' schedulExecute function. commands will be
		// processed there independantly from the app's frame rate.
		scheduler.setTask(this, "scheduleExecute");

		// Init command for the plotter
		send("V4&^CMP*GWFIK5SHA$CPE");

		// start scheduler, asking for 15ms period
		scheduler.start(15);

		// Try to read the serial port just in case
		String firstRead = serialPort.readString();
		processReadString(firstRead);
	}

	protected void scheduleExecute() {
		processMessages();
	}

	void processMessages() {

		if (commands.size() > 0) {

			Functor func = commands.get(0);

			if (!func.initialized) {
				func.init();

			}
			
			func.updateTime();
			
			func.execute();
			func.executeCallCount++;

			if (func.done)
				commands.remove(0);
		}
	}

	public void close() {
		this.penUp();
		this.backToOrigin();
		serialPort.stop();
		scheduler.stop();
	}

	public void QueueCommand(Functor f, int waitMS) {

		if (waitMS > 0)
			f.runTime = waitMS / 1000.0f;

		commands.add(f);
	}

	public void QueueCommand(Functor f) {
		QueueCommand(f, 0);
	}

	void processReadString(String str) {

		if (str == null || str.isEmpty()) {
			return;
		}

		if (!this.initialized && (str.contains("Vigo") || str.contains("MPos"))) {
			this.initialized = true;
			initResponse();
		}
	}

	void initResponse() {
		send("M3 S0");
		penUp();
		SetMoveState(MOVE_STATE.FAST);
	}

	void SetMoveState(MOVE_STATE s) {
		if (s == MOVE_STATE.FAST)
			send("G1 S" + fastMoveSpeed);
		else {
			send("G1 F" + slowMoveSpeed);
		}

		this.moveState = s;
	}

// ------------------------------------- SERIAL EVENT ----------------------------------

	@Override
	public void serialEvent(SerialPortEvent event) {

		// PlotterXY removed the event listener to "catch" the event, so call the
		// listener explicitly first.
		this.serialPort.serialEvent(event);

		if (event.getEventType() == SerialPortEvent.RXCHAR) {
			String str = this.serialPort.readString();

			if (str != null)
				this.onStringReceivedFromSerial(str);
		}
	}

	void onStringReceivedFromSerial(String receivedString) {

		if (receivedString.contains("|")) {

			String[] infos = receivedString.split("\\|", 4);
			for (String info : infos) {
				if (info.contains("Pos:")) {

					String[] poss = info.split(":")[1].split(",");
					float x = parseFloat(poss[0]);
					float y = parseFloat(poss[1]);
					float z = parseFloat(poss[2]);

					motorPosition.x = x;
					motorPosition.y = y;
					motorPosition.z = z;
				}
			}
		}

		processReadString(receivedString);

	}

	// ----------------------------------------- COMMANDS

	public void toggleMoveState() {
		if (this.moveState == MOVE_STATE.FAST)
			SetMoveState(MOVE_STATE.PRECISE);
		else if (this.moveState == MOVE_STATE.PRECISE)
			SetMoveState(MOVE_STATE.FAST);
	}

	// clear queued commands and back to origin
	public void clearXYCommands() {
		commands.clear();
		backToOrigin();
		penUp();
	}
	
	public void unsafeClear() {
		commands.clear();
	}

	public void penUp() {
		println("PEN UP");
		send("M5");
		send("G4 1000");
		QueueCommand(new Functor() {
			@Override
			public void execute() {
				pen = PenState.UP;
			}
		}, 500);
	}

	public void penReset() {
		println("PEN RESET");
		send("S800");
		send("G4 1000");
		QueueCommand(new Functor() {
			@Override
			public void execute() {
				pen = PenState.UNKNOWN;
			}
		}, 500);
	}

	public void penDown() {
		println("PEN DOWN");
		send("M3 S950");
		send("G4 1000");
		QueueCommand(new Functor() {
			@Override
			public void execute() {
				pen = PenState.DOWN;
			}
		}, 500);
	}
	
	public void setOrigin(PVector point) {
		setOrigin(point.x,point.y);
	}

	public void setOrigin(float x, float y) {
		send("G92", x, y);
		cursorA.set(x, y);
		cursorD.set(x, y);
	}

	public void setCurrentAsOrigin() {
		setOrigin(0, 0);
	}

	public void backToOrigin() {
		SetMoveState(MOVE_STATE.FAST);
		send("G0", 0, 0);
		InterpolateDisplayCursor(0, 0);
		cursorA.set(0, 0);
	}
	
	public void moveTo(PVector point) {
		moveTo(point.x,point.y);
	}

	public void moveTo(float x, float y) {

		PVector to = new PVector(x, y);

		send("G" + (moveState == MOVE_STATE.FAST ? "0" : "1"), to.x, to.y);
		InterpolateDisplayCursor(to.x, to.y);
		cursorA.set(to);
	}
	
	public void moveRelative(PVector point) {
		moveRelative(point.x,point.y);
	}

	public void moveRelative(float x, float y) {
		moveTo(cursorA.x + x, cursorA.y + y);
	}

	public void InterpolateDisplayCursor(float _x, float _y) {
		CursorValueChange f = new CursorValueChange(this, cursorD, cursorA.copy(), new PVector(_x, _y));
		QueueCommand(f);
	}

	public void send(String cmd, float x, float y, String endCMD) {
		send(cmd + createPositionString(x, y) + " " + endCMD);
	}

	String createPositionString(float x, float y) {
		return " X" + setFloatPrecision(x) + " Y" + setFloatPrecision(-y) + " Z0";
	}

	String formatStringForMessage(double d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}

	float precision = 1000;

	public String setFloatPrecision(float value) {
		float v = round(value * precision) / precision;
		return formatStringForMessage(v);
	}

	public void send(String cmd, float x, float y) {
		send(cmd + createPositionString(x, y));
	}

	public void send(String str) {
		str = str + "\r";
		QueueCommand(new MessageSender(this.serialPort, str.getBytes()));
		QueueCommand(new MessageSender(this.serialPort, null));
		QueueCommand(new MessageSender(this.serialPort, "?".getBytes()));
		QueueCommand(new MessageSender(this.serialPort, null));
	}

}

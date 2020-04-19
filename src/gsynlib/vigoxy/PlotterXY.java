package gsynlib.vigoxy;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import gsynlib.base.GsynlibBase;

import gsynlib.scheduling.*;
import gsynlib.scheduling.StatefulCommand.RUNSTATE;
import gsynlib.utils.GApp;
import gsynlib.vigoxy.commands.*;
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

	public Boolean serialVerbose = false;

	public String helloString = "V4&^CMP*GWFIK5SHA$CPE";

	public MOVE_STATE moveState = MOVE_STATE.UNKNOWN;
	public PenState pen = PenState.UNKNOWN;
	public Boolean initialized = false;

	Serial serialPort;
	int lf = 0x0D;
	String lfstr = "";

	ReentrantLock serialReadLock = new ReentrantLock();
	String incomingSerialMessage = null;

	Scheduler scheduler;

	// QUEUE OF COMMANDS
	ReentrantLock commandsLock = new ReentrantLock();
	ArrayList<StatefulCommand> commands = new ArrayList<StatefulCommand>();

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

	public void addCommand(StatefulCommand f) {
		commands.add(f);
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

		System.gc();

		// set scheduler to run this' schedulExecute function. commands will be
		// processed there independantly from the app's frame rate.
		scheduler.setTask(this, "scheduleExecute");

		String firstRead = this.serialPort.readString();

		if (!GApp.isNullOrEmpty(firstRead))
			this.incomingSerialMessage = firstRead;

		// start scheduler, asking for 15ms period
		scheduler.start(15);

		// Init command for the plotter
		send(helloString, true);
	}

	synchronized protected void scheduleExecute() {
		commandsLock.lock();
		try {
			processCommands();
		} finally {
			commandsLock.unlock();
		}

		serialReadLock.lock();
		try {
			processSerialRead();
		} finally {
			serialReadLock.unlock();
		}
	}

	void processSerialRead() {
		if (GApp.isNullOrEmpty(this.incomingSerialMessage))
			return;

		String[] lns = this.incomingSerialMessage.split("\n");
		for (int i = 0; i < lns.length; i++) {
			String line = lns[i];
			this.readLineFromSerialAsync(line);
		}

		this.incomingSerialMessage = null;
	}

	void processCommands() {

		if (commands.size() > 0) {

			StatefulCommand func = commands.get(0);

			if (func.state == StatefulCommand.RUNSTATE.CREATED) {
				func.start();
			}

			if (func.state == StatefulCommand.RUNSTATE.STARTED) {
				func.update();
			}

			if (func.state == StatefulCommand.RUNSTATE.DONE) {
				func.stop();
			}

			if (func.state == StatefulCommand.RUNSTATE.THRASH) {
				commands.remove(0);
			}
		}

	}

	public void close() {
		this.penUp();
		this.backToOrigin();
		serialPort.stop();
		scheduler.stop();
	}

	public void QueueCommand(StatefulCommand f, float waitSeconds) {
		f.totalTime = waitSeconds / 1000f;
		QueueCommand(f);
	}

	public void QueueCommand(StatefulCommand f) {

		commandsLock.lock();
		try {
			commands.add(f);
		} finally {
			commandsLock.unlock();
		}

	}

	void initResponse() {
		penUp();
		SetMoveState(MOVE_STATE.FAST);

		// $ = help, $G list GCode commands
		// send("$G", true);
	}

	void SetMoveState(MOVE_STATE s) {
		if (s == MOVE_STATE.FAST)
			send("G0 S" + fastMoveSpeed);
		else {
			send("G0 F" + slowMoveSpeed);
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
			serialReadLock.lock();
			try {
				String str = this.serialPort.readString();

				if (!GApp.isNullOrEmpty(str)) {

					if (GApp.isNullOrEmpty(incomingSerialMessage))
						incomingSerialMessage = str;
					else
						incomingSerialMessage += str + "\n";
				}
			} finally {
				serialReadLock.unlock();
			}
		}
	}

	void readLineFromSerialAsync(String receivedString) {

		Boolean isInitMessage = false;
		Boolean isPositionMessage = false;
		Boolean isOk = receivedString.startsWith("ok");
		Boolean isError = receivedString.startsWith("error");

		if (!this.initialized && (receivedString.contains("Vigo") || receivedString.contains("MPos"))) {
			this.initialized = true;
			isInitMessage = true;
			initResponse();
		}

		if (serialVerbose)
			println("RECEIVED STRING: ", receivedString);

		if (receivedString.contains("|")) {

			String[] infos = receivedString.split("\\|", 4);
			for (String info : infos) {
				if (info.contains("Pos:")) {

					String[] poss1 = info.split(":");
					if (poss1.length > 1) {

						String[] poss = poss1[1].split(",");

						float x = 0;
						float y = 0;
						float z = 0;

						if (poss.length > 0)
							x = parseFloat(poss[0]);
						if (poss.length > 1)
							y = parseFloat(poss[1]);
						if (poss.length > 2)
							z = parseFloat(poss[2]);

						motorPosition.x = x;
						motorPosition.y = y;
						motorPosition.z = z;
					}
				}
			}

			isPositionMessage = true;
		}

		Boolean isFeedback = isInitMessage || isPositionMessage || isOk || isError;

		commandsLock.lock();
		try {
			if (commands.size() > 0) {
				StatefulCommand f = commands.get(0);

				if (f instanceof MessageSender) {
					MessageSender ms = (MessageSender) f;
					ms.received(receivedString);
					ms.forceFinalize();
					ms.stop();
					commands.remove(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			commandsLock.unlock();
		}

		if (isError && serialVerbose) {
			println("error " + receivedString);
		}
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
		unsafeClear();
		backToOrigin();
		penUp();
		System.gc();
	}

	public void unsafeClear() {
		commandsLock.lock();
		try {
			commands.clear();
		} finally {
			commandsLock.unlock();
		}

		serialReadLock.lock();
		try {
			this.incomingSerialMessage = null;
		} finally {
			serialReadLock.unlock();
		}

		System.gc();
	}

	int penWaitTimeMS = 500;

	public void penUp() {
		send("M5 S950", true);
		dwell(penWaitTimeMS);
		QueueCommand(new StatefulCommand() {
			@Override
			public void execute() {
				println("PEN UP");
				pen = PenState.UP;
				super.finishCommand();
			}
		}, penWaitTimeMS);
	}

	public void penReset() {
		send("S800", true);
		dwell(penWaitTimeMS);
		QueueCommand(new StatefulCommand() {
			@Override
			public void execute() {
				println("PEN RESET");
				pen = PenState.UNKNOWN;
				super.finishCommand();
			}
		}, penWaitTimeMS);
	}

	public void penDown() {
		send("M3 S950", true);
		dwell(penWaitTimeMS);
		QueueCommand(new StatefulCommand() {
			@Override
			public void execute() {
				println("PEN DOWN");
				pen = PenState.DOWN;
				super.finishCommand();
			}
		}, penWaitTimeMS);
	}

	public void setOrigin(PVector point) {
		setOrigin(point.x, point.y);
	}

	public void setOrigin(float x, float y) {
		sendCommandWithPosition("G92", x, y);
		cursorA.set(x, y);
		cursorD.set(x, y);
	}

	public void setCurrentAsOrigin() {
		setOrigin(0, 0);
	}

	public void backToOrigin() {
		SetMoveState(MOVE_STATE.FAST);
		sendCommandWithPosition("G0", 0, 0);
		InterpolateDisplayCursor(0, 0);
		cursorA.set(0, 0);
	}

	public void moveTo(PVector point) {
		moveTo(point.x, point.y);
	}

	public void moveTo(float x, float y) {

		PVector to = new PVector(x, y);

		sendCommandWithPosition("G" + (moveState == MOVE_STATE.FAST ? "0" : "1"), to.x, to.y);
		InterpolateDisplayCursor(to.x, to.y);
		cursorA.set(to);
	}

	public void moveRelative(PVector point) {
		moveRelative(point.x, point.y);
	}

	public void moveRelative(float x, float y) {
		moveTo(cursorA.x + x, cursorA.y + y);
	}

	public void InterpolateDisplayCursor(float _x, float _y) {
		CursorValueChange f = new CursorValueChange(this, cursorD, cursorA.copy(), new PVector(_x, _y));
		QueueCommand(f);
	}

	public void send(String cmd, float x, float y, String endCMD, Boolean expectAnswer) {
		send(cmd + createPositionString(x, y) + " " + endCMD, expectAnswer);
	}

	public void dwell(float timeInMs) {
		send("G4 P" + setFloatPrecision(((float) timeInMs) / 1000f), true);
	}

	String createPositionString(float x, float y) {
		return " X" + setFloatPrecision(x) + " Y" + setFloatPrecision(-y) + " Z0 ";
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

	public String processSendStringFormat(String str) {
		if (!str.endsWith(" "))
			str = str + " ";
		return str;
	}

	public void sendCommandWithPosition(String cmd, float x, float y) {
		send(cmd + createPositionString(x, y), true);
	}

	public void send(String str) {
		str = processSendStringFormat(str);
		send(str, false);
	}

	public void send(String str, Boolean expectAnswer) {
		str = processSendStringFormat(str);
		QueueCommand(new MessageSender(this.serialPort, expectAnswer, true, null, str.getBytes(), null));
	}

}

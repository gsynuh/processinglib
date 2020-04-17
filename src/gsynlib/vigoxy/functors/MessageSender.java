package gsynlib.vigoxy.functors;

import gsynlib.scheduling.*;
import gsynlib.vigoxy.*;
import processing.core.*;
import processing.serial.*;
import static processing.core.PApplet.*;

public class MessageSender extends StatefulCommand {

	public static final Boolean verbose = false;

	public Boolean expectAnswer = true;

	byte[] emptyMSG = new byte[0];

	byte[] msg = null;
	Serial serial = null;

	public String getMessage() {
		String msgString =  new String(msg);
		msgString = msgString.replace("\n", "\\n");
		msgString = msgString.replace("\r", "\\r");
		return msgString;
	}

	Boolean messageWritten = false;

	public MessageSender(Serial _serial, byte[] _msg, Boolean _waitForAnswer) {
		super();

		this.msg = _msg;
		
		if (this.msg == null)
			this.msg = emptyMSG;

		this.expectAnswer = _waitForAnswer;
		this.serial = _serial;

	}
	
	@Override public void start() {
		super.start();
		
		if (!messageWritten) {
			if (verbose)
				println("MessageSender writing : " + getMessage());

			this.serial.write(msg);
			messageWritten = true;
		}
	}
	
	@Override public void update() {		
		super.updateTime();
		
		if(!this.expectAnswer) {
			finishCommand();
			return;
		}
		
		if (verbose)
			println("Message sender waiting ",this.getMessage(), this.currentTime);
	}

	public void received(String str) {
		if (verbose)
			println("MessageSender received : \n'" + getMessage() + "'", str);
		finishCommand();
	}
}
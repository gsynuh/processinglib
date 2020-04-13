package gsynlib.vigoxy.functors;

import gsynlib.scheduling.*;
import processing.core.*;
import processing.serial.*;
import static processing.core.PApplet.*;

public class MessageSender extends Functor {
	
	public static final Boolean verbose = false;
	
	byte[] emptyMSG = new byte[0];

	byte[] msg = null;
	Serial serial = null;

	public MessageSender(Serial _serial, byte[] _msg) {
		super();
		
		this.msg = _msg;
		if (this.msg == null)
			this.msg = emptyMSG;

		this.serial = _serial;
	}

	public void execute() {
		
		if(verbose)
			println("MessageSender : " + new String(msg));
		
		this.serial.write(msg);
	}
}
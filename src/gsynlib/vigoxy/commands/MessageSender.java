package gsynlib.vigoxy.commands;

import gsynlib.scheduling.*;
import processing.serial.*;
import static processing.core.PApplet.*;

import java.util.ArrayList;

public class MessageSender extends StatefulCommand {

	public static Boolean verbose = false;
	
	public static String newlineString = "\n";
	public static String returnString = "\r";
	public static String askString = "?";

	public Boolean expectAnswer = true;

	byte[] emptyMSG = new byte[0];

	ArrayList<byte[]> msgs = new ArrayList<byte[]>();
	
	Serial serial = null;

	public String getMessage() {
		String msgString = "";
		
		for(int i = 0; i < this.msgs.size(); i++) {
			byte[] b = this.msgs.get(i);
			msgString += new String(b);
		}

		msgString = msgString.replace("\n", "\\n");
		msgString = msgString.replace("\r", "\\r");
		return msgString;
	}

	Boolean messageWritten = false;

	public MessageSender(Serial _serial,Boolean _waitForAnswer, Boolean _askResponse, byte[]... _msgs) {
		super();

		this.msgs.clear();
		for(int i = 0; i < _msgs.length; i++) {
			byte[] msgArg = _msgs[i];
			if(msgArg == null) {
				this.msgs.add(emptyMSG);
			}else {
				this.msgs.add(msgArg);
			}
		}
		
		if (this.msgs.size() == 0)
			this.msgs.add(emptyMSG);
		
		if(_askResponse) {
			this.msgs.add(askString.getBytes());
		}

		this.expectAnswer = _waitForAnswer;
		this.serial = _serial;
		
		if(this.expectAnswer && verbose) {
			println("MessageSender '" + this.getMessage() + "' waiting...");
		}

	}
	
	@Override public void start() {
		super.start();
		
		if (!messageWritten) {
			if (verbose)
				println("MessageSender writing : " + getMessage());

			for(int i = 0; i < this.msgs.size(); i++) {
				byte[] msg = this.msgs.get(i);
				this.serial.write(msg);
				this.serial.write(newlineString);
			}
			
			this.serial.write(returnString.getBytes());
			
			messageWritten = true;
		}
	}
	
	@Override public void update() {		
		super.updateTime();
		
		if(!this.expectAnswer) {
			finishCommand();
			return;
		}
	}

	public void received(String str) {
		if (verbose)
			println("MessageSender '" + getMessage() + "' received :", str);
		finishCommand();
	}
}
package gsynlib.utils;

import gsynlib.base.GsynlibBase;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class GApp {

	public static Boolean verbose = true;

	static PApplet __app;
	public static void set(PApplet _app) {

		if (verbose)
			println("GApp set " + _app);
		
		__app = _app;
		GsynlibBase._app = _app;
	}
	
	public static PApplet get() {
		return __app;
	}
	
	public static Boolean isNullOrEmpty(String str)  {
		return str == null || (str != null && str.isEmpty());
	}
	
	public static final PVector helperPoint = new PVector();
	public static final PVector helperPoint2 = new PVector();
	
	public static int color(int r, int g, int b, int a) {
		r = r & 0xFF;
		g = g & 0xFF;
		b = b & 0xFF;
		a = a & 0xFF;
		return (r << 24) + (g << 16) + (b << 8) + (a);
	}
	
	public static int color(int r, int g, int b) {
		return color(r,g,b,0xFF);
	}
	
	public static int color(int g, int a) {
		return color(g,g,g,a);
	}

	public static String asciiToHex(String asciiStr) {
		char[] chars = asciiStr.toCharArray();
		StringBuilder hex = new StringBuilder();
		for (char ch : chars) {
			hex.append(Integer.toHexString((int) ch));
		}

		return hex.toString();
	}

	public static String hexToAscii(String hexStr) {
		StringBuilder output = new StringBuilder("");

		for (int i = 0; i < hexStr.length(); i += 2) {
			String str = hexStr.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}

		return output.toString();
	}
}
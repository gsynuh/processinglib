package gsynlib.utils;

import gsynlib.base.GsynlibBase;
import processing.core.PApplet;

public class GApp {

	public static Boolean verbose = true;

	public static void set(PApplet _app) {

		if (verbose)
			System.out.println("GApp set " + _app);

		GsynlibBase._app = _app;
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
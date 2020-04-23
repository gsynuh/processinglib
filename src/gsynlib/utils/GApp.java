package gsynlib.utils;

import gsynlib.base.GsynlibBase;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

import java.util.ArrayList;

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
	
	public static float sqrDist(PVector a, PVector b) {
		float dx = a.x - b.x;
		float dy = a.y - b.y;
		return dx*dx + dy*dy;
	}
	
	public static Boolean vectorPoolInitialized = false;
	static int livePVCount = 0;
	static ArrayList<PVector> vectorPool = new ArrayList<PVector>();
	static ArrayList<PVector> liveVec = new ArrayList<PVector>();
	
	static void InitializePool() {
		IncrementPoolSize(128);
		vectorPoolInitialized = true;
	}
	
	static void IncrementPoolSize(int size) {
		for(int i = 0; i < size; i++) {
			vectorPool.add(new PVector());
		}
	}
	
	public static PVector getVector() {
		if(!vectorPoolInitialized) {
			InitializePool();
		}
		
		if(livePVCount >= vectorPool.size()) {
			IncrementPoolSize(32);
		}
		
		PVector vec =  vectorPool.get(livePVCount);
		vec.set(0,0,0);
		
		if(liveVec.add(vec)) {
			livePVCount++;
			vectorPool.remove(vec);
		}
		return vec;
	}
	
	public static void disposeVector(PVector p) {
		if(liveVec.remove(p)) {
			livePVCount--;
			vectorPool.add(p);
		}
	}
	
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
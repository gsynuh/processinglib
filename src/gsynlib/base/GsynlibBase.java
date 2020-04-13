package gsynlib.base;

import processing.core.PApplet;

public class GsynlibBase {
	public static PApplet _app;
	
	public PApplet app() {
		if(_app == null) {
			System.out.println("PApplet not set for GsynlibBase. Make sure you call GApp.setApp(this); at the start of a sketch !");
		}
		
		return _app;
	}
}

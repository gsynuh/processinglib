package gsynlib.base;

import gsynlib.utils.GApp;
import processing.core.PApplet;

public class GsynlibBase {
	public static PApplet _app;
	
	public PApplet g() {
		if(_app == null) {
			_app = GApp.tryGetApp();
			System.out.println("PApplet not set for GsynlibBase. Make sure you call GApp.setApp(this); at the start of a sketch !");
		}
		
		return _app;
	}
}

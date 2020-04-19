package gsynlib.vigoxy;

import gsynlib.base.*;

public class PlotterCommand extends GsynlibBase {
	static int latestCommandId = 0;
	public int commandID = 0;
	public float rand = 0f;
	public PlotterCommand() {
		commandID = latestCommandId;
		latestCommandId++;
		rand = app().random(1f);
	}
}

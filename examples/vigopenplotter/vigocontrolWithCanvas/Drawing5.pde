import gsynlib.utils.*;

public class Drawing5 {
  public Drawing5(PlotterCanvas c) {
    Bounds canvasArea = c.getBounds();
    c.bezierLoop(12, 10, 10, canvasArea.size.x - 20, canvasArea.size.y - 20);
  }
}

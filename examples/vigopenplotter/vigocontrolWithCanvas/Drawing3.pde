import gsynlib.utils.*;
import gsynlib.particles.*;

public class Drawing3 {
  public Drawing3(PlotterCanvas c) {
    Bounds canvasArea = c.getBounds();
    
    c.pushMatrix();
    c.translate(canvasArea.center.x,canvasArea.center.y);
    
    float r1 = 20;
    float r2 = 20;
    float a1 = 0;
    float a2 = 0;
    
    float div1 = 6;
    float div2 = 124;
    
    float numCircles = div1 * div2;
    println("num circles",numCircles);
    
    for(int i = 0; i < numCircles; i++) {
      float x1 = cos(a1)*r1;
      float y1 = sin(a1)*r1;
      
      float x2 = cos(a2)*r2;
      float y2 = sin(a2)*r2;
      
      float x = x1+x2;
      float y = y1+y2;
      
      c.circle(x,y,5);
      
      a1 += TWO_PI/div1;
      a2 += TWO_PI/div2;
    }
    c.popMatrix();

  }
}

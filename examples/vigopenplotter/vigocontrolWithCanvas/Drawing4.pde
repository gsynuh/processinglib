import gsynlib.utils.*;
import gsynlib.particles.*;

public class Drawing4 {
  public Drawing4(PlotterCanvas c) {
    Bounds canvasArea = c.getBounds();
    
    noiseSeed(round(random(9999)));
    c.translate(0,50);
    for (float i = -20; i < 20; i+=1.5) {
      c.pushMatrix();
      c.translate(0,i);
      
      Formula f = new Formula() {
        @Override
          public float f(float x) {
          return noise(x*2,10)*0.2;
        }
      };

      f.bounds.set(canvasArea);
      c.formula(f);

      c.popMatrix();
    }
  }
}

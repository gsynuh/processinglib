import gsynlib.utils.*;
import gsynlib.geom.*;

PoissonSampler poisson;

void setup() {
  size(800,800);
  GApp.set(this);
  poisson = new PoissonSampler();
  poisson.init(10,100,100,width-200,height-200);
  println("numPoints:",poisson.getAllPoints().size());
}

void draw() {
  background(255);
  
  poisson.renderDebug();
}

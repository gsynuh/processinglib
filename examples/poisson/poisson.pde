import gsynlib.utils.*;
import gsynlib.geom.*;

PoissonSampler poisson;

void setup() {
  size(800,800);
  GApp.set(this);
  poisson = new PoissonSampler();
  poisson.init(10,50,50,width-100,height-100);
  println("numPoints:",poisson.getAllPoints().size());
}

void draw() {
  background(255);
  
  poisson.renderDebug();
}

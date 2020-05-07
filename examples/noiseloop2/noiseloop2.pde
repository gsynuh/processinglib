import gsynlib.utils.*;

NoiseLoop noise;

float z = 0;
float numLoops = 20;
float numPoints = 100;

void setup() {
  size(800, 800, FX2D);
  GApp.set(this);

  noise = new NoiseLoop();
}

//On mouse press , change the noise loop perimeter
void mousePressed() {  
  float mX = map(mouseX, 0, width, 1, 5);
  noise.setLoopPerimeter(mX*mX);
}

void drawTube(int id) {
  for (float off = - numLoops/2; off < numLoops/2; off++) {
    beginShape();
    for (float i = 0; i <= numPoints; i++) {
      float t = i/numPoints;

      float n = noise.get(id, t, z + off*0.1);
      float r = height/8 + n*height/12;

      float x = 0;
      float y = off*5;

      float a = t * TWO_PI;
      vertex(cos(a)*r + x, sin(a)*r + y);
    }
    endShape(CLOSE);
  }
}

void draw() {

  background(255);

  noFill();
  stroke(0);

  z = (float)frameCount*0.02f;


  //DRAW CIRCLE REPRESENTING NOISE FROM 0-1
  pushMatrix();
  translate(width/2, height/2);

  pushMatrix();
  translate(-width/4, 0);
  drawTube(0);
  popMatrix();

  pushMatrix();
  translate(width/4, 0);
  drawTube(1);
  popMatrix();

  popMatrix();
}

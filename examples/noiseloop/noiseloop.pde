import gsynlib.utils.*;


NoiseLoop noise;

void setup() {
  size(600, 600);
  GApp.set(this);

  noise = new NoiseLoop();
}

//On mouse press , change the noise loop perimeter
void mousePressed() {
  float mX = map(mouseX,0,width,0.0001,10);
  noise.setLoopPerimeter(mX);
}


void draw() {

  background(255);
  float numPoints = 360;
  
  noStroke();
  fill(64);
  
  //DRAW CIRCLE REPRESENTING NOISE FROM 0-1
  pushMatrix();
  translate(width/2, height/2);
  beginShape();
  for (float i = 0; i <= numPoints; i++) {
    float t = i/numPoints;
    float n = noise.get(t, (float)frameCount*0.02f ); //the second argument is actually moving the noise in 3D
    float r = height/6 + n*height/6;
    
    float a = t * TWO_PI;
    vertex(cos(a)*r, sin(a)*r);
  }
  endShape(CLOSE);
  popMatrix();
  
  
  //DRAW A THE NOISE AS A GRAPH, and scroll it by adding or removing value from the t argument of get(t)
  pushMatrix();
  float m = 0;
  float h = height/10;
  float w = width-m*2;
  translate(width/2 - w/2,height-m);
  
  fill(64);
  rect(0,-h,w,h);
  
  fill(255);
  beginShape();
  vertex(0,0);
  float numSamples = 200;
  float sampleW = w / numSamples;
  for(float i = 0; i < numSamples; i++) {
    
    float t = i/numSamples;   
    
    //move sample time with frameCount
    t -= (float)frameCount*0.005f;
    
    float s = noise.get(t);
    
    vertex(i*sampleW,-s*h);
  }
  vertex(w,0);
  endShape(CLOSE);
  popMatrix();
}

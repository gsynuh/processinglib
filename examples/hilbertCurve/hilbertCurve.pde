import gsynlib.geom.*;

HilbertC2D hilbertCurve;

void setup() {
  size(1024, 1024);
  noSmooth();
  colorMode(HSB, 360, 255, 255);
  background(0);
  init(1);
}

void keyPressed() {
  
  int currentOrder = hilbertCurve.getOrder();
  
  if(keyCode == UP) {
    
    if(currentOrder >= 10)
      return;
      
    currentOrder++;
    init(currentOrder);
    return;
  }else if(keyCode == DOWN) {
    if(currentOrder <= 1)
      return;
    
    currentOrder--;
    init(currentOrder);
    return;
  }
}

void init(int order) {
  
  if(hilbertCurve == null)
    hilbertCurve = new HilbertC2D(order);
  else
    hilbertCurve.setOrder(order);
  
}


void draw() {
  background(0);
  
  pushMatrix();
  
  translate(50,50);
  float w = width-100;  
  float s = w/(hilbertCurve.getSize());
  
  float is = 1/s;
  scale(s,s);
  
  int numPoints = hilbertCurve.getPointCount();

  for (int i = 1; i < numPoints; i++) {
    
    PVector prev = hilbertCurve.getPoint(i-1);
    PVector p = hilbertCurve.getPoint(i);
    
    float h = map(i-1, 0, numPoints, 0, 360)+180;
    stroke(h%360, 255, 255);
    strokeWeight(is*1);
    line(prev.x, prev.y, p.x, p.y);
  }
  
  float t = frameCount*0.0005f % 1.0f;
  PVector p = hilbertCurve.samplePoint(t);
  
  stroke(0,0,255);
  strokeWeight(is*12);
  point(p.x,p.y);
  
  float mX = constrain(map(mouseX,50,50+w,0,1),0,1);
  float mY = constrain(map(mouseY,50,50+w,0,1),0,1);
  float time = hilbertCurve.sampleTime(mX,mY);
  p = hilbertCurve.samplePoint(time);
  
  stroke(0,255,255);
  strokeWeight(is*6);
  point(p.x,p.y);
  
  popMatrix();
  
  fill(255);
  text("order:" + hilbertCurve.getOrder() + " n:" + hilbertCurve.getSize() + " id:"+p.z,50,height-30);
}

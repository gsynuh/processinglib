import gsynlib.utils.LSystem;

LSystem sys;

PVector startDrawPoint = new PVector();
PImage overlayimg;

void setup() {
  size(900, 900,FX2D);

  overlayimg = createImage(width, height, ARGB);
  overlayimg.loadPixels();
  for (int x = 0; x < overlayimg.width; x++) {
    for (int y = 0; y < overlayimg.height; y++) {
      int i = x + y*overlayimg.width;
      float bw = random(1) < 0.5 ? 0 : 1;

      overlayimg.pixels[i] = color(bw<0.5? 64 : 255, random(0, 75));
    }
  }
  overlayimg.updatePixels();

  frameRate(15);

  sys = new LSystem();
  sys.setSeed(round(random(10000)));
  sys.varB = 3;

  initPreset(4);

}

void initPreset(int i) {

  //preset examples at : https://en.wikipedia.org/wiki/L-system

  if (i == 1) {

    //Sierpiński arrowhead curve L-system
    sys.setAlphabet("AB-+");
    sys.setAxiom("A");

    sys.addRule("A", "B-A-B");
    sys.addRule("B", "A+B+A");

    sys.varA = radians(60);

    startDrawPoint.set(width/2, height-50);
  } else if (i == 2) {

    //Dragon curve
    sys.setAlphabet("XYF+-");
    sys.setAxiom("FX");

    sys.addRule("X", "X+YF+");
    sys.addRule("Y", "-FX-Y");

    sys.varA = radians(90);

    startDrawPoint.set(width/2, height/2-50);
  } else if (i == 3) {

    //Test
    sys.setAlphabet("FABX+-[]");
    sys.setAxiom("X");

    sys.addRule("X", "B[+X+A]-[+A[XF]-B]-[-B+X]-F");

    sys.addRule("F", "FFB");
    sys.addRule("F", "FFX");

    sys.addRule("B", "BB");
    sys.addRule("A", "AAB+");

    sys.varA = radians(60);
    sys.varB  = 5;
    startDrawPoint.set(width*0.5, height*0.7);
  } else if (i == 4) {

    //Tree
    sys.setAlphabet("!RF+-[]");
    sys.setAxiom("F");

    sys.addRule("F", "FF+[!+F-RFR-F]-[-!+RFR+F]");

    sys.addRule("!", "F");
    sys.addRule("!", "+[F]", 0.2);
    sys.addRule("!", "-[+F]", 0.8);

    sys.varA = radians(25);
    sys.varB  = 4;
    startDrawPoint.set(width*0.25, height*0.9);
  }else if (i == 5) {

    //Fract
    sys.setAlphabet("F-+[]");
    sys.setAxiom("F-F-F-F");

    sys.addRule("F", "F[F]-F+F[--F]+F-F");

    sys.varA = radians(90);
    sys.varB  = 10;
    startDrawPoint.set(width*0.9, height*0.9);
  } else {

    //Fern
    sys.setAlphabet("XF+-[]");
    sys.setAxiom("X");

    sys.addRule("X", "F+[[X]-X]-F[-FX]+X");
    sys.addRule("F", "FF");

    sys.varA = radians(25);

    startDrawPoint.set(width/2, height-50);
  }
}

void keyPressed() {
  
  if(keyCode == LEFT) {
    int iter = sys.getIteration();
    if(iter > 0) {
      sys.process(iter - 1);
    }
    loop();
    return;
  }
  
  if(keyCode == RIGHT) {
    sys.step();
    loop();
    return;
  }

  if (keyCode == 82) {
    sys.setSeed(round(random(10000)));
    loop();
    return;
  }

  sys.reset();
  loop();
}

void mousePressed() {
  sys.next();
  loop();
}

void draw() {
  blendMode(NORMAL);
  background(255);

  pushMatrix();
  pushStyle();

  //initial drawing conditions
  stroke(64, 127);
  strokeWeight(0.8);
  translate(startDrawPoint.x, startDrawPoint.y);

  int pushedMatrices = 0;
  ArrayList<Character> state = sys.getCurrentState(); // IN PROGRESS STATE
  
  if(state.size() == 0) //IN PROGRESS STATE DOESNT EXIST, OR IS DONE
    state = sys.getState(); //GRAB FULL STATE
    

  for (int i = 0; i < state.size(); i++) {

    char c = state.get(i);

    switch(c) {
    case '1':
    strokeWeight(0.8);
      stroke(64, 127);
      break;
    case '2':
    strokeWeight(1.1);
      stroke(255, 0, 0,127);
      break;
    case '[':
      pushMatrix();
      pushedMatrices++;
      break;
    case ']':
      popMatrix();
      pushedMatrices--;
      break;
    case 'A':
    case 'B':
    case 'F':

      float x = 0;
      float y = -sys.varB;

      line(0, 0, x, y);
      translate(x, y);
      break;
    case '-':
      rotate(-sys.varA);
      break;
    case '+':
      rotate(sys.varA);
      break;
    case 'R':
      float r = (float)sys.rand.nextDouble();
      r = map(r, 0, 1, -sys.varA*0.5, sys.varA*0.5);
      rotate(r);
      break;
    case 'X':
      //NO OP
      break;
    }
  }
  
  if(pushedMatrices > 0)
  for(int i = 0; i < pushedMatrices; i++){
    popMatrix();
  }

  popMatrix();
  popStyle();

  //debug display
  fill(255);
  noStroke();
  rect(0, height-30, width, 30);
  fill(64);
  text("iteration:"+sys.getIteration()+ " step:" + sys.getCurrIndex() + " cmds:" + state.size() + " seed:" + sys.getSeed(), 10, height-12);

  //overlay
  pushMatrix();
  float sX = width / (float)overlayimg.width;
  float sY = height / (float)overlayimg.height;
  blendMode(ADD);
  scale(sX, sY);
  image(overlayimg, 0, 0);
  popMatrix();

  noLoop();
}

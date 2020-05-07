import gsynlib.utils.LSystem;

LSystem sys;

PVector startDrawPoint = new PVector();

void setup() {
  size(800, 800);

  frameRate(10);

  sys = new LSystem();
  sys.setSeed(round(random(10000)));

  initPreset(0);

  //advance straight to iteration 3
  sys.process(3);
}

void initPreset(int i) {

  //preset examples at : https://en.wikipedia.org/wiki/L-system

  if (i == 1) {

    //Sierpiński arrowhead curve L-system
    sys.alphabet = "AB-+";
    sys.axiom = "A";
    sys.addRule("A", "B-A-B");
    sys.addRule("B", "A+B+A");
    sys.varA = radians(60);
    startDrawPoint.set(width/2, height-50);
  } else if (i == 2) {

    //Dragon curve
    sys.alphabet = "XYF+-";
    sys.axiom = "FX";
    sys.addRule("X", "X+YF+");
    sys.addRule("Y", "-FX-Y");
    sys.varA = radians(90);
    startDrawPoint.set(width/2, height/2-50);
  } else {

    //Fern
    sys.alphabet = "XF+-[]";
    sys.axiom = "X";
    sys.addRule("X", "F+[[X]-X]-F[-FX]+X");
    sys.addRule("F", "FF");
    sys.varA = radians(25);
    startDrawPoint.set(width/2, height-50);
  }
}

void keyPressed() {
  sys.reset();
}

void mousePressed() {
  sys.next();
}

void draw() {
  background(255);

  pushMatrix();
  pushStyle();

  //initial drawing conditions
  stroke(64);
  strokeWeight(0.8);
  translate(startDrawPoint.x, startDrawPoint.y);

  float d = 3f;

  String state = sys.getState();
  for (int i = 0; i < state.length(); i++) {

    char c = state.charAt(i);
    switch(c) {
    case '[':
      pushMatrix();
      break;
    case ']':
      popMatrix();
      break;
    case 'A':
    case 'B':
    case 'F':
      line(0, 0, 0, -d);
      translate(0, -d);
      break;
    case '-':
      rotate(-sys.varA);
      break;
    case '+':
      rotate(sys.varA);
      break;
    case 'X':
      //NO OP
      break;
    }
  }

  popMatrix();
  popStyle();

  //debug display
  fill(32);
  text("iteration:"+sys.getIteration()+ " step:" + sys.getCurrIndex(), 10, height-12);
}

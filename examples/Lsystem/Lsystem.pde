import gsynlib.utils.LSystem;

LSystem sys;

PVector startDrawPoint = new PVector();

void setup() {
  size(800, 800);

  frameRate(5);

  sys = new LSystem();
  sys.setSeed(round(random(10000)));
  sys.varB = 3;

  initPreset(3);

  //advance straight to iteration 3
  sys.process(3);
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
    sys.setAlphabet("XFAB+-![]");
    sys.setAxiom("X");

    sys.addRule("X", "A-[X+[!F]-[XB]]++[X]-A");

    sys.addRule("A", "AB+[B]-A");
    sys.addRule("B", "X[-A+[F]-A]");

    sys.addRule("!", "-[B]-X+");

    sys.addRule("F", "-[FA]+");

    sys.varA = radians(120);
    sys.varB  = 2;
    startDrawPoint.set(width*0.75, height*0.75);
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

  ArrayList<Character> state = sys.getState();

  for (int i = 0; i < state.size(); i++) {

    char c = state.get(i);

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
      line(0, 0, 0, -sys.varB);
      translate(0, -sys.varB);
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

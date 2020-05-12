import gsynlib.utils.LSystem;

LSystem sys;

PVector startDrawPoint = new PVector();
PImage overlayimg;

byte[] cov19seq;

color backColor;
color frontColor;

void setup() {
  fullScreen(FX2D, 2);

  cov19seq = loadBytes("sarscov2-2019_NC_045512_300320.txt");

  overlayimg = createImage(width, height, ARGB);
  overlayimg.loadPixels();
  for (int x = 0; x < overlayimg.width; x++) {
    for (int y = 0; y < overlayimg.height; y++) {
      int i = x + y*overlayimg.width;
      float bw = random(1) < 0.5 ? 0 : 1;

      overlayimg.pixels[i] = color(bw<0.5? 0 : 255, random(0, 32));
    }
  }
  overlayimg.updatePixels();

  frameRate(60);

  sys = new LSystem();
  sys.setSeed(round(random(10000)));
  sys.varB = 3;

  sys.setAlphabet("TFGABCDXY+-[]/*!R01234");
  initPreset(9);
}

void initPreset(int i) {

  backColor = color(248);
  frontColor = color(64);
  
  sys.varB  = 2;
  sys.startAngle = 0;
  
  sys.clearRules();
  sys.reset();
  //some preset examples at : https://en.wikipedia.org/wiki/L-system

  if (i == 1) {

    //Sierpiński arrowhead curve L-system
    sys.setAxiom("A");

    sys.addRule("A", "B-A-B");
    sys.addRule("B", "A+B+A");

    sys.varA = PI/3;

    startDrawPoint.set(width/2, height-50);
    
  } else if (i == 2) {

    //Dragon curve
    sys.setAxiom("FX");

    sys.addRule("X", "X+YF+");
    sys.addRule("Y", "-FX-Y");

    sys.varA = PI/2;

    startDrawPoint.set(width/2, height/2-50);
    
  } else if (i == 3) {

    //Dragon curve custom 
    sys.setAxiom("X");

    sys.addRule("X", "[B[+TA-X]CF[X-FB]]TT[-TATCF-TX]+ATX");
    sys.addRule("F", "FF");
    sys.addRule("T", "FF");

    sys.addRule("A", "F+FF[--F]+F");
    sys.addRule("B", "-F");
    sys.addRule("C", "-F[X]");

    sys.varA = PI/3;
    sys.varB  = 1;
    startDrawPoint.set(width*0.5, height*0.6);
    
  } else if (i == 4) {

    //Tree
    sys.setAxiom("F");

    sys.addRule("F", "FF+[!+F-RFR-F]-[-!+RFR+F]");

    sys.addRule("!", "F");
    sys.addRule("!", "+[F]", 0.2);
    sys.addRule("!", "-[+F]", 0.8);

    sys.varA = radians(25);
    sys.varB  = 4;
    startDrawPoint.set(width*0.25, height*0.9);
    
  } else if (i == 5) {

    //Fract
    sys.setAxiom("F-F-F-F");

    sys.addRule("F", "F[F]-F+F[--F]+F-F");

    sys.varA = PI/2;
    sys.varB  = 10;
    startDrawPoint.set(width*0.9, height*0.9);
    
  } else if (i == 6) {

    //SARS COV 2 AS BASE RULES ?

    String cov = new String(cov19seq);
    cov = cov.toUpperCase();

    println("nucleotides:", cov.length());
    sys.setAxiom("0+"+cov);

    sys.addRule("A", "0+F");
    sys.addRule("T", "0-F");
    sys.addRule("C", "1F");
    sys.addRule("G", "2F");

    sys.varA = PI/6;
    sys.varB  = 0.7;
    startDrawPoint.set(width*0.5, height*0.9);
    
  } else if (i == 7) {

    //TREE2 made by gsynuh
    sys.setAxiom("X");

    sys.addRule("X", "FG[GX-F][[+AB][-BFA][A+AB!--BC]![A-A+B+C]]FG");

    sys.addRule("A", "A[-X][+X]B");
    sys.addRule("B", "B[+X][-X]A");
    sys.addRule("F", "FF");

    sys.addRule("G", "F[+X]C");
    sys.addRule("C", "F[-X]G");

    sys.addRule("!", "R[RF]");

    sys.varA = radians(25);
    sys.varB  = 3;
    startDrawPoint.set(width*0.5, height*0.9);
    
  } else if (i == 8) {

    sys.setAxiom("X");

    sys.addRule("X", "[-X][+X]A+B+C-BTB-A-D+A+B-A-D[+X]D-C+B+A");
    
    sys.addRule("A", "+F[C-X]F[-[TFTFTFFF-[X]]+[FT]]");
    sys.addRule("B", "-F[+TXFTF]F");
    sys.addRule("C", "F[T-TFTX]F");
    sys.addRule("D", "F[T+A]F");
   
    sys.addRule("F", "FF");
    
    sys.addRule("T", "TFTFT");
    sys.addRule("T", "FTFT");
 
    sys.varA = radians(60);
    sys.varB  = 2;
    startDrawPoint.set(width*0.5, height*0.75);
  } else if (i == 9) {
    
    sys.setAxiom("X");
    
    sys.addRule("X", "[X][TA]-[TA]-[TA]-[TA]-[TA]-[TA]");
    
    sys.addRule("A","[XB[TT-X]F]");
    
    sys.addRule("B","F+[F-FA]");
    sys.addRule("B","F-[F+FA]");
    
    sys.addRule("F","FF");
    sys.addRule("T","TT");
    
    sys.varA = PI/3;
    sys.varB  = 5;
    startDrawPoint.set(width*0.5, height*0.5);
    
    backColor = color(220);
    frontColor = color(120);
    
  } else if (i == 10) {
    
    sys.setAxiom("X");   
    
    sys.addRule("X", "[[C]F[C]]+F[-C]");
    sys.addRule("C", "/X*");

    sys.varA = PI/3;
    sys.varB  = 200;
    sys.startAngle = -PI/2;
    startDrawPoint.set(width*0.7, height*0.7);
    
  } else {

    //Fern
    sys.setAxiom("X");

    sys.addRule("X", "F+[[X]-X]-F[-FX]+X");
    sys.addRule("F", "FF");

    sys.varA = radians(25);

    startDrawPoint.set(width/2, height-50);
  }
}

Boolean autoplay = false;

void keyPressed() {

  int i = parseInt(key);
  i -= 48;
  if (i > -1 && i < 10) {
    initPreset(i);
    loop();
    return;
  }
  
  if(key == 'A') {
    initPreset(10);
    loop();
    return;
  }

  if (keyCode == LEFT) {
    int iter = sys.getIteration();
    if (iter -1 >= 0) {
      sys.process(iter - 1);
    }
    loop();
    return;
  }

  if (keyCode == RIGHT) {
    autoplay = !autoplay;
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
  
  background(backColor);
  blendMode(NORMAL);

  /*
  pushStyle();
   fill(190); 
   int s = 10;
   textSize(s);
   textLeading(s+1); 
   String code = sys.getStateString();
   text(code,10,10,width-20,height-20);
   popStyle();
   */

  pushMatrix();
  pushStyle();

  int alpha = 200;

  //initial drawing conditions
  stroke(frontColor, alpha);
  strokeWeight(0.8);
  translate(startDrawPoint.x, startDrawPoint.y);
  
  rotate(sys.startAngle);

  float a = sys.varA;
  float b = sys.varB;

  int pushedMatrices = 0;
  ArrayList<Character> state = sys.getState();

  for (int i = 0; i < state.size(); i++) {

    char c = state.get(i);

    float x = 0;
    float y = -b;

    switch(c) {
    case '0':
      strokeWeight(0.8);
      stroke(64, alpha);
      break;
    case '1':
      strokeWeight(1.1);
      stroke(0, 255, 0, alpha);
      break;
    case '2':
      strokeWeight(1.1);
      stroke(255, 0, 0, alpha);
      break;
    case '3':
      strokeWeight(1.1);
      stroke(0, 0, 255);
      break;
    case '4':
      strokeWeight(1.1);
      stroke(255, 0, 255);
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
    case 'C':
    case 'D':
    case 'F':
      line(0, 0, x, y);
    case 'T':
      translate(x, y);
      break;
    case '-':
      rotate(-a);
      break;
    case '+':
      rotate(a);
      break;
    case 'R':
      float r = (float)sys.rand.nextDouble();
      r = map(r, 0, 1, -a*0.25, a*0.25);
      rotate(r);
      break;
    case 'Y':
    case 'X':
      //NO OP
      break;
    case '/':
      b/=2;
      break;
    case '*':
      b*=2;
      break;
    }
  }

  if (pushedMatrices > 0)
    for (int i = 0; i < pushedMatrices; i++) {
      popMatrix();
    }

  popMatrix();
  popStyle();

  //debug display
  fill(backColor);
  noStroke();


  rect(0, height-30, width, 30);

  fill(frontColor);
  text("iteration:"+sys.getIteration()+ " step:" + sys.getCurrIndex() + " cmds:" + state.size() + " seed:" + sys.getSeed(), 10, height-12);

  //overlay
  pushMatrix();
  float sX = width / (float)overlayimg.width;
  float sY = height / (float)overlayimg.height;

  scale(sX, sY);
  blendMode(ADD);
  image(overlayimg, 0, 0);
  //blendMode(NORMAL);
  //image(overlayimg, 0, 0);
  popMatrix();

  noLoop();

  if (autoplay) {
    sys.step();
    loop();
  }
}

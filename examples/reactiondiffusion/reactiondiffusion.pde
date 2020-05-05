import gsynlib.utils.*;
import gsynlib.image.*;

Grid<Float> conv;
Grid<ColorFloat> backstate;
Grid<ColorFloat> state;

PImage renderImage;

int simulationW = 0;
int simulationH = 0;

void setup() {
  size(600, 600);

  colorMode(HSB);

  simulationW = width/3;
  simulationH = height/3;
  renderImage = createImage(simulationW, simulationH, RGB);


  //CONVOLUTION
  conv = new Grid<Float>(3, 3);
  //middle
  conv.set(1, 1, -1f);
  //diags
  conv.set(0, 0, 0.05f);
  conv.set(2, 2, 0.05f);
  conv.set(0, 2, 0.05f);
  conv.set(2, 0, 0.05f);
  //cards
  conv.set(0, 1, 0.2f);
  conv.set(2, 1, 0.2f);
  conv.set(1, 0, 0.2f);
  conv.set(1, 2, 0.2f);

  //grid of old state
  backstate = new Grid<ColorFloat>(simulationW, simulationH);
  backstate.loop = true;

  //grid of current state
  state = new Grid<ColorFloat>(simulationW, simulationH);
  state.loop = true;

  for (int i = 0; i < backstate.size(); i++) {
    ColorFloat c = new ColorFloat();
    backstate.set(i, c);

    c = new ColorFloat();
    state.set(i, c);
  }

  init();
}

void init() {

  for (int i = 0; i < state.size(); i++) {
    ColorFloat c = state.get(i);
    c.a = 1f;
    c.b = 0f;
  }  
  blitState();

  //DRAW SOME SQUARES
  for (int i = 0; i < 200; i++) {
    drawSquareAt(
      round(random(simulationW)), 
      round(random(simulationH)), 
      floor(random(0, 20)));
  }

  blitState();
}

void keyPressed() {
  init();
}

//bring back the current state to back
void blitState() {
  for (int i = 0; i < state.size(); i++) {
    ColorFloat bc = backstate.get(i);
    ColorFloat c = state.get(i);
    bc.set(c);
  }
}

float dA = 1;
float dB = 0.5;
float feed = 0.055;
float k = 0.062;
float dT = 1;

float laplace(Grid<ColorFloat> grid, int val, int startX, int startY) {

  float sum = 0f;

  int mX = 0;
  int mY = 0;

  for (int x = startX - 1; x <= startX + 1; x++) {
    mY = 0;

    for (int y = startY - 1; y <= startY + 1; y++) {
      ColorFloat c = grid.get(x, y);

      float v = val == 0 ? c.a : c.b;
      Float w = conv.get(mX, mY);

      sum += v*w;
      mY++;
    }
    mX++;
  }

  return sum;
}

//update the "state" grid
void updateState() {
  for (int x = 0; x < state.width(); x++) {
    for (int y = 0; y < state.height(); y++) {

      ColorFloat s = backstate.get(x, y);
      ColorFloat c = state.get(x, y);

      c.a = s.a + (
        (dA * laplace(backstate, 0, x, y)) 
        - s.a * s.b * s.b 
        + (feed*(1f-s.a)) )*dT;

      c.b = s.b + (
        (dB * laplace(backstate, 1, x, y)) 
        + s.a * s.b * s.b 
        - (k+feed) * s.b )*dT;

      c.clamp();
    }
  }
}

void drawSquareAt(int mX, int mY, int m) {
  for (int x = -m; x < m; x++) {
    for (int y = -m; y < m; y++) {
      ColorFloat c = backstate.get(mX + x, mY + y);
      ColorFloat s = state.get(mX + x, mY + y);
      c.a += 0.2f;
      c.b += 0.2f;
      c.clamp();
      s.set(c);
    }
  }
}

void draw() {
  background(255);

  for (int i = 0; i < 30; i++) {
    updateState();
    blitState();
  }

  color c1 = color(0);
  color c2 = color(30, 255, 255);

  renderImage.loadPixels();
  for (int x = 0; x< state.width(); x++) {
    for (int y = 0; y<state.height(); y++) {
      int index = x + y*state.width();
      ColorFloat c = state.get(x, y);
      float a = c.a;
      float b = c.b-0.5;
      float t = constrain(a-b, 0, 1);
      t = (cos(t*PI*2)*0.5 + 0.5);

      renderImage.pixels[index] = lerpColor(c1, c2, t);
    }
  }
  renderImage.updatePixels();

  float sX = width/renderImage.width *0.5;
  float sY = height/renderImage.height *0.5;
  scale(sX, sY);
  image(renderImage, 0, 0);
  image(renderImage, width/2 / sX, height/2/sY);
  image(renderImage, width/2 / sX, 0);
  image(renderImage, 0, height/2 /sY);
}

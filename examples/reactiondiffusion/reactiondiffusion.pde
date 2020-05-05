import gsynlib.utils.*;
import gsynlib.image.*;
import gsynlib.geom.*;

Grid<Float> conv;
Grid<ColorFloat> backstate;
Grid<ColorFloat> state;

PImage renderImage;

int simulationW = 0;
int simulationH = 0;

float divisions = 3;
float scale = 0.5;

float dA = 1;
float dB = 0.48;
float feed = 0.056;
float k = 0.062;
float dT = 1;

Boolean invertColors = false;

void setup() {
  size(800, 800);
  GApp.set(this);

  simulationW = floor(width*scale);
  simulationH = floor(height*scale);
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

void mousePressed() {
  invertColors = !invertColors;
}

void init() {

  for (int i = 0; i < state.size(); i++) {
    ColorFloat c = state.get(i);
    c.a = random(0.6,1);
    c.b = random(0,0.03);
  }  
  blitState();

  noiseSeed(round(random(99999)));
  PoissonSampler poisson = new PoissonSampler();
  float minDist = (simulationW<simulationH? simulationW : simulationH) / 10;
  poisson.init(minDist, -100, -100, simulationW+100, simulationH+100);

  int m = floor(minDist/2);
  for (PVector p : poisson.getPoints()) {
    int px = floor(p.x);
    int py = floor(p.y);
    if (px < 0 || px > simulationW) continue;
    if (py < 0 || py > simulationH) continue;

    for (int x = px -m; x < px+m; x++) {
      for (int y = py -m; y < py+m; y++) {
        ColorFloat c = state.get(x, y);
        if(random(1) > 0.9) {
          c.a += random(0.5,0.4);
          c.b += random(0.6,0.5);
        }
        
        c.clamp();
      }
    }
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

float contrast(float value) {
  float prec = 4;
  value = value * prec - prec/2;
  value = constrain(value, 0, 1);
  return value;
}

void draw() {
  background(0);

  for (int i = 0; i < 25; i++) {
    updateState();
    blitState();
  }

  color c1 = color(0);
  color c2 = color(255);

  renderImage.loadPixels();
  for (int x = 0; x< state.width(); x++) {
    for (int y = 0; y<state.height(); y++) {
      int index = x + y*state.width();
      ColorFloat c = state.get(x, y);
      float a = c.a;
      float b = c.b;
      float t = abs((invertColors ? 1 : 0) - contrast(a-b));

      renderImage.pixels[index] = color(t*255);
    }
  }
  renderImage.updatePixels();

  float sX = width/(float)renderImage.width /divisions;
  float sY = height/(float)renderImage.height /divisions;
  float w = (float)renderImage.width;
  float h = (float)renderImage.height;

  scale(sX, sY);

  for (int x = 0; x <divisions; x++) {
    for (int y = 0; y <divisions; y++) {
      image(renderImage, x*w, y*h);
    }
  }
}

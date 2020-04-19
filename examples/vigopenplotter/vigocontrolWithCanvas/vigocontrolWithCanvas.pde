/*
*
 *  Wait for the red rectangle to be green (received OK from plotter)
 *  And use the arrow keys to move relatively (using gcode's G0)
 *  numpad's 8 and 2 keys should change the pen's state.
 *
 */

import gsynlib.geom.*;
import gsynlib.utils.*;
import gsynlib.scheduling.*;
import gsynlib.vigoxy.*;
import gsynlib.vigoxy.commands.*;

import processing.serial.*;

import controlP5.*;

float debugMoveSpeed = 10;
float maxLengthToDraw = 10;
PlotterXY plotter;
PlotterCanvas canvas;

ControlP5 cp5;

//TODO REEVALUER VITESSE DE PRINT SUR DIAGONALES
//TODO DITHERING IMAGE

PImage catImage;

void setup() {
  size(800, 800);

  catImage = loadImage("cat.png");
  catImage.resize(16, 16);

  GApp.set(this);

  String[] serials = Serial.list();
  String portName = serials[serials.length - 1];

  plotter = new PlotterXY(portName);

  //plotter.serialVerbose = true;
  //MessageSender.verbose = true;

  plotter.open();

  canvas = new PlotterCanvas(plotter);
  maxLengthToDraw = canvas.maxLengthToDraw;

  cp5 = new ControlP5(this);
  cp5setup(cp5);
}


//prepareXY is a function automatically called by PlotterCanvas
void prepareXY(PlotterCanvas c) {

  Bounds canvasArea;

  c.displayPenSize = 2;
  c.backgroundColor = color(255, 255, 255, 0);
  c.canvasColor = color(255);

  c.setCanvasBounds(canvasArea = new Bounds(0, 0, 100, 100));
  c.setDrawBounds(new Bounds(0, 150, width, height-255));

  PVector cbr = canvasArea.getBottomRight();
  PVector center = canvasArea.getCenter();

  //c.rect(0, 0, 10, 10);
  //c.point(5, 5);

  c.pushMatrix();
  c.translate(center.x, center.y -25);
  c.rotate(PI/4);
  for (int i = 0; i < 20; i++) {
    c.rotate(0.008f * i);
    c.scale(1.1f);
    c.rect(-2.5, -2.5, 5, 5);
  }
  c.popMatrix();



  c.pushMatrix();
  c.translate(center.x - 25, center.y +25);
  c.rotate(-PI);
  for (int i = 0; i < 30; i++) {
    float a = i * PI/12;
    float x = cos(a) * 5;
    float y = sin(a) * 10;
    c.circle(x, y, 10);
  }
  c.popMatrix();

  c.pushMatrix();
  c.translate(center.x + 25, center.y +25);
  c.rotate(-PI);
  float rad = 10;
  float numCircles = 24;
  float ang = numCircles/TWO_PI;
  for (int i = 0; i < numCircles; i++) {
    float a = i * ang;
    float x = cos(a) * rad;
    float y = sin(a) * rad;
    c.circle(x, y, rad);
  }

  rad = 4;
  for (int i = 0; i < 3; i++) {
    rad += 2;
    c.circle(0, 0, rad);
  }
  c.popMatrix();

  /*
   c.pushMatrix();
   c.translate(90, 35);
   c.scale(0.25);
   c.rotate(PI/2);
   
   c.image(catImage, 20, 20, 30, 30);
   
   for (int i = 0; i < 4; i++) {
   PVector p1 = c.getRandomPointOnCanvas();
   float rad = random(5, 20);
   p1.x = constrain(p1.x, canvasArea.position.x + rad, canvasArea.position.x + canvasArea.size.x - rad);
   p1.y = constrain(p1.y, canvasArea.position.y + rad, canvasArea.position.y + canvasArea.size.y - rad);
   c.circle(p1.x, p1.y, rad);
   }
   
   
   c.beginShape();
   for (int i = 0; i < 4; i++) {
   PVector p1 = c.getRandomPointOnCanvas();
   c.vertex(p1.x, p1.y);
   }
   c.endShape(true);
   
   for (int i = 0; i < 2; i++) {
   PVector p1 = c.getRandomPointOnCanvas();
   PVector p2 = c.getRandomPointOnCanvas();
   c.line(p1.x, p1.y, p2.x, p2.y);
   }
   
   
   c.bezierLoop(2, 10, 10, canvasArea.size.x - 20, canvasArea.size.y - 20);
   c.popMatrix();*/

  //c.rect(cbr.x - 10, cbr.y - 10, 10, 10);
  //c.point(cbr.x - 5, cbr.y - 5);
}

void draw() {
  background(220);
  noStroke();

  canvas.maxLengthToDraw = maxLengthToDraw;

  if (!plotter.initialized || plotter.getCommandCount() >0) //these are the initialization and "busy" combined conditions.
  {
    fill(255, 0, 0);
  } else {
    fill(0, 255, 0);
  }

  float signalSize = 50;
  rect(width - signalSize, 0, signalSize, signalSize);

  PVector cursorA = plotter.getCursor();
  PVector cursorD = plotter.getDisplayCursor();
  PVector mot = plotter.getMotorPos();

  fill(0);
  text("CUR A X " + cursorA.x + " Y " + cursorA.y, 20, 18);
  text("CUR D " + cursorD.x + " Y " + cursorD.y, 20, 31);
  text("MOT X " + mot.x + " Y " + mot.y, 20, 45);
  if (!plotter.initialized) text("CONTACTING PLOTTER....\nPLEASE WAIT UNTIL THE RECTANGLE GOES GREEN !", 20, height - 32);
}

void keyReleased() {
  isKeyDown = false;
}

Boolean isKeyDown = false;
void keyPressed() {

  //Don't accept key presses if plotter is not ready.
  if (!plotter.initialized)return;
  //prevent OS based consecutive key presses when key is held down.
  if (isKeyDown)return;

  isKeyDown = true;

  String k = key + "";
  k = k.toLowerCase();

  if (!k.isEmpty())
    switch(keyCode) {
    case LEFT : 
      plotter.moveRelative(-debugMoveSpeed, 0);
      break;
    case RIGHT : 
      plotter.moveRelative(debugMoveSpeed, 0);
      break;
    case UP:  
      plotter.moveRelative(0, -debugMoveSpeed);
      break;
    case DOWN: 
      plotter.moveRelative(0, debugMoveSpeed);
      break;

    case 32:
      plotter.toggleMoveState();
      println(plotter.moveState);
      break;

    case 98: 
      plotter.penDown();
      break;
    case 104: 
      plotter.penUp();
      break;
    }
}

void stop() {
  plotter.close();
}

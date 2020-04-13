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

import processing.serial.*;

float debugMoveSpeed = 10;
PlotterXY plotter;
PlotterCanvas canvas;

void setup() {
  size(800, 800);
  
  GApp.set(this);

  String[] serials = Serial.list();
  String portName = serials[serials.length - 1];
    
  plotter = new PlotterXY(portName);
  plotter.open();
  
  canvas = new PlotterCanvas(plotter);
}

void prepareXY(PlotterCanvas c) {
  println("Prepare function for canvas");
  
  Bounds canvasArea;
  
  c.setCanvasBounds(canvasArea = new Bounds(0,0,400,400));
  c.setDrawBounds(new Bounds(0,100,width,height-100));
  
  for(int i = 0; i < 20; i++) {
    PVector p1 = c.getRandomPointOnCanvas();
    c.point(p1.x,p1.y);
  }
  
  for(int i = 0; i < 2; i++) {
    PVector p1 = c.getRandomPointOnCanvas();
    PVector p2 = c.getRandomPointOnCanvas();
    c.line(p1.x,p1.y,p2.x,p2.y);
  }
  
  for(int i = 0; i < 2; i++) {
    PVector p1 = c.getRandomPointOnCanvas();
    c.rect(p1.x,p1.y,random(5,40),random(5,40));
  }
  
  for(int i = 0; i < 2; i++) {
    PVector p1 = c.getRandomPointOnCanvas();
    c.circle(p1.x,p1.y,random(5,40));
  }
  
  c.beginShape();
  for(int i = 0; i < 10; i++) {
    PVector p1 = c.getRandomPointOnCanvas();
    c.vertex(p1.x,p1.y);
  }
  c.endShape(true);
  
  c.bezierLoop((int)random(2,12),10,10,canvasArea.size.x - 20,canvasArea.size.y - 20);
}

void mousePressed() {
  canvas.prepare();
  canvas.bake();
}

void draw() {
  background(255);
  noStroke();
  
  
  if (!plotter.initialized || plotter.getCommandCount() >0) //these are the initialization and "busy" combined conditions.
  {
    fill(255, 0, 0);
  } else {
    fill(0, 255, 0);
  }
  rect(0,0,width/2,height/2);
  
  PVector cursor = plotter.getCursor();
  PVector mot = plotter.getMotorPos();

  fill(0);
  text("CUR X " + cursor.x + " Y " + cursor.y, 20, 32);
  text("MOT X " + mot.x + " Y " + mot.y, 20, 45);
  if(!plotter.initialized) text("CONTACTING PLOTTER....\nPLEASE WAIT UNTIL THE RECTANGLE GOES GREEN !", 20, height - 32);


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

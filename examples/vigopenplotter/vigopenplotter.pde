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

void setup() {
  size(800, 800);
  
  GApp.set(this);

  String[] serials = Serial.list();
  String portName = serials[serials.length - 1];
    
  plotter = new PlotterXY(portName);
  
  plotter.open();
}

void draw() {
  background(255);
  
   if (!plotter.initialized || plotter.getCommandCount() >0)
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
}

void keyReleased() {
  isKeyDown = false;
}

Boolean isKeyDown = false;
void keyPressed() {

  if (!plotter.initialized)return;
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

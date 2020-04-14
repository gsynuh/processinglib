color canvasColor1 = color(32);

void cp5setup(ControlP5 c) {
    cp5.addSlider("debugMoveSpeed")
       .setPosition(10, 60)
       .setSize(210, 20)
       .setRange(1, 100)
       .setValue(10)
       .setColorCaptionLabel(canvasColor1)
       .setLabel("move speed");
       
           cp5.addSlider("maxLengthToDraw")
       .setPosition(10, height - 70)
       .setSize(210, 20)
       .setRange(5, 40)
       .setValue(maxLengthToDraw)
       .setColorCaptionLabel(canvasColor1)
       .setLabel("max mine distance");
       
       cp5.addButton("penUp")
     .setPosition(250,10)
     .setSize(100,30)
     .setLabel("Pen up");
     
     cp5.addButton("penDown")
     .setPosition(360,10)
     .setSize(100,30)
     .setLabel("Pen Down");
       
     cp5.addButton("penReset")
     .setPosition(470,10)
     .setSize(100,30)
     .setLabel("Pen Reset");
     
     cp5.addButton("SetOrigin")
     .setPosition(10,90)
     .setSize(100,30)
     .setLabel("Set origin");
     
     cp5.addButton("BackToOrigin")
     .setPosition(120,90)
     .setSize(100,30)
     .setLabel("Back 2 Origin");
     
     cp5.addButton("SetDrawingBottomRight")
     .setPosition(10,height - 40)
     .setSize(100,30)
     .setLabel("BOTTOM RIGHT POS");
     
     cp5.addButton("RegenerateDrawing")
     .setPosition(120,height - 40)
     .setSize(100,30)
     .setLabel("REDRAW");
     
     cp5.addButton("RebuildDrawing")
     .setPosition(230,height - 40)
     .setSize(100,30)
     .setLabel("REBUILD");
     
     cp5.addButton("DoDrawing")
     .setPosition(340,height - 40)
     .setSize(100,30)
     .setLabel("PRINT");
     
     cp5.addButton("DoDrawingFake")
     .setPosition(450,height - 40)
     .setSize(100,30)
     .setLabel("TEST PRINT");
     
     cp5.addButton("ChangeDebugLine")
     .setPosition(560,height - 40)
     .setSize(100,30)
     .setLabel("TOGGLE DLINES")
     ;
     
     cp5.addButton("ClearXYCommands")
     .setPosition(width - 110,60)
     .setSize(100,30)
     .setLabel("CLEAR CMDS")
     ; 
}


//---- FUNCTIONS

void ClearXYCommands() {
  plotter.clearXYCommands();
}

void BackToOrigin() {
  plotter.backToOrigin();
}

void SetOrigin() {
  plotter.setCurrentAsOrigin();
}

void penUp() {
 plotter.penUp();
}

void penDown() {
   plotter.penDown();
}

void penReset() {
  plotter.penReset();
}

void ChangeDebugLine() {
  canvas.debugLinesDI = !canvas.debugLinesDI;
  canvas.bake();
}

void DoDrawingFake() {
  plotter.backToOrigin();
  plotter.penUp();
  println("FAKE DRAWING NOT IMPLEMENTED");
  plotter.penUp();
  plotter.backToOrigin();
}

void DoDrawing(){
  plotter.backToOrigin();
  plotter.penUp();
  println("DRAWING NOT IMPLEMENTED");
  plotter.penUp();
  plotter.backToOrigin();
}

void SetDrawingBottomRight() {
   PVector p = plotter.getCursor();
   
   canvas.setCanvasBounds(new Bounds(0,0,p.x,p.y));
   canvas.bake();
}

void RebuildDrawing() {
  canvas.bake();
}

void RegenerateDrawing() {
  canvas.prepare();
  canvas.bake();
}

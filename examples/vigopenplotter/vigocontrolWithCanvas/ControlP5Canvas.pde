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
       .setRange(10, 200)
       .setValue(maxLengthToDraw)
       .setColorCaptionLabel(canvasColor1)
       .setLabel("max line distance");
       
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
     
     cp5.addButton("FakeDrawBounds")
     .setPosition(670,height - 40)
     .setSize(100,30)
     .setLabel("SHOW BOUNDS")
     ;
     
     cp5.addButton("ClearXYCommands")
     .setPosition(width - 220,60)
     .setSize(100,30)
     .setLabel("CLEAR CMDS")
     ; 
     
     cp5.addButton("ExitApp")
     .setPosition(width - 110,60)
     .setSize(100,30)
     .setLabel("EXIT")
     ; 
}


//---- FUNCTIONS

void ExitApp() {
  plotter.clearXYCommands();
  
  
  class ExitAppF extends StatefulCommand {
    @Override
    public void execute() {
      super.execute();
      GApp.get().exit();  
    }
  }
  
  plotter.addCommand(new ExitAppF());
}

void ClearXYCommands() {
  plotter.clearXYCommands();
}

void BackToOrigin() {
  plotter.backToOrigin();
}

void SetOrigin() {
  plotter.setCurrentAsOrigin();
}

void FakeDrawBounds() {
  canvas.showBounds();
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
  canvas.testPrint();
}

void DoDrawing(){
  canvas.print();
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

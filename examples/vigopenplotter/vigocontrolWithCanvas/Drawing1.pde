public class Drawing1 {
  public Drawing1(PlotterCanvas c) {

    Bounds canvasArea = c.getBounds();
    
    //c.rect(0, 0, 10, 10);
    //c.point(5, 5);

    c.pushMatrix();
    c.translate(canvasArea.center.x, canvasArea.center.y -25);
    c.rotate(PI/4);
    for (int i = 0; i < 20; i++) {
      c.rotate(0.008f * i);
      c.scale(1.1f);
      c.rect(-2.5, -2.5, 5, 5);
    }
    c.popMatrix();



    c.pushMatrix();
    c.translate(canvasArea.center.x - 25, canvasArea.center.y +25);
    c.rotate(-PI);
    for (int i = 0; i < 30; i++) {
      float a = i * PI/12;
      float x = cos(a) * 5;
      float y = sin(a) * 10;
      c.circle(x, y, 10);
    }
    c.popMatrix();

    c.pushMatrix();
    c.translate(canvasArea.center.x + 25, canvasArea.center.y +25);
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
}

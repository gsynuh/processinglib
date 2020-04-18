import gsynlib.image.*;

PImage cat;
Dithering ditheringFilter;

void setup() {
  size(1024, 512);
  
  noSmooth();
    
  cat = loadImage("cat.png");

  ditheringFilter = new Dithering();
  init();
}

void init() {
  ditheringFilter.luminance = !ditheringFilter.luminance;

  if (ditheringFilter.luminance) {
    ditheringFilter.quantizeFactor = 1;
  } else {
    ditheringFilter.quantizeFactor = 3;
  }

  ditheringFilter.CreateFilter(cat);

  loop();
}

void mousePressed() {
  init();
}

void DrawImage() {
  strokeWeight(1.0);
  for (int y = 0; y < ditheringFilter.height; y++) {
    for (int x = 0; x < ditheringFilter.width; x++) {
      ColorFloat c = ditheringFilter.getColor(x, y);
      color col = ColorFloat.toInt32(c);
      
      float b = brightness(col);
      
      if(b >= 255)
        continue;
      
      stroke(col);
      point(x, y);
    }
  }
}

void draw() {
  background(255);
  image(cat, 0, 0);
  translate(512, 0);
  DrawImage();
  noLoop();
}

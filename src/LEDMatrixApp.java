/*
	 The hookedup LED Controller / Matrix / Emulator / Canvas demo.
	 
	 Copywrite 2011, hookedup, inc. 
	 All rights reserved.
 */

//===================== LED CONTROLLER AND MATRIX CODE

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.serial.Serial;

public class LEDMatrixApp extends PApplet {


	int MATRIX_COLS = 40;
	int MATRIX_ROWS = 25;
	int MATRIX_CELL_W = 22;
	int MATRIX_CELL_H = 22;
	int MATRIX_CELL_SPACING = 0;
	int MATRIX_EMULATOR_DELAY = 74;
	int CONTROLLER_PORT_SLOT = 10; //or something like -> 2;

	


	// App Options
	int clickSpread = 1;
	//== Extra windows used to allow display of graphics while in main loop
	ExtraWindow ledCanvas;

	//== The LED Controller sends colors - 1 bytes per color 0-255 range
	// Using a colorScale that matches makes a 1 to 1 mapping so the color data can be sent straight away
	int colorScale = 255;


	//== Various elements pulled in from demos for quick testing of API
	PFont fontA;
	float currentcolor;
	int circSize = 5;


	//== Main window elements used to biuld the super high tech front end
	RectButton rect1, rect2;
	boolean locked = false;

	//== The emulator - abstracted logic for drawing matrix content.  Output can go to any context / window.
	MatrixUI ui;


	//== Serial port used for the transfer
	Serial myPort;

	//== The LED Controller
	LEDController ctlLED = new LEDController(1000);

	//== The LED Matrix
	LEDMatrix matrix = new LEDMatrix(MATRIX_COLS, MATRIX_ROWS);
	
	
	
	public void setup()
	{
	  //-- There is a monster under the bed
	  bot = loadShape("bot1.svg");

	  //-- Font - really a simple demo - better text / font stuff around I think
	  fontA = loadFont("Ziggurat-HTF-Black-32.vlw");
	  textFont(fontA, 24);

	  //-- Used for trailer demo
	  pg = createGraphics(80, 80, P2D);

	  //-- Set color mode to match the scale used by the LED controller - 256 steps per color - 1 byte
	  colorMode(RGB, 255);
	  //-- Update this window and call loadFromCanvas() to send to matrix
	  ledCanvas = new ExtraWindow(this, "Output Canvas", 0, ((MATRIX_CELL_H + MATRIX_CELL_SPACING) * MATRIX_ROWS), (MATRIX_COLS), (MATRIX_ROWS));
	  ledCanvas.setUndecorated(true);
	  //-- Create a new matrix ui and attach it to the matrix
	  ui = new MatrixUI(0, 0, MATRIX_COLS, MATRIX_ROWS, MATRIX_CELL_W, MATRIX_CELL_H, MATRIX_CELL_SPACING, this);
	  //-- Using setUI enables emulation mode
	  //- to run without the emulator - comment out the line below
	  matrix.setUI(ui);  

	  //--- LED Controller setup.
	  //  For emulation mode - I set the value of CONTROLLER_PORT_SLOT to 10 assuming you don't have that many comm ports.
	  // this will assure you are always in emulation mode only with no action needed.
	  // when you get the controller - set CONTROLLER_PORT_SLOT to the position it is in the serial list (Serial.list()).  
	  // Note to make this smarter - get the serial port number which should be static and find it in the list by description and then use that number
	  if (Serial.list().length > CONTROLLER_PORT_SLOT ) {
	    //  myPort = new Serial(this, Serial.list()[CONTROLLER_PORT_SLOT], 115200);
	    //  ctlLED.begin(myPort);
	    //  matrix.setController(ctlLED);
	  }
	  //--- LED Controller setup END


	  //--- Main application
	  size(400, 400);
	  smooth();
	  frameRate(42); 


	  float baseColor = color(102);

	  // Define and create circle button
	  float buttoncolor = color(204);
	  float highlight = color(153);
	  ellipseMode(CENTER);
	  currentcolor = baseColor;

	  // Define and create rectangle button
	  buttoncolor = color(102);
	  highlight = color(51); 
	  rect1 = new RectButton(20, 260, 100, buttoncolor, highlight);

	  // Define and create rectangle button
	  buttoncolor = color(51);
	  highlight = color(0); 
	  rect2 = new RectButton(220, 260, 100, buttoncolor, highlight);
	  background(255);
	}

	public void draw()
	{
	  stroke(255);
	  update(mouseX, mouseY);
	  rect1.display();
	  rect2.display();

	  //--- Turn on below to see trailer demo.  Move mouse over the top left area of the main form (about 20 pixels in) and see it update on the matrix using draw as timer  
	  //demoDraw();
	}

	void update(int x, int y)
	{
	  if (locked == false) {
	    rect1.update();
	    rect2.update();
	  } 
	  else {
	    locked = false;
	  }

	  if (mousePressed) {
	    if (rect1.pressed()) {
	      currentcolor = rect1.basecolor;
	      runDemo1();
	    } 
	    else if (rect2.pressed()) {
	      currentcolor = rect2.basecolor;
	      runDemo2();
	    } 
	    else {
	      //--- We are clicking on screen - not in a button so send that area of the main screen to the matrix
	      colorMode(RGB, 255);
	      for (int iH = 0 ; iH < matrix.rows ; iH++) {
	        for (int iW = 0 ; iW < matrix.cols ; iW++) {
	          float cp = get( (iW*clickSpread)+mouseX, (iH*clickSpread)+mouseY);    
	          print("\n"+ red((int) cp));
	          print(" - "+ green((int) cp));
	          print(" - "+ blue((int) cp));
	          /*float r2 = cp >> 16 & 0xFF;
	          print("\n"+ r2);
	          float g2 = cp >> 8 & 0xFF;
	          print(" - "+ g2);*/
	          matrix.setRGB(iW, iH, (int)(red((int) cp)), (int)(green((int) cp)), (int)(blue((int) cp)));
	        }
	      }
	      matrix.refresh();
	    }
	  }
	}
	
	///////ANIMATION CODE

	//== Shows the entire matrix flashing on and off .. with 1000 LEDS - use some sunglasses B-)
	void strobeOnOff(int theTimes) {
	  for (int i = 0 ; i < theTimes ; i++) {
	    matrix.allTo(0, 0, 0);
	    matrix.refresh();
	    matrix.allTo(255, 255, 255);
	    matrix.refresh();
	  }
	}  

	//== Some vector test patters.  
	//*** NOTE: Drawline and box may be removed from the matrix class.  If you use it let me know and I won't.
	void sweepWidth() {
	  for (int j = 0 ; j < matrix.cols ; j++) {
	    matrix.allOff();
	    matrix.drawLine(j, 0, j, matrix.rows - 1, 255, 255, 255);
	    matrix.refresh();
	  }
	}

	//*** NOTE: Drawline and box slated for removal.
	void sweepHeight() {
	  for (int j = 0 ; j < matrix.rows ; j++) {
	    matrix.allOff();
	    matrix.drawLine(0, j, matrix.cols - 1, j, 255, 255, 255);
	    matrix.refresh();
	  }
	}
	//*** NOTE: Drawline and box slated for removal.
	void vectorDemo() {
	  sweepHeight();
	  for (int j = 0 ; j < 3 ; j++) {

	    for (int i = 0 ; i < MATRIX_COLS / 2 ; i++) {
	      matrix.allOff();
	      matrix.drawBox(i, i, matrix.cols - (i+1), matrix.rows - (i+1), 255, 0, 0);
	      matrix.refresh();
	    }
	    for (int i = MATRIX_COLS / 2 ; i > 0 ; i--) {
	      matrix.allOff();
	      matrix.drawBox(i, i, matrix.cols - (i+1), matrix.rows - (i+1), 255, 255, 0);
	      matrix.refresh();
	    }
	  }
	  sweepWidth();
	}

	//--- This draws a hue based pallet to the main window .. twice.  Very odd but if not it would show up lighter.  Not sure the root cause of this.
	// I find the same issue with get of pixel.  The RGB color value returned are not zero when they should be - like the color is saturated.
	// remove one of the drawHue() calls below to see what I mean when you have the live matrix or by exporting / reviewing color output numbers

	//--- To use - click the mouse and the matrix updates using a box starting at the mouse coordinates as 0,0
	//* any graphic method can be used to update the main window and the mouse click will send to the matrix
	void hueDemo() {
	  drawHue();
	  drawHue();
	}

	int cImage = 0;
	//--- This demo shows the process or loading and images ans scrolling it
	//--- This can just run a loop and not have to coordinator with draw in any way.
	//--- The loadFromCanvas() call loads the "screen shot" from the canvas window to the matrix 
	void scrollGraphicDemo() {
	  PImage myImage;
	  if ( cImage == 0 ) {
	    cImage = 1;
	    myImage = loadImage("test001.jpg");
	  } 
	  else {
	    cImage = 0;
	    myImage = loadImage("test002.jpg");
	  }
	  for ( int i = 0 ; i > 0-myImage.width  ; i--) {
	    ledCanvas.image(myImage, i, 0);
	    loadFromCanvas();
	  }
	}

	//--- This demo shows the process of loading text and scrolling it across the matrix
	void scrollWordDemo() {
	  ledCanvas.fill(0);
	  for ( int i = MATRIX_COLS ; i > 0-MATRIX_COLS-30  ; i--) {
	    ledCanvas.background(102);
	    ledCanvas.text("Hello", i, 9);
	    loadFromCanvas();
	  }
	}

	void aliDemo() {

	  ledCanvas.fill(133, 255, 255);
	  ledCanvas.noStroke(); 
	  ledCanvas.rect(0,0,matrix.cols, matrix.rows);
	  //   ledCanvas.fill(255, 0,0);
	  //   ledCanvas.ellipse(round(matrix.cols*.5),round(matrix.rows*.5),20,20);
	  ledCanvas.stroke(255);
	  ledCanvas.fill(255);
	  ledCanvas.textFont(fontA, 12);
	  ledCanvas.text("hi!", 0, 20);
	  loadFromCanvas();
	  delay(500);
	  ledCanvas.fill(133, 255, 255);
	  ledCanvas.noStroke(); 
	  ledCanvas.rect(0,0,matrix.cols, matrix.rows);
	  ledCanvas.stroke(255);
	  ledCanvas.fill(255);
	  ledCanvas.text("how's", 0, 20);
	  loadFromCanvas();
	  delay(500);
	  ledCanvas.fill(133, 255, 255);
	  ledCanvas.noStroke(); 
	  ledCanvas.rect(0,0,matrix.cols, matrix.rows);
	  ledCanvas.stroke(255);
	  ledCanvas.fill(255);
	  ledCanvas.text("it", 0, 20);
	  loadFromCanvas();
	  delay(500);
	  ledCanvas.fill(133, 255, 255);
	  ledCanvas.noStroke(); 
	  ledCanvas.rect(0,0,matrix.cols, matrix.rows);
	  ledCanvas.stroke(255);
	  ledCanvas.fill(255);
	  ledCanvas.text("going?", 0, 20);
	  loadFromCanvas();

	  loadFromCanvas();
	}

	void demoDraw() {
	  //--- Add a call to demoDraw() in draw() to see "trails" on the main window update.  

	  // To move this effect to another window - a subclass is needed most likely ..
	  //   due to pg wanting to use current context it seems - more research needed
	  fill(0, 12);
	  rect(0, 0, width, height);
	  fill(255);
	  noStroke();
	  ellipse(mouseX, mouseY, circSize, circSize);

	  pg.beginDraw();
	  pg.background(102);
	  pg.noFill();
	  pg.stroke(255);
	  pg.ellipse(mouseX-circSize, mouseY-circSize, circSize, circSize);
	  pg.endDraw();
	  colorMode(RGB, 255);
	  for (int iH = 0 ; iH <  matrix.rows ; iH++) {
	    for (int iW = 0 ; iW < matrix.cols ; iW++) {
	      float cp = get( (iW+20), (iH+20));    
	      matrix.setRGB(iW, iH, (int)(red((int) cp)), (int)(green((int) cp)), (int)(blue((int) cp)));
	    }
	  }
	  matrix.refresh();
	}


	//--- Scale a graphic to the main window
	//Note: This was the demo I was in the middle of when the train ride ended - so I have how to use where I was since it is interesting.

	/* To Use: 
	 add just a call to this in runDemo1()
	 Click the button and keep it down - moving it around.  It will scale the graphic.
	 Once you get the graphic the size you want - stop.
	 Then click the mouse around the graphic and it gets updated to the matrix.
	 
	 */
	PGraphics pg;
	PShape bot;
	
	void scaleDemo() {
	  size(640, 360);
	  smooth();
	  background(102);
	  translate(width/2, height/2);
	  float zoom = map(mouseX, 0, mouseY, 0.1f, 4.5f);
	  scale(zoom);
	  shape(bot, -140, -140);
	}

	//--- Add call to anything you like here.
	void runDemo1() {
	  aliDemo();
	  //  scrollGraphicDemo();
	  // scrollWordDemo();
	  //hueDemo();
	}


	void runDemo2() {
	  int tmpMin = 10; //Math.min(MATRIX_COLS,MATRIX_ROWS);
	  for ( int i = 0 ; i < tmpMin ; i++ ) {
	    matrix.allOff();
	    matrix.drawBox(i, 0, matrix.cols - 1, matrix.rows - 1, 255, 0, 0);
	    matrix.refresh();
	    delay(100);
	  }
	}

	int cFrame = 0;
	boolean emulationMode = false;

	void drawHue() {
	  noStroke();
	  colorMode(HSB, colorScale);
	  for (int i = 0; i < colorScale; i++) {
	    for (int j = 0; j < colorScale; j++) {
	      stroke(i, colorScale, j);
	      point(i, j);
	    }
	  }
	}

	void drawRGB() {
	  noStroke();
	  //colorMode(RGB, colorScale);
	  for (int i = 0; i <= colorScale; i++) {
	    for (int j = 0; j <= colorScale; j++) {
	      stroke(255, 0, 0);
	      point(i, j);
	    }
	  }
	}

	//--- Used to load the content of the ledCanvas window to the matrix and refresh (send to matrix and/or emulator)
	void loadFromCanvas() {
	  colorMode(RGB, 255);
	  for (int iH = 0 ; iH < matrix.rows ; iH++) {
	    for (int iW = 0 ; iW < matrix.cols ; iW++) {
	      float cp = ledCanvas.get( (iW), (iH));    
	      matrix.setRGB(iW, iH, (int)(red((int) cp)), (int)(green((int) cp)), (int)(blue((int) cp)));
	    }
	  }
	  matrix.refresh();
	}

	

	class Button
	{
	  int x, y;
	  int size;
	  float basecolor, highlightcolor;
	  float currentcolor;
	  boolean over = false;
	  boolean pressed = false;   

	  void update() 
	  {
	    if (over()) {
	      currentcolor = highlightcolor;
	    } 
	    else {
	      currentcolor = basecolor;
	    }
	  }

	  boolean pressed() 
	  {
	    if (over) {
	      locked = true;
	      return true;
	    } 
	    else {
	      locked = false;
	      return false;
	    }
	  }

	  boolean over() 
	  { 
	    return true;
	  }

	  boolean overRect(int x, int y, int width, int height) 
	  {
	    if (mouseX >= x && mouseX <= x+width && 
	      mouseY >= y && mouseY <= y+height) {
	      return true;
	    } 
	    else {
	      return false;
	    }
	  }

	  boolean overCircle(int x, int y, int diameter) 
	  {
	    float disX = x - mouseX;
	    float disY = y - mouseY;
	    if (sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
	      return true;
	    } 
	    else {
	      return false;
	    }
	  }
	}

	class RectButton extends Button
	{
	  RectButton(int ix, int iy, int isize, float icolor, float ihighlight) 
	  {
	    x = ix;
	    y = iy;
	    size = isize;
	    basecolor = icolor;
	    highlightcolor = ihighlight;
	    currentcolor = basecolor;
	  }

	  boolean over() 
	  {
	    if ( overRect(x, y, size, size) ) {
	      over = true;
	      return true;
	    } 
	    else {
	      over = false;
	      return false;
	    }
	  }

	  void display() 
	  {
	    stroke(255);
	    fill(currentcolor);
	    rect(x, y, size, size);
	  }
	}
}

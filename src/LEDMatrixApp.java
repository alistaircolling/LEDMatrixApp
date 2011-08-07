/*
	 The hookedup LED Controller / Matrix / Emulator / Canvas demo.
	 
	 Copywrite 2011, hookedup, inc. 
	 All rights reserved.
 */

//===================== LED CONTROLLER AND MATRIX CODE

import gifAnimation.Gif;

import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.serial.Serial;
import processing.xml.XMLElement;
import toxi.color.ColorGradient;
import toxi.color.ColorList;
import toxi.color.TColor;
import toxi.math.CosineInterpolation;
import controlP5.ControlEvent;
import controlP5.ControlP5;

public class LEDMatrixApp extends PApplet {

	int MATRIX_COLS = 40;
	int MATRIX_ROWS = 25;
	int MATRIX_CELL_W = 22;
	int MATRIX_CELL_H = 22;
	int MATRIX_CELL_SPACING = 0;
	int MATRIX_EMULATOR_DELAY = 74;
	int CONTROLLER_PORT_SLOT = 10; // or something like -> 2;

	// App Options
	int clickSpread = 1;
	// == Extra windows used to allow display of graphics while in main loop
	ExtraWindow ledCanvas;

	// == The LED Controller sends colors - 1 bytes per color 0-255 range
	// Using a colorScale that matches makes a 1 to 1 mapping so the color data
	// can be sent straight away
	int colorScale = 255;

	// == Various elements pulled in from demos for quick testing of API
	PFont fontA;
	float currentcolor;
	int circSize = 5;

	// == The emulator - abstracted logic for drawing matrix content. Output can
	// go to any context / window.
	MatrixUI ui;

	// == Serial port used for the transfer
	Serial myPort;

	// == The LED Controller
	LEDController ctlLED = new LEDController(1000);

	// == The LED Matrix
	LEDMatrix matrix = new LEDMatrix(MATRIX_COLS, MATRIX_ROWS);

	// controls
	private ControlP5 control;
	public controlP5.Button b;
	private AppConfig config;
	private float temp;
	private float weatherCode;
	private PImage cloudRain;
	private PImage cloudSun;
	private PImage cloud;
	private PImage heavyRain;
	private PImage snow;
	private PImage lightCloud;
	private PImage storm;
	private PImage sun;
	private PImage[] weatherIcons;
	private int[] tubeLines;

	public void setup() {

		// -- There is a monster under the bed
		bot = loadShape("bot1.svg");

		// -- Font - really a simple demo - better text / font stuff around I
		// think
		fontA = loadFont("pfr14.vlw");
		textFont(fontA, 14);

		// -- Used for trailer demo
		pg = createGraphics(80, 80, P2D);

		// -- Set color mode to match the scale used by the LED controller - 256
		// steps per color - 1 byte
		colorMode(RGB, 255);
		// -- Update this window and call loadFromCanvas() to send to matrix
		ledCanvas = new ExtraWindow(this, "Output Canvas", 0,
				((MATRIX_CELL_H + MATRIX_CELL_SPACING) * MATRIX_ROWS),
				(MATRIX_COLS), (MATRIX_ROWS));
		ledCanvas.setUndecorated(true);
		// -- Create a new matrix ui and attach it to the matrix
		ui = new MatrixUI(0, 0, MATRIX_COLS, MATRIX_ROWS, MATRIX_CELL_W,
				MATRIX_CELL_H, MATRIX_CELL_SPACING, this);
		// -- Using setUI enables emulation mode
		// - to run without the emulator - comment out the line below
		matrix.setUI(ui);

		// --- LED Controller setup.
		// For emulation mode - I set the value of CONTROLLER_PORT_SLOT to 10
		// assuming you don't have that many comm ports.
		// this will assure you are always in emulation mode only with no action
		// needed.
		// when you get the controller - set CONTROLLER_PORT_SLOT to the
		// position it is in the serial list (Serial.list()).
		// Note to make this smarter - get the serial port number which should
		// be static and find it in the list by description and then use that
		// number
		if (Serial.list().length > CONTROLLER_PORT_SLOT) {
			// myPort = new Serial(this, Serial.list()[CONTROLLER_PORT_SLOT],
			// 115200);
			// ctlLED.begin(myPort);
			// matrix.setController(ctlLED);
		}
		// --- LED Controller setup END

		// --- Main application
		size(200, 200);
		background(255);
		smooth();
		frameRate(42);

		float baseColor = color(102);

		// set text on ledcanvas
		control = new ControlP5(this);
		control.addButton("Scrolling text", 1, 10, 10, 80, 19);
		control.addButton("Gif demo", 2, 10, 30, 80, 19);
		control.addButton("Weather demo", 3, 10, 50, 80, 19);
		control.addButton("Tube demo", 4, 10, 70, 80, 19);

		// testing only

		// weatherDemo();
		
		
	}

	private void setupWeatherIcons() {
		cloudRain = loadImage("weather/cloudy-with-rain.png");
		cloudSun = loadImage("weather/cloudy-with-sunshine.png");
		cloud = loadImage("weather/cloudy.png");
		heavyRain = loadImage("weather/heavy-rain.png");
		snow = loadImage("weather/heavy-snow.png");
		lightCloud = loadImage("weather/light-cloud.png");
		storm = loadImage("weather/storm.png");
		sun = loadImage("weather/sunny.png");
		weatherIcons = new PImage[3200];

		setIcon(weatherIcons, storm, 0, 5);
		setIcon(weatherIcons, snow, 6, 10);
		setIcon(weatherIcons, cloudRain, 11, 15);
		setIcon(weatherIcons, cloudSun, 16, 25);
		setIcon(weatherIcons, cloud, 26, 38);
		setIcon(weatherIcons, heavyRain, 38, 42);
		setIcon(weatherIcons, lightCloud, 43, 45);
		setIcon(weatherIcons, sun, 45, 50);

	}

	private void setIcon(PImage[] array, PImage img, int startI, int endI) {

		for (int i = startI; i <= endI; i++) {
			array[i] = img;
		}
	}

	public void controlEvent(ControlEvent theEvent) {
		println(theEvent.controller().name() + ":"
				+ theEvent.controller().value());
		switch ((int) theEvent.controller().value()) {
		case 1:
			aliDemo();
			break;
		case 2:
			gifDemo();
			break;
		case 3:
			weatherDemo();
			break;
		case 4:
			tubeDemo();
			break;

		default:
			break;
		}
	}

	private void tubeDemo() {
		
		tubeLines = new int[12];
		tubeLines[0] = 0xb36307;
		tubeLines[1] = 0xe32118;
		tubeLines[2] = 0xf7cc00;
		tubeLines[3] = 0x003788;
		tubeLines[4] = 0x00782b;
		tubeLines[5] = 0x95cdba;
		tubeLines[6] = 0xf3a9bb;
		tubeLines[7] = 0x0098d4;
		tubeLines[8] = 0xa0a5a9;
		tubeLines[9] = 0x9b0056;
		tubeLines[10] = 0x0;
		tubeLines[11] = 0xee7c10;
		
		setCanvasBg(255, 255, 255);
		
		int[] ranAr = generateRandomArray(tubeLines.length, 3);
		
		for (int i = 0; i < tubeLines.length; i++) {
			for (int j = 0; j <= MATRIX_ROWS; j++) {
				
				int targX = (i*3)+3;
				int targY = j;
				TColor col = TColor.BLACK.copy();
				col.setARGB(tubeLines[i]);
				ledCanvas.strokeWeight(3);
//				if (ranAr[i]==2 && j>18){
//					ledCanvas.stroke(50);
//				}else{
					ledCanvas.stroke(round(255*col.red()), round(255*col.green()), round(255*col.blue()));//tubeLines[i]);
					
				//}
				ledCanvas.line(targX, targY, targX, targY+1);
				loadFromCanvas();
				delay(10);
			}
		}
		
		ledCanvas.fill(255);
		ledCanvas.textFont(fontA, 8);
		ledCanvas.textSize(8);
		ledCanvas.text("TUBES", 5, 16);
		loadFromCanvas();
	}

	private int[] generateRandomArray( int size, int range) {
		
		int[] returnAr = new int[size];
		for (int i = 0; i < returnAr.length; i++) {
			returnAr[i] = round(random(0, range));
		}
		return returnAr;
	}

	private void weatherDemo() {
		fillBGGrad(TColor.BLACK.copy(),TColor.BLUE.copy());
		loadFromCanvas();
		setupWeatherIcons();
		ledCanvas.fill(255);
		ledCanvas.textFont(fontA, 12);
		ledCanvas.textSize(12);
		ledCanvas.text("Today", 1, 16);
		
		loadFromCanvas();

		String url = "http://weather.yahooapis.com/forecastrss?p=UKXX0085&u=c";
		XMLElement rss = new XMLElement(this, url);
		delay(500);
		XMLElement[] today = rss.getChildren("channel/item");
		XMLElement todayChildren = today[0];

		for (int i = 0; i < todayChildren.getChildCount(); i++) {
			XMLElement child = todayChildren.getChild(i);
			String childName = child.getName();
			if (childName.compareTo("yweather:condition") == 0) {
				temp = child.getFloatAttribute("temp");
				weatherCode = child.getFloatAttribute("code");
			}
		}
		fillBGGrad(TColor.WHITE.copy(), TColor.BLUE.copy());

		int ranWeather = (int) random(0, 50);
		PImage icon = weatherIcons[ranWeather];
		ledCanvas.image(icon, 18, 2, 16, 16);
		ledCanvas.fill(0);
		ledCanvas.textFont(fontA, 12);
		ledCanvas.textSize(12);
		ledCanvas.textLeading(18);
		ledCanvas.text(round(temp), 2, 12);
		ledCanvas.text("¡c", 2, 24);
		loadFromCanvas();

	}

	private void fillBGGrad( TColor from, TColor to) {
		ColorGradient grad = new ColorGradient();
		grad.setInterpolator(new CosineInterpolation());
		grad.addColorAt(0, from);
		grad.addColorAt(10, to);
		grad.addColorAt(25, to);
		ColorList l = grad.calcGradient(0, 25);
		int y = 0;
		for (Iterator i = l.iterator(); i.hasNext();) {
			TColor c = (TColor) i.next();
			ledCanvas.stroke(c.toARGB());
			ledCanvas.line(0, y, 40, y);
			y++;
		}
	}

	private void showText() {

		// ledCanvas.stroke(0);
		ledCanvas.fill(0);
		ledCanvas.textFont(fontA, 8);
		ledCanvas.text("Hello!", 5, 24);
		loadFromCanvas();

	}

	private void gifDemo() {
		setCanvasBg(255, 255, 255);
		showGif("time.gif", 40, 1, 24, 8, 1);
		delay(500);
		setCanvasBg(255, 255, 255);
		loadFromCanvas();
		showGif("eye.gif", 100, 3, 26, 8, 0);
		setCanvasBg(255, 255, 255);
		loadFromCanvas();
		setCanvasBg(0, 0, 0);
		showGif("letters.gif", 100, 1, 24, 8, 0);
		delay(500);
		setCanvasBg(255, 255, 255);
		showGif("truck.gif", 30, 10, 30, 5, -2);
		delay(500);
		setCanvasBg(255, 255, 255);

	}

	private void showGif(String string, int delayTime, int l, int size,
			int xPos, int yPos) {
		PImage[] gifFrames = Gif.getPImages(this, string);
		int loops = l; // can be used for repeating animation
		for (int i = 0; i < gifFrames.length * loops; i++) {
			setCanvasBg(255, 255, 255);
			ledCanvas.image(gifFrames[i % gifFrames.length], xPos, yPos, size,
					size);
			loadFromCanvas();
			delay(delayTime);
		}

	}

	private void setCanvasBg(int r, int g, int b) {
		ledCanvas.noStroke();
		ledCanvas.fill(r, g, b);
		ledCanvas.rect(0, 0, MATRIX_COLS, MATRIX_ROWS);

	}

	private void shapesDemo() {

		ledCanvas.fill(0);
		ledCanvas.noStroke();
		ledCanvas.rect(0, 0, MATRIX_COLS, MATRIX_ROWS);
		loadFromCanvas();
		delay(100);
		for (int i = 1; i < 41; i++) {

			ledCanvas.fill(random(255), random(255), random(255));
			ledCanvas.noStroke();
			ledCanvas.noSmooth();
			int high = i;
			int wide = i;
			println(high + ":" + wide);
			ledCanvas.rect(round((MATRIX_COLS * .5f) - (wide / 2)) - 1,
					round((MATRIX_ROWS * .5f) - (high / 2)), wide, high);
			loadFromCanvas();
			delay(150);
		}

	}

	public void draw() {
		stroke(255);
		update(mouseX, mouseY);

		// --- Turn on below to see trailer demo. Move mouse over the top left
		// area of the main form (about 20 pixels in) and see it update on the
		// matrix using draw as timer
		// demoDraw();
	}

	void update(int x, int y) {

	}

	// /////ANIMATION CODE

	// == Shows the entire matrix flashing on and off .. with 1000 LEDS - use
	// some sunglasses B-)
	void strobeOnOff(int theTimes) {
		for (int i = 0; i < theTimes; i++) {
			matrix.allTo(0, 0, 0);
			matrix.refresh();
			matrix.allTo(255, 255, 255);
			matrix.refresh();
		}
	}

	// == Some vector test patters.
	// *** NOTE: Drawline and box may be removed from the matrix class. If you
	// use it let me know and I won't.
	void sweepWidth() {
		for (int j = 0; j < matrix.cols; j++) {
			matrix.allOff();
			matrix.drawLine(j, 0, j, matrix.rows - 1, 255, 255, 255);
			matrix.refresh();
		}
	}

	// *** NOTE: Drawline and box slated for removal.
	void sweepHeight() {
		for (int j = 0; j < matrix.rows; j++) {
			matrix.allOff();
			matrix.drawLine(0, j, matrix.cols - 1, j, 255, 255, 255);
			matrix.refresh();
		}
	}

	// *** NOTE: Drawline and box slated for removal.
	void vectorDemo() {
		sweepHeight();
		for (int j = 0; j < 3; j++) {

			for (int i = 0; i < MATRIX_COLS / 2; i++) {
				matrix.allOff();
				matrix.drawBox(i, i, matrix.cols - (i + 1), matrix.rows
						- (i + 1), 255, 0, 0);
				matrix.refresh();
			}
			for (int i = MATRIX_COLS / 2; i > 0; i--) {
				matrix.allOff();
				matrix.drawBox(i, i, matrix.cols - (i + 1), matrix.rows
						- (i + 1), 255, 255, 0);
				matrix.refresh();
			}
		}
		sweepWidth();
	}

	// --- This draws a hue based pallet to the main window .. twice. Very odd
	// but if not it would show up lighter. Not sure the root cause of this.
	// I find the same issue with get of pixel. The RGB color value returned are
	// not zero when they should be - like the color is saturated.
	// remove one of the drawHue() calls below to see what I mean when you have
	// the live matrix or by exporting / reviewing color output numbers

	// --- To use - click the mouse and the matrix updates using a box starting
	// at the mouse coordinates as 0,0
	// * any graphic method can be used to update the main window and the mouse
	// click will send to the matrix
	void hueDemo() {
		drawHue();
		drawHue();
	}

	int cImage = 0;

	// --- This demo shows the process or loading and images ans scrolling it
	// --- This can just run a loop and not have to coordinator with draw in any
	// way.
	// --- The loadFromCanvas() call loads the "screen shot" from the canvas
	// window to the matrix
	void scrollGraphicDemo() {
		PImage myImage;
		if (cImage == 0) {
			cImage = 1;
			myImage = loadImage("test001.jpg");
		} else {
			cImage = 0;
			myImage = loadImage("test002.jpg");
		}
		for (int i = 0; i > 0 - myImage.width; i--) {
			ledCanvas.image(myImage, i, 0);
			loadFromCanvas();
		}
	}

	// --- This demo shows the process of loading text and scrolling it across
	// the matrix
	void scrollWordDemo() {
		// ledCanvas.fill(0);
		ledCanvas.textFont(fontA, 24);
		ledCanvas.smooth();

		for (int i = MATRIX_COLS; i > 0 - MATRIX_COLS - 30; i--) {
			// ledCanvas.background(102);
			ledCanvas.stroke(0);
			ledCanvas.text("Hello", i, 9);
			loadFromCanvas();
		}
	}

	void aliDemo() {

		for (int i = MATRIX_COLS; i > 0 - MATRIX_COLS - 40; i--) {
			ledCanvas.fill(133, 255, 255);
			ledCanvas.noStroke();
			ledCanvas.rect(0, 0, matrix.cols, matrix.rows);
			ledCanvas.stroke(0);
			ledCanvas.fill(0);
			ledCanvas.textFont(fontA, 14);
			ledCanvas.smooth();
			ledCanvas.text("Hello KND!", i, 15);
			PImage textImg = new PImage();

			loadFromCanvas();
			delay(100);
		}
	}

	void demoDraw() {
		// --- Add a call to demoDraw() in draw() to see "trails" on the main
		// window update.

		// To move this effect to another window - a subclass is needed most
		// likely ..
		// due to pg wanting to use current context it seems - more research
		// needed
		fill(0, 12);
		rect(0, 0, width, height);
		fill(255);
		noStroke();
		ellipse(mouseX, mouseY, circSize, circSize);

		pg.beginDraw();
		pg.background(102);
		pg.noFill();
		pg.stroke(255);
		pg.ellipse(mouseX - circSize, mouseY - circSize, circSize, circSize);
		pg.endDraw();
		colorMode(RGB, 255);
		for (int iH = 0; iH < matrix.rows; iH++) {
			for (int iW = 0; iW < matrix.cols; iW++) {
				float cp = get((iW + 20), (iH + 20));
				matrix.setRGB(iW, iH, (int) (red((int) cp)),
						(int) (green((int) cp)), (int) (blue((int) cp)));
			}
		}
		matrix.refresh();
	}

	// --- Scale a graphic to the main window
	// Note: This was the demo I was in the middle of when the train ride ended
	// - so I have how to use where I was since it is interesting.

	/*
	 * To Use: add just a call to this in runDemo1() Click the button and keep
	 * it down - moving it around. It will scale the graphic. Once you get the
	 * graphic the size you want - stop. Then click the mouse around the graphic
	 * and it gets updated to the matrix.
	 */
	PGraphics pg;
	PShape bot;

	void scaleDemo() {
		size(640, 360);
		smooth();
		background(102);
		translate(width / 2, height / 2);
		float zoom = map(mouseX, 0, mouseY, 0.1f, 4.5f);
		scale(zoom);
		shape(bot, -140, -140);
	}

	// --- Add call to anything you like here.
	void runDemo1() {
		aliDemo();
		// scrollGraphicDemo();
		// scrollWordDemo();
		// hueDemo();
	}

	void runDemo2() {
		int tmpMin = 10; // Math.min(MATRIX_COLS,MATRIX_ROWS);
		for (int i = 0; i < tmpMin; i++) {
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
		// colorMode(RGB, colorScale);
		for (int i = 0; i <= colorScale; i++) {
			for (int j = 0; j <= colorScale; j++) {
				stroke(255, 0, 0);
				point(i, j);
			}
		}
	}

	// --- Used to load the content of the ledCanvas window to the matrix and
	// refresh (send to matrix and/or emulator)
	void loadFromCanvas() {
		colorMode(RGB, 255);
		for (int iH = 0; iH < matrix.rows; iH++) {
			for (int iW = 0; iW < matrix.cols; iW++) {
				float cp = ledCanvas.get((iW), (iH));
				matrix.setRGB(iW, iH, (int) (red((int) cp)),
						(int) (green((int) cp)), (int) (blue((int) cp)));
			}
		}
		matrix.refresh();
	}

}

import processing.core.PApplet;
import processing.serial.Serial;
import sun.java2d.loops.DrawRect;
import sun.tools.tree.UnsignedShiftRightExpression;
import toxi.color.TColor;
import toxi.geom.Vec2D;

class LEDController extends PApplet {
	private static final int LEDS_WIDE = 40;
	private static final int LEDS_TALL = 25;
	public int LED_COUNT;
	private int LED_BULB_COUNT;
	private int LED_SEND_BUFFER_SIZE;
	private int LED_PAGES;

	Serial sp;

	byte ledBuffer[];
	byte sendBuffer[];

	PApplet app;

	LEDController(int ledCount, PApplet a) {
		app = a;
		LED_COUNT = ledCount;
		LED_BULB_COUNT = (LED_COUNT * 3);
		LED_SEND_BUFFER_SIZE = 1000;
		LED_PAGES = (LED_BULB_COUNT / LED_SEND_BUFFER_SIZE);

		ledBuffer = new byte[LED_BULB_COUNT]; //
		sendBuffer = new byte[LED_SEND_BUFFER_SIZE];
	}

	public void begin(Serial theSerialPort) {
		sp = theSerialPort;
		clearBuffers();
	}

	public void setAllTo(int theR, int theG, int theB) {
		for (int i = 0; i < LED_COUNT; i++) {
			setRGB(i, theR, theG, theB);
		}
	}

	void SetAllOff() {
		setAllTo(0, 0, 0);
	}

	void clearBuffers() {
		for (int i = 0; i < LED_BULB_COUNT; i++) {
			ledBuffer[i] = 0;
		}
		for (int i = 0; i < LED_SEND_BUFFER_SIZE; i++) {
			sendBuffer[i] = 0;
		}
	}

	public void setRGB(int thePos, int theR, int theG, int theB) {

		int tmpStart = thePos * 3;
		// println("r:"+theR);
		ledBuffer[tmpStart] = (byte) theR;
		ledBuffer[tmpStart + 1] = (byte) theB;
		ledBuffer[tmpStart + 2] = (byte) theG;

	}

	int getXY(int x, int y) {
		if (y % 2 > 0) {
			return (((y + 1) * width) - x - 1);
		} else {
			return ((y * width) + x);
		}
	}

	public void display() {
	//	println("display");
		// app.background(255, 0, 234);
		for (int i = 0; i < ledBuffer.length-3; i += 3) {
			
			if (i % 3 == 0 && i < (ledBuffer.length - 3)) {
				
				int r = unsignedByteToInt(ledBuffer[i]);
				int g = unsignedByteToInt(ledBuffer[i+2]);
				int b = unsignedByteToInt(ledBuffer[i+1]);
		//		println("R:"+r+"  g:"+g+"  b:"+b);
				app.noStroke();
			//	app.fill(unsignedByteToInt(ledBuffer[i - 2]) , unsignedByteToInt(ledBuffer[i - 1]) , unsignedByteToInt(ledBuffer[i - 0]));
				app.fill(r, g, b);
				int theX = (i%(LEDS_WIDE*3))/3;
				int theY = floor(((i-theX)/3)/LEDS_WIDE);
				app.rect(theX*10, theY*10, 10, 10);
			}
		}
		// app.background(255, 0, 234);
//		for (int i = 0; i < ledBuffer.length; i += 3) {
//
//			if (i % 3 == 0 && i < (ledBuffer.length - 3) && i > 0) {
//				
//				app.noStroke();
//				app.fill(unsignedByteToInt(ledBuffer[i - 2]) , unsignedByteToInt(ledBuffer[i - 1]) , unsignedByteToInt(ledBuffer[i - 0]));
//				
//				int theX = (i%(LEDS_WIDE*3))/3;
//				int theY = floor(((i-theX)/3)/LEDS_WIDE);
//				app.rect(theX*10, theY*10, 10, 10);
//			}
//		}
	}

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}
	
	Vec2D getXYFromPos(int pos) {

		Vec2D vec = new Vec2D();
		vec.x = pos % LEDS_WIDE;
		int row = floor((pos-vec.x)/LEDS_WIDE); 
		vec.y = row;
		return vec;

	}

	void SendLEDs() {
		for (int i = 0; i < LED_BULB_COUNT; i += LED_SEND_BUFFER_SIZE) {
			for (int j = 0; j < LED_SEND_BUFFER_SIZE; j++) {
				sendBuffer[j] = ledBuffer[i + j];
//
//				//println(j + "this j:" + unsignedByteToInt(sendBuffer[j]));
//				if (j % 3 == 0 && j < (LED_SEND_BUFFER_SIZE - 3) && j > 0) {
//					
//					
//					app.noStroke();
//				//	app.fill(unsignedByteToInt(sendBuffer[j - 3]) ,
//					//		 unsignedByteToInt(sendBuffer[j - 2]) ,
//						//	 unsignedByteToInt(sendBuffer[j - 1]));
//					int r = unsignedByteToInt(sendBuffer[j - 3]); 
//					int g = unsignedByteToInt(sendBuffer[j - 2]);
//					int b = unsignedByteToInt(sendBuffer[j - 1]);
//					
//					app.fill(r, g, b);
//					app.rect(random(0, 40), random(0, 25), 1, 1);
//					//println("j:" + j);
//					
//					
//				}

			}
			sp.write(sendBuffer);
		}
	}
}

import processing.core.PApplet;
import processing.serial.Serial;
import sun.java2d.loops.DrawRect;

class LEDController extends PApplet {
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
		LED_SEND_BUFFER_SIZE = 500;
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

	public void fakeDraw(int x, int y, int theR, int theG, int theB) {

		app.stroke(kolor(theR, theG, theB));
		app.fill(kolor(theR, theG, theB));
		app.rect(x, y, 10, 10);
		// TODO Auto-generated method stub

	}

	int kolor(int r, int g, int b) {
		return ((b & 255 & 0xC0) + ((g & 255 & 0xE0) >> 2) + ((r & 0xE0) >> 5)) & 0xFF;
	}

	public void display() {
		// TODO Auto-generated method stub
		// app.background(255, 0, 234);

		int theR = 255;
		int theG = 0;
		int theB = 240;
		app.stroke((int)kolor(theR, theG, theB));
		app.fill(kolor(theR, theG, theB));
		app.rect(0, 0, 100, 100);
		
		// super.draw();
		//println("draw");
	}

	void SendLEDs() {
		for (int i = 0; i < LED_BULB_COUNT; i += LED_SEND_BUFFER_SIZE) {
			for (int j = 0; j < LED_SEND_BUFFER_SIZE; j++) {
				sendBuffer[j] = ledBuffer[i + j];
			}
			sp.write(sendBuffer);
		}
	}
}

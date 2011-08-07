import processing.core.PApplet;
import processing.serial.Serial;

/*
 LEDController Class 
 
 Copywrite 2011, hookedup, inc. 
 All rights reserved.
 */

class LEDController extends PApplet{
	public int LED_COUNT;
	private int LED_BULB_COUNT;
	private int LED_SEND_BUFFER_SIZE;
	private int LED_PAGES;
	

	Serial sp;

	byte ledBuffer[];
	byte sendBuffer[];

	LEDController(int ledCount) {
		LED_COUNT = ledCount;
		LED_BULB_COUNT = (LED_COUNT * 3);
		LED_SEND_BUFFER_SIZE = 500;
		LED_PAGES = (LED_BULB_COUNT / LED_SEND_BUFFER_SIZE);

		ledBuffer = new byte[LED_BULB_COUNT]; //
		sendBuffer = new byte[LED_SEND_BUFFER_SIZE];
	}

	void begin(Serial theSerialPort) {
		sp = theSerialPort;
		clearBuffers();
	}

	void setAllTo(int theR, int theG, int theB) {
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

	void setRGB(int thePos, int theR, int theG, int theB) {
		int tmpStart = thePos * 3;
		ledBuffer[tmpStart] = (byte) theR;
		ledBuffer[tmpStart + 1] = (byte) theB;
		ledBuffer[tmpStart + 2] = (byte) theG;
	}

	byte getR(int thePos) {
		int tmpPos = thePos * 3;
		return ledBuffer[tmpPos];
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




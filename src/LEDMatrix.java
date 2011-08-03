import processing.core.PApplet;
import processing.serial.Serial;

import processing.*;

//

class LEDMatrix extends PApplet {

	// The LED Controller to use with this matrix
	// LEDController ledC;
	// The size of the matrix
	int width = 5;
	int height = 4;
	int matStart = 0;

	Serial myPort;
	LEDController lc;// = new LEDController(1000);

	// LEDMatrix matrix = new LEDMatrix(lc, 5, 4);

	LEDMatrix(LEDController theLEDController, int theW, int theH) {
		width = theW;
		height = theH;
		lc = theLEDController;
		matStart = 0;
	}

	int getXY(int x, int y) {
		if (y % 2 > 0) {
			return (((y + 1) * width) - x - 1);
		} else {
			return ((y * width) + x);
		}
	}

	boolean drawLine(int thex0, int they0, int thex1, int they1, int r, int g,
			int b) {
		boolean steep;

		steep = Math.abs(they1 - they0) > Math.abs(thex1 - thex0);

		IntHolder x0h = new IntHolder(thex0);
		IntHolder x1h = new IntHolder(thex1);
		IntHolder y0h = new IntHolder(they0);
		IntHolder y1h = new IntHolder(they1);

		if (steep) {
			swapInts(x0h, y0h);
			swapInts(x1h, y1h);
		}
		if (x0h.val > x1h.val) {
			swapInts(x0h, x1h);
			swapInts(y0h, y1h);
		}
		int x0 = x0h.val;
		int x1 = x1h.val;
		int y0 = y0h.val;
		int y1 = y1h.val;

		int deltax = x1 - x0;
		int deltay = Math.abs(y1 - y0);
		int error = 0;
		int ystep;
		int y = y0;
		int x;
		if (y0 < y1)
			ystep = 1;
		else
			ystep = -1;
		for (x = x0; x <= x1; x++) // from x0 to x1
		{
			if (steep) {
				lc.setRGB(getXY(y, x), r, g, b);
				lc.fakeDraw(x, y, r, g, b);
			} else {
				lc.setRGB(getXY(x, y), r, g, b);
				lc.fakeDraw(x, y, r, g, b);
			}
			error = error + deltay;
			if (2 * error >= deltax) {
				y = y + ystep;
				error = error - deltax;
			}
		}
		return true;
	}

	// Swap the values of two variables, for use when drawing lines.
	void swapInts(IntHolder a, IntHolder b) {
		int temp;
		temp = b.val;
		b.val = a.val;
		a.val = temp;
	}

	void drawBox(int x0, int y0, int x1, int y1, int r, int g, int b) {
		drawLine(x0, y0, x1, y0, r, g, b);
		drawLine(x1, y0, x1, y1, r, g, b);
		drawLine(x1, y1, x0, y1, r, g, b);
		drawLine(x0, y1, x0, y0, r, g, b);
	}

	void allTo(int r, int g, int b) {
		lc.setAllTo(r, g, b);
	}

	void allOff() {
		allTo(0, 0, 0);
	}

	void allOn() {
		allTo(255, 255, 255);
	}

	void refresh() {
		lc.SendLEDs();
	}

	void strobeOnOff(int theTimes) {
		for (int i = 0; i < theTimes; i++) {
			lc.setAllTo(0, 0, 0);
			lc.SendLEDs();
			lc.setAllTo(255, 255, 255);
			lc.SendLEDs();
		}
	}

	void sweepWidth() {
		for (int j = 0; j < width; j++) {
			allOff();
			drawLine(j, 0, j, height - 1, 255, 255, 255);
			refresh();
		}
	}

	void sweepHeight() {
		for (int j = 0; j < height; j++) {
			allOff();
			drawLine(0, j, width - 1, j, 255, 255, 255);
			refresh();
		}
	}

	public void runDemo1() {
		strobeOnOff(10);
	}

	public void runDemo2() {
		sweepWidth();
		sweepHeight();

		for (int i = 0; i < 10; i++) {

			allOff();
			drawBox(0, 0, width - 1, height - 1, 255, 0, 0);
			refresh();
			delay(200);
			allOff();
			drawBox(1, 1, width - 2, height - 2, 0, 255, 0);
			refresh();
			delay(200);
			allOff();
			drawBox(0, 0, width - 1, height - 1, 255, 255, 0);
			drawBox(1, 1, width - 2, height - 2, 255, 0, 255);
			refresh();
			delay(200);
		}

	}

	// inner class to handle passing int by reference
	class IntHolder {
		int val;

		IntHolder(int theVal) {
			val = theVal;
		}
	}
}

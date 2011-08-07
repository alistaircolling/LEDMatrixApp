import processing.core.PApplet;

class LEDMatrix extends PApplet {

	// inner class to handle passing int by reference
	class IntHolder {
		int val;

		IntHolder(int theVal) {
			val = theVal;
		}
	}

	private static final int MATRIX_EMULATOR_DELAY = 5;

	// The LED Controller to use with this matrix
	LEDController lc;
	MatrixUI ui;

	boolean useUI;
	boolean useController;

	// The size of the matrix
	int cols = 5;
	int rows = 4;
	int matStart = 0;
	int values[][][];

	// boolean emulationOnlyMode = false;

	LEDMatrix(int theW, int theH) {
		cols = theW;
		rows = theH;
		matStart = 0;
		useUI = false;
		useController = false;
		values = new int[theW][theH][3];
	}

	void setController(LEDController theLEDController) {
		lc = theLEDController;
		useController = true;
	}

	void setUI(MatrixUI theUI) {
		ui = theUI;
		useUI = true;
	}

	int getXY(int x, int y) {
		if (y % 2 > 0) {
			return (((y + 1) * cols) - x - 1);
		} else {
			return ((y * cols) + x);
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

			if (steep)
				setRGB(y, x, r, g, b);
			else
				setRGB(x, y, r, g, b);

			error = error + deltay;
			if (2 * error >= deltax) {
				y = y + ystep;
				error = error - deltax;
			}
		}
		return true;
	}

	void setRGB(int theX, int theY, int theR, int theG, int theB) {
		values[theX][theY][0] = theR;
		values[theX][theY][1] = theG;
		values[theX][theY][2] = theB;
	}

	int getR(int theX, int theY) {
		return values[theX][theY][0];
	}

	int getG(int theX, int theY) {
		return values[theX][theY][1];
	}

	int getB(int theX, int theY) {
		return values[theX][theY][2];
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
		for (int iW = 0; iW < cols; iW++) {
			for (int iH = 0; iH < rows; iH++) {
				setRGB(iW, iH, r, g, b);
			}
		}
	}

	void allOff() {
		allTo(0, 0, 0);
	}

	void allOn() {
		allTo(255, 255, 255);
	}

	void refreshUI() {
		for (int iH = 0; iH < rows; iH++) {
			for (int iW = 0; iW < cols; iW++) {
				ui.cells[iW][iH].setRGB(getR(iW, iH), getG(iW, iH),
						getB(iW, iH));
			}
		}
		ui.display();
		// refreshController();
	}

	void refresh() {
		if (useUI) {
			refreshUI();
		}
		if (!useController) {

			delay(MATRIX_EMULATOR_DELAY);
		} else {
			int tmpRow = 0;
			int tmpCol = 0;
			// --- load emulator from data

			// print(ui.cellCount);
			// load controller
			int tmpLED = 0;
			for (int iH = 0; iH < rows; iH++) {
				for (int iW = 0; iW < cols; iW++) {
					if (iH % 2 > 0) {
						int tmpW = cols - iW - 1;
						// int tmpH = iH;
						lc.setRGB(tmpLED, (byte) getR(tmpW, iH), (byte) getG(
								tmpW, iH), (byte) getB(tmpW, iH));
					} else {
						lc.setRGB(tmpLED, (byte) getR(iW, iH), (byte) getG(iW,
								iH), (byte) getB(iW, iH));
					}
					tmpLED++;
				}
			}

			lc.SendLEDs();
		}
	}
}
import processing.core.PApplet;

/**
 * MatrixUI Class
 * 
 * Copywrite 2011, hookedup, inc. All rights reserved.
 */

class MatrixUI extends PApplet{
	int posX, posY, rows, cols, cellW, cellH, cellSpacing;
	int cellCount;
	MatrixCell cells[][];
	ExtraWindow matrixWin;
	public PApplet app;
	int MATRIX_COLS = 40;
	int MATRIX_ROWS = 25;
	int MATRIX_CELL_W = 22;
	int MATRIX_CELL_H = 22;
	int MATRIX_CELL_SPACING = 0;

	MatrixUI(int thePosX, int thePosY, int theCols, int theRows, int theCellH,
			int theCellW, int theCellSpacing, PApplet theApp) {
		begin(thePosX, thePosY, theCols, theRows, theCellH, theCellW,
				theCellSpacing, theApp);
	}

	void display() {
		app.noFill();
		app.strokeWeight(2);
		app.stroke(155, 153);
		for (int iW = 0; iW < cols; iW++) {
			for (int iH = 0; iH < rows; iH++) {
				cells[iW][iH].display();
			}
		}
	}

	void begin(int thePosX, int thePosY, int theCols, int theRows,
			int theCellH, int theCellW, int theCellSpacing, PApplet theApp) {
		app = theApp;
		matrixWin = new ExtraWindow(app, "" + MATRIX_COLS + " x " + MATRIX_ROWS
				+ " Matrix Emulator", 0, 0,
				((MATRIX_CELL_W + MATRIX_CELL_SPACING) * MATRIX_COLS),
				((MATRIX_CELL_H + MATRIX_CELL_SPACING) * MATRIX_ROWS));
		matrixWin.smooth();
		// optional -->
		matrixWin.setUndecorated(true);

		posX = thePosX;
		posY = thePosY;
		rows = theRows;
		cols = theCols;
		cellW = theCellW;
		cellH = theCellH;
		cellSpacing = theCellSpacing;
		cellCount = cols * rows;

		cells = new MatrixCell[cols][rows];

		for (int iW = 0; iW < cols; iW++) {
			for (int iH = 0; iH < rows; iH++) {
				cells[iW][iH] = new MatrixCell(posX
						+ ((cellW + cellSpacing) * iW), posY
						+ ((cellH + cellSpacing) * iH), cellW, cellH, this);
			}
		}
	}
}

/**
 * MatrixCell Class
 * 
 * Copywrite 2011, hookedup, inc. All rights reserved.
 */
class MatrixCell {
	float x, y; // X-coordinate, y-coordinate
	int posX, posY; // X-coordinate, y-coordinate
	int r, g, b; // X-coordinate, y-coordinate
	int h, w; // height, width
	MatrixUI parent;

	MatrixCell(float xpos, float ypos, int theH, int theW, MatrixUI theParent) {
		start(xpos, ypos, theH, theW, theParent);
	}

	void start(float xpos, float ypos, int theH, int theW, MatrixUI theParent) {
		parent = theParent;

		x = xpos;
		y = ypos;
		h = theH;
		w = theW;

		r = 0;
		g = 0;
		b = 0;
	}

	void setRGB(int theR, int theG, int theB) {
		r = theR;
		g = theG;
		b = theB;
	}

	void display() {
		parent.matrixWin.fill(r, g, b);
		parent.matrixWin.rect(x, y, h, w);
	}
}
